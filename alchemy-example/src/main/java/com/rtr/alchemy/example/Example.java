package com.rtr.alchemy.example;

import com.google.common.collect.Lists;
import com.rtr.alchemy.db.ExperimentsDatabaseProvider;
import com.rtr.alchemy.db.memory.MemoryDatabaseProvider;
import com.rtr.alchemy.example.identity.User;
import com.rtr.alchemy.models.Experiment;
import com.rtr.alchemy.models.Experiments;
import com.rtr.alchemy.models.Treatment;

import java.util.List;

public class Example {
    private static final String EXPERIMENT = "pie_or_cake";
    private static final String CONTROL = "control";
    private static final String PIE = "pie";
    private static final String CAKE = "cake";
    private static final List<User> USERS = Lists.newArrayList(
        new User("bob"),
        new User("joe"),
        new User("jim"),
        new User("jane"),
        new User("betty"),
        new User("jean"),
        new User("jessica"),
        new User("david"),
        new User("john"),
        new User("leah"),
        new User("carla"),
        new User("arthur"),
        new User("george"),
        new User("amy"),
        new User("brad")
    );


    private static void printUserTreatments(Experiments experiments) {
        for (User user : USERS) {
            final Treatment treatment = experiments.getActiveTreatment(EXPERIMENT, user);
            final String treatmentName = treatment == null ? "no treatment" : "treatment " + treatment.getName();

            System.out.println(
                String.format(
                    "User %s has %s",
                    user.getName(),
                    treatmentName
                )
            );
        }
    }

    public static void main(String[] args) {
        final ExperimentsDatabaseProvider provider = new MemoryDatabaseProvider();
        final Experiments experiments = new Experiments(provider);

        final Experiment experiment = experiments
            .create(EXPERIMENT)
            .setDescription("my food experiment to determine if people prefer cake or pie")
            .addTreatment(CONTROL, "the default treatment")
            .addTreatment(PIE, "the users who will receive pie")
            .addTreatment(CAKE, "the users who will receive cake")
            .setIdentityType("user")
            .allocate(CONTROL, 25)
            .allocate(PIE, 25)
            .allocate(CAKE, 25)
            .addOverride(CONTROL, USERS.get(0))
            .activate()
            .save();

        System.out.println("initial allocation:");
        printUserTreatments(experiments);

        experiment.allocate(CONTROL, 15);
        System.out.println();
        System.out.println("after additional allocation:");
        printUserTreatments(experiments);

        experiment.reallocate(CONTROL, PIE, 20);
        experiment.reallocate(CONTROL, CAKE, 20);
        experiment.allocate(PIE, 5);
        experiment.allocate(CAKE, 5);
        System.out.println();
        System.out.println("after re-allocation:");
        printUserTreatments(experiments);
    }
}
