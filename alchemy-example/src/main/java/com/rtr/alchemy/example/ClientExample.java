package com.rtr.alchemy.example;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtr.alchemy.client.AlchemyClient;
import com.rtr.alchemy.client.AlchemyClientConfiguration;
import com.rtr.alchemy.dto.models.AllocationDto;
import com.rtr.alchemy.dto.models.ExperimentDto;
import com.rtr.alchemy.dto.models.TreatmentDto;
import com.rtr.alchemy.dto.models.TreatmentOverrideDto;
import com.rtr.alchemy.example.dto.UserDto;
import com.sun.jersey.api.client.UniformInterfaceException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.util.Map;

/**
 * Example code that utilizes the alchemy-client library to talk to an instance of Alchemy service
 */
public class ClientExample {
    private static void println(String formatMessage, Object ... args) {
        System.out.println(String.format(formatMessage, args));
    }

    private static void println() {
        System.out.println();
    }

    private static AlchemyClient buildClient(String configurationFile) throws Exception {
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final ObjectMapper mapper = Jackson.newObjectMapper();
        final ConfigurationFactory<AlchemyClientConfiguration> configurationFactory = new ConfigurationFactory<>(
            AlchemyClientConfiguration.class,
            validator,
            mapper,
            ""
        );

        return new AlchemyClient(
            configurationFactory.build(new File(configurationFile))
        );
    }

    private static void disableLogging() {
        final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.ERROR);
    }

    public static void main(String[] args) throws Exception {
        try {
            disableLogging();

            if (args.length != 1) {
                System.out.println("must specify a client configuration file");
                return;
            }

            final AlchemyClient client = buildClient(args[0]);

            // Let's create our experiment
            client
                .createExperiment("my_experiment")
                .setDescription("my new experiment")
                .addTreatment("control", "the default")
                .addTreatment("pie", "show them pie")
                .setFilter("identified")
                .allocate("control", 25)
                .allocate("pie", 25)
                .activate()
                .apply();

            // Actually, the description should be more descriptive
            client
                .updateExperiment("my_experiment")
                .setDescription("my experiment to see if people like pie")
                .apply();

            // Let's get our experiment and print it out
            final ExperimentDto experiment = client.getExperiment("my_experiment");
            println("name: %s", experiment.getName());
            println("description: %s", experiment.getDescription());
            println("active: %s", experiment.isActive());
            println();

            // We should have 1 experiment, if ours is the only one
            println("number of experiments: %d", client.getExperiments().size());
            println();

            // Let's add an override for our qa person, who obviously likes pie
            client.addOverride("my_experiment", "qa_pie", "pie", "user_name=qa");

            // Let's also add for comparison, cake
            client.addTreatment("my_experiment", "cake", "show them cake");
            client
                .updateAllocations("my_experiment")
                .allocate("cake", 25)
                .apply();

            // Let's print out our allocations
            for (final AllocationDto allocation : client.getAllocations("my_experiment")) {
                println("treatment: %s, offset: %d, size: %d", allocation.getTreatment(), allocation.getOffset(), allocation.getSize());
            }
            println();

            // We should have 3 treatments
            println("number of treatments: %d", client.getTreatments("my_experiment").size());
            println();

            // Let's print out or 'control' treatment
            final TreatmentDto treatment = client.getTreatment("my_experiment", "control");
            println("name: %s, description: %s", treatment.getName(), treatment.getDescription());
            println();

            // You know what, who cares about pie or cake? Let's compare beer and wine!
            client.clearTreatments("my_experiment");
            client.addTreatment("my_experiment", "beer", "beer me!");
            client.addTreatment("my_experiment", "wine", "wine please");
            client
                .updateAllocations("my_experiment")
                .allocate("beer", 90) // slightly biased
                .allocate("wine", 10)
                .apply();

            // Ok, that was unfair, let's fix the allocations
            client.clearAllocations("my_experiment");
            client.addOverride("my_experiment", "gene_likes_beer", "beer", "user_name=gene");
            client.addOverride("my_experiment", "qa_wine", "wine", "user_name=qa");
            client
                .updateAllocations("my_experiment")
                .allocate("beer", 51)
                .allocate("wine", 49)
                .apply();

            // Print out an override
            final TreatmentOverrideDto override = client.getOverride("my_experiment", "qa_wine");
            println("name: %s, treatment: %s", override.getName(), override.getTreatment());
            println();

            // Let's query our active experiments
            final TreatmentDto activeTreatment = client.getActiveTreatment("my_experiment", new UserDto("gene"));
            println("name: %s, description: %s", activeTreatment.getName(), activeTreatment.getDescription());
            println();

            for (final Map.Entry<String, TreatmentDto> entry : client.getActiveTreatments(new UserDto("qa")).entrySet()) {
                println(
                    "experiment: %s, treatment: %s, description: %s",
                    entry.getKey(),
                    entry.getValue().getName(),
                    entry.getValue().getDescription()
                );
            }
            println();

            // Let's get rid of our overrides
            client.removeOverride("my_experiment", "qa_wine");
            client.clearOverrides("my_experiment");

            println("number of overrides: %d", client.getOverrides("my_experiment").size());
            println();

            // We ran out of wine!
            client.removeTreatment("my_experiment", "wine");

            // Let's nuke it and call it a day
            client.deleteExperiment("my_experiment");

        } catch (final UniformInterfaceException uie) {
            println("ERROR, HTTP %d: %s", uie.getResponse().getStatus(), uie.getResponse().getEntity(String.class));
        }
    }
}
