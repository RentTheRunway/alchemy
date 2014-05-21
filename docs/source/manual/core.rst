.. _man-core:

############
Alchemy Core
############

.. highlight:: text

.. rubric:: The ``alchemy-core`` module is the core module required to create and configure A/B experiments and to query which treaments a given identity is assigned to.

Terminology
===========

* ``Treatment`` - Represents a possible user experience or outcome of an experiment
* ``Identity`` - A unique representation of an entity that is mapped to a random treatment in an experiment, such as a user
* ``Segment`` - A category to which an identity belongs to, which is used for filtering.
* ``Bin`` - a numbered bucket which is assigned a treatment and to which identities are mapped to in equal distribution
* ``Allocation`` - Represents a contiguous block of bins assigned to a treatment
* ``Allocations`` - A collection of allocations that defines which bins are assigned to which treatments
* ``TreatmentOverride`` - Assignment of a specific treatment to a specific identity, which overrides allocations
* ``Experiment`` - A collection of treatments, allocations, and overrides

Identities
==========
All identities generate a hash. What varies from identity to identity are the elements being hashed.  Identities also generate a list of segments that describe and categorize the given identity.  For example, one could have a ``User`` identity with an identifying value ``id``.  When ``id`` is null, ``anonymous_user`` could be returned as a segment, otherwise ``identified_user``.

Implementing a custom identity
==============================
New identities can be implemented as needed.  You need to extend the Identity class and implement ``computeHash()``.  Optionally, you can also override ``computeSegments()`` and add the ``@Segments`` annotation if the identity type can be categorized into different segments:

.. code-block:: java

    @Segments({
        "anonymous_person",
        "identified_person"
    })
    public class FullName extends Identity {
        private final String firstName;
        private final String lastName;

        public FullName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        @Override
        public long computeHash(int seed, Set<String> segments) {
            return
                identity(seed)
                    .putString(firstName)
                    .putString(lastName)
                    .hash();
        }

        // Optional
        @Override
        public Set<String> computeSegments() {
            return segments(firstName == null && lastName == null ? "anonymous_person" : "identified_person");
        }
    }

It is recommended that the built-in hash builder be used by calling ``identity()`` with the seed value and then specifying the individual fields to be used to generate the hash.  If your identity contains an object reference to another class, you can have it implement Identity as well, and add it to the hash builder to propagate building the hash down the object hierarchy.  The current implementation of the hash builder uses mumur_128 to ensure good distribution and few collisions.
The ``@Segments`` annotation is used to identify what possible segments the identity can generate in ``computeSegments``.  If other segments values are return, they are ignored.

If you wish to also make the Identity usable from Alchemy Service, you will need to implement a matching DTO and a mapper.  To implement the DTO, simply extend from IdentityDTO:

.. code-block:: java

    public class FullNameDto extends IdentityDto {
        private final String firstName;
        private final String lastName;

        public FullName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

Lastly, you will need to implement a mapper that maps to/from your identity DTO and business object:

.. code-block:: java

    public class FullNameMapper implements Mapper<FullNameDto, FullName> {
        @Override
        public FullNameDto toDto(FullName source) {
            return new FullNameDto(source.getFirstName(), source.getLastName());
        }

        @Override
        public FullName fromDto(FullNameDto source) {
            return new FullName(source.getFirstName(), source.getLastName());
        }
    }

Composite Identities
====================
A composite identity is an identity that is composed of two or more underlying identities and may have complex criteria for which underlying identity contributes to the computed hash.
As an example, suppose you have an identity that represents a ``device`` with an id and an identity that represents as ``user`` with an id.  A composite identity could contain both, since both of these pieces of information may be available at the same time.
Next, suppose at first, the user is anonymous, so you opt to use the hash code generated from ``device``. At a later time when the user logs in and is then identified, you may want to continue to use ``device``.  For another experiment, you
may want to hash on ``user`` instead in this case, so how can you use the same composite identity but different hashing behavior?  You can solve this problem by utilizing the fact that computeHash() is passed the segments that are being requested
by an experiment and then change the behavior accordingly.  For example:

.. code-block:: java

    @Segments(
        value = { Composite.SEGMENT_USER, Composite.SEGMENT_DEVICE },
        identities = { User.class, Device.class }
    )
    public class Composite extends Identity {
        public static final String SEGMENT_USER = "user";
        public static final String SEGMENT_DEVICE = "device";
        private final User user;
        private final Device device;

        public Composite(User user, Device device) {
            this.user = user;
            this.device = device;
        }

        public User getUser() {
            return user;
        }

        public Device getDevice() {
            return device;
        }

        @Override
        public long computeHash(int seed, Set<String> segments) {
            // we want to compute a hash based on what segment is preferred (user vs device)
            // we'll say that 'user' supersedes 'device', such that if only 'user' is specified, we hash user,
            // if only device is specified, we hash device, if both are specified, we hash user.  If neither are
            // specified, we can return whichever is not null first user vs device

            if (segments.contains(SEGMENT_USER)) { // "user" or "both" were requested
                return user.computeHash(seed, segments);
            } else if (segments.contains(SEGMENT_DEVICE)) { // "device" was requested
                return device.computeHash(seed, segments);
            }

            // neither was requested, default to whatever the most specifically identifying value we can return is
            if (user != null) {
                return user.computeHash(seed, segments);
            }

            if (device != null) {
                return device.computeHash(seed, segments);
            }

            return 0;
        }

        @Override
        public Set<String> computeSegments() {
            return
                Sets.union(
                    // NOTE: it's safe to pass null to the segments(...) utility function
                    segments(
                        user != null ? "user" : null,
                        device != null ? "device" : null
                    ),
                    Sets.union(
                        segments(user),
                        segments(device)
                    )
                );
        }
    }

The main thing to note is that we check what segments are being requested to determine the behavior of the hash function.  The composite identity also needs to specify what the possible segment values it can return are.
We can specify this by including the ``identities`` value in the ``@Segments`` annotation, which will then automatically include the segments that those identities return.

Implementing a custom database provider
=======================================
In alchemy, the regular CRUD methods and querying what treatment an identity is assigned to is separated into two storage components: ``ExperimentStore`` and ``ExperimentCache``.
An ``Experiment`` object contains all the data it needs to define an experiment, treatments, and which identities are assigned to which users.  As a result, all CRUD operations
in ``ExperimentStore`` are on the ``Experiment`` object level.  The ``ExperimentCache`` object is responsible for being able to quickly fetch a cached copy of only active ``Experiment`` objects.  It must always be highly performant.
A ``ExperimentDatabaseProvider`` is a simple factory for creating the store and cache, given some common configuration, since generally, the cache must load experiments from the same place as the store.
The ``alchemy-db-memory`` module contains an example implementation of a database provider that features a cache and store that stores experiments in memory.  This is great to use for testing as well.

Allocation
==========
In Alchemy, treatments are allocated and assigned to bins. By default, there are 100 bins to correspond to percentages when allocating treatments. Identities are also assigned to bins by computing a hash and mapping that number to a bin number.

Allocations of treatments are performed in such a way that when allocations are modified, a best effort is made to keep users assigned to the same previously assigned treatments.  Also, during allocation, the user need not know which bin an allocation actually ends up being assigned to.

For example, let's say you have two treatments: "control" and "new_banner".  You might at first **allocate** 20% to "control" and 20% to "new_banner".

If you later decide you would like "new_banner" to be 30%, you can **allocate** an additional 10%, and users who had "control" would still have "control".

If you decide that you would like to reduce "new_banner" to 10%, you could **deallocate** 10%.  This would, naturally, cause half of the users with the "new_banner" treatment to no longer receive this treatment, but, users who had "control" will still have "control".

Lastly, you can **reallocate** a given amount from one treatment to another.  For example, you could **reallocate** 5% from "control" to "new_banner".  The end result would be that 5% of all users who were assigned to "control" are now assigned to "new_banner", but all other treatment associations are left intact.

Code Example
============
In order to interact with experiments or query what treatments identities are assigned to, you will first need to create an instance of ``Experiments``.  In the example below, we create our ``Experiments`` using a database that stores experiments in memory:

.. code-block:: java

    MemoryDatabaseProvider provider = new MemoryDatabaseProvider();
    Experiments experiments = Experiments.using(provider).build();

Creating and configuring an experiment is easy to do with Alchemy's fluent API:

.. code-block:: java

    Identity identity = new User("bob");

    Experiment experiment =
        experiments
            .create("experiment")
            .setDescription("my first experiment")
            .setSegments("identified")
            .addTreatment("control")
            .addTreatment("cake")
            .addTreatment("pie")
            .addOverride("cake", identity)
            .allocate("control", 10)
            .allocate("cake", 20)
            .allocate("pie", 30)
            .activate()
            .save();

If we want to figure out what treatment we have:

.. code-block:: java

    Identity identity = new User("jane");
    Treatment treatment = experiments.getActiveTreatment("experiment", identity);
    if (treatment == null) {
        // user is not assigned to any treatment
    } else if (treatment.getName().equals("control")) {
        // user is assigned to the "control" treatment
    } else if (treatment.getName().equals("cake")) {
        // user is assigned to the "cake" treatment
    } else if (treatment.getName().equals("pie")) {
        // user is assigned to the "pie" treatment
    }

In this case, because of the override we added, the treatment the user receives should be "cake".  It's also important to note that had we not called ``activate()`` when creating the experiment, ``getActiveTreatment()`` will always return null until the experiment is actually active.