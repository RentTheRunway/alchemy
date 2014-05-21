package com.rtr.alchemy.example;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.db.memory.MemoryStoreProvider;
import com.rtr.alchemy.example.identities.Composite;
import com.rtr.alchemy.example.identities.Device;
import com.rtr.alchemy.example.identities.User;
import com.rtr.alchemy.models.Allocation;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.models.Experiments;
import com.rtr.alchemy.models.Treatment;
import com.rtr.alchemy.models.TreatmentOverride;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Example code that utilizes the alchemy-core library to interact with experiments directly
 */
public class LibraryExample {
    private static void disableLogging() {
        final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);
    }

    private static void println(String formatMessage, Object ... args) {
        System.out.println(String.format(formatMessage, args));
    }

    private static void println() {
        System.out.println();
    }

    /**
     * This example performs some basic operations on an instance of Experiments directly
     */
    public static void main(String[] args) throws IOException {
        disableLogging();

        try(final ExperimentsStoreProvider provider = new MemoryStoreProvider();
            final Experiments experiments = Experiments.using(provider).build()) {

            // Let's create our experiment
            experiments
                .create("my_experiment")
                .setDescription("my new experiment")
                .addTreatment("control", "the default")
                .addTreatment("pie", "show them pie")
                .setSegments("identified")
                .allocate("control", 25)
                .allocate("pie", 25)
                .activate()
                .save();

            // Actually, the description should be more descriptive
            experiments
                .get("my_experiment")
                .setDescription("my experiment to see if people like pie")
                .save();

            // Let's get our experiment and print it out
            final Experiment experiment = experiments.get("my_experiment");
            println("name: %s", experiment.getName());
            println("description: %s", experiment.getDescription());
            println("active: %s", experiment.isActive());
            println();

            // We should have 1 experiment, if ours is the only one
            println("number of experiments: %d", Iterables.size(experiments.find()));
            println();

            // Let's add an override for our qa person, who obviously likes pie
            experiment
                .addOverride("qa_pie", "pie", new User("qa"))
                .save();

            // Let's also add for comparison, cake
            experiment
                .addTreatment("cake", "show them cake")
                .allocate("cake", 25)
                .save();

            // Let's print out our allocations
            for (final Allocation allocation : experiment.getAllocations()) {
                println(
                    "treatment: %s, offset: %d, size: %d",
                    allocation.getTreatment().getName(),
                    allocation.getOffset(),
                    allocation.getSize()
                );
            }
            println();

            // We should have 3 treatments
            println("number of treatments: %d", experiment.getTreatments().size());
            println();

            // Let's print out or 'control' treatment
            final Treatment treatment = experiment.getTreatment("control");
            println("name: %s, description: %s", treatment.getName(), treatment.getDescription());
            println();

            // You know what, who cares about pie or cake? Let's compare beer and wine!
            experiment
                .clearTreatments()
                .addTreatment("beer", "beer me!")
                .addTreatment("wine", "wine please")
                .allocate("beer", 90) // slightly biased
                .allocate("wine", 10)
                .save();

            // Ok, that was unfair, let's fix the allocations
            experiment
                .deallocateAll()
                .addOverride("gene_likes_beer", "beer", new User("gene"))
                .addOverride("qa_wine", "wine", new User("qa"))
                .allocate("beer", 51)
                .allocate("wine", 49)
                .save();

            // Print out an override
            final TreatmentOverride override = experiment.getOverride("qa_wine");
            println("name: %s, treatment: %s", override.getName(), override.getTreatment());
            println();

            // Let's query our active experiments
            final Treatment activeTreatment = experiments.getActiveTreatment("my_experiment", new User("gene"));
            println("name: %s, description: %s", activeTreatment.getName(), activeTreatment.getDescription());
            println();

            for (final Map.Entry<Experiment, Treatment> entry : experiments.getActiveTreatments(new User("qa")).entrySet()) {
                println(
                    "experiment: %s, treatment: %s, description: %s",
                    entry.getKey().getName(),
                    entry.getValue().getName(),
                    entry.getValue().getDescription()
                );
            }
            println();

            // Let's get rid of our overrides
            experiment
                .removeOverride("qa_wine")
                .clearOverrides()
                .save();

            println("number of overrides: %d", experiment.getOverrides().size());
            println();

            // We ran out of wine!
            experiment.removeTreatment("wine");

            // Let's nuke it and call it a day
            experiments.delete("my_experiment");

            // Experiment with composite identity - an identity where hashing can change based on what segments are requested
            experiments
                .create("composite")
                .setSegments(User.SEGMENT_IDENTIFIED, Composite.SEGMENT_DEVICE) // we want only users that are identified and we prefer to hash on device
                .addTreatment("control")
                .allocate("control", 100)
                .activate()
                .save();

            // These are the segments that will be requested by the experiment when calling computeHash
            final Set<String> segments = Sets.newHashSet(User.SEGMENT_IDENTIFIED, Composite.SEGMENT_DEVICE);

            final Composite userOnly = new Composite(new User("foo"), null);
            println("treatment for user-only composite: %s", experiments.getActiveTreatment("composite", userOnly));
            println();

            final Composite deviceOnly = new Composite(null, new Device("bar"));
            println("treatment for device-only composite: %s", experiments.getActiveTreatment("composite", deviceOnly));
            println();

            final Composite anonUser = new Composite(new User(null), new Device("bar"));
            println("treatment for anon-user composite: %s", experiments.getActiveTreatment("composite", anonUser));
            println("hash for anon-user composite: %d", anonUser.computeHash(0, segments));
            println();

            final Composite userFoo = new Composite(new User("foo"), new Device("bar"));
            println("treatment for user-foo composite: %s", experiments.getActiveTreatment("composite", userFoo));
            println("hash for user-foo composite: %d", userFoo.computeHash(0, segments));
            println();

            // Since we're hashing on device, because that is our requested segment, hashes should be the same
            final Composite userBaz = new Composite(new User("baz"), new Device("bar"));
            println("treatment for user-baz composite: %s", experiments.getActiveTreatment("composite", userBaz));
            println("hash for user-baz composite: %d", userBaz.computeHash(0, segments));
            println();

            // Now change our segments to not be specified to device, now hashes should be different
            final Set<String> segments2 = Sets.newHashSet(User.SEGMENT_IDENTIFIED);
            println("hash for user-foo composite: %d", userFoo.computeHash(0, segments2));
            println("hash for user-baz composite: %d", userBaz.computeHash(0, segments2));
        }
    }
}
