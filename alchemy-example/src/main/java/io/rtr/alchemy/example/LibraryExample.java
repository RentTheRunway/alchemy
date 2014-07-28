package io.rtr.alchemy.example;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.rtr.alchemy.db.ExperimentsStoreProvider;
import io.rtr.alchemy.db.memory.MemoryStoreProvider;
import io.rtr.alchemy.example.identities.Composite;
import io.rtr.alchemy.example.identities.Device;
import io.rtr.alchemy.example.identities.User;
import io.rtr.alchemy.filtering.FilterExpression;
import io.rtr.alchemy.models.Allocation;
import io.rtr.alchemy.models.Experiment;
import io.rtr.alchemy.models.Experiments;
import io.rtr.alchemy.models.Treatment;
import io.rtr.alchemy.models.TreatmentOverride;
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
                .setFilter(FilterExpression.of("identified"))
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
                .addOverride("qa_pie", "pie", "user_name=qa")
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
                .addOverride("gene_likes_beer", "beer", "user_name=gene")
                .addOverride("qa_wine", "wine", "user_name=qa")
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

            // Experiment with composite identity - an identity where hashing can change based on what attributes are requested
            experiments
                .create("composite")
                 // we want only users that are identified and we prefer to hash on device
                .setFilter(FilterExpression.of(String.format("%s & %s", User.ATTR_IDENTIFIED, Device.ATTR_DEVICE)))
                .setHashAttributes(Device.ATTR_DEVICE)
                .addTreatment("control")
                .allocate("control", 100)
                .activate()
                .save();

            // These are the attributes that will be requested by the experiment when calling computeHash
            final Set<String> hashAttributes = Sets.newLinkedHashSet(Lists.newArrayList(User.ATTR_IDENTIFIED, Device.ATTR_DEVICE));

            final Composite userOnly = new Composite(new User("foo"), null);
            println("treatment for user-only composite: %s", experiments.getActiveTreatment("composite", userOnly));
            println();

            final Composite deviceOnly = new Composite(null, new Device("bar"));
            println("treatment for device-only composite: %s", experiments.getActiveTreatment("composite", deviceOnly));
            println();

            final Composite anonUser = new Composite(new User(null), new Device("bar"));
            println("treatment for anon-user composite: %s", experiments.getActiveTreatment("composite", anonUser));
            println("hash for anon-user composite: %d", anonUser.computeHash(0, hashAttributes, anonUser.computeAttributes()));
            println();

            final Composite userFoo = new Composite(new User("foo"), new Device("bar"));
            println("treatment for user-foo composite: %s", experiments.getActiveTreatment("composite", userFoo));
            println("hash for user-foo composite: %d", userFoo.computeHash(0, hashAttributes, userFoo.computeAttributes()));
            println();

            // Since we're hashing on device, because that is our requested filter, hashes should be the same
            final Composite userBaz = new Composite(new User("baz"), new Device("bar"));
            println("treatment for user-baz composite: %s", experiments.getActiveTreatment("composite", userBaz));
            println("hash for user-baz composite: %d", userBaz.computeHash(0, hashAttributes, userBaz.computeAttributes()));
            println();

            // Now change our attributes to not be specified to device, now hashes should be different
            final Set<String> hashAttributes2 = Sets.newLinkedHashSet(Lists.newArrayList(User.ATTR_IDENTIFIED));

            println("hash for user-foo composite: %d", userFoo.computeHash(0, hashAttributes2, userFoo.computeAttributes()));
            println("hash for user-baz composite: %d", userBaz.computeHash(0, hashAttributes2, userBaz.computeAttributes()));
        }
    }
}
