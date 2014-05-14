package com.rtr.alchemy.example;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.collect.Iterables;
import com.rtr.alchemy.db.ExperimentsStoreProvider;
import com.rtr.alchemy.db.memory.MemoryStoreProvider;
import com.rtr.alchemy.example.identities.User;
import com.rtr.alchemy.models.Allocation;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.models.Experiments;
import com.rtr.alchemy.models.Treatment;
import com.rtr.alchemy.models.TreatmentOverride;
import org.slf4j.LoggerFactory;

import java.util.Map;

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
    public static void main(String[] args) {
        disableLogging();

        final ExperimentsStoreProvider provider = new MemoryStoreProvider();
        final Experiments experiments = Experiments.using(provider).build();

        // Let's create our experiment
        experiments
            .create("my_experiment")
            .setDescription("my new experiment")
            .addTreatment("control", "the default")
            .addTreatment("pie", "show them pie")
            .setIdentityType("user")
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


    }
}
