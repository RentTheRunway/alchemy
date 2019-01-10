package io.rtr.alchemy.example;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rtr.alchemy.client.AlchemyClient;
import io.rtr.alchemy.client.AlchemyClientConfiguration;
import io.rtr.alchemy.dto.models.AllocationDto;
import io.rtr.alchemy.dto.models.ExperimentDto;
import io.rtr.alchemy.dto.models.TreatmentDto;
import io.rtr.alchemy.dto.models.TreatmentOverrideDto;
import io.rtr.alchemy.example.dto.UserDto;
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
                .addTreatment(0, "the default")
                .addTreatment(1, "show them pie")
                .setFilter("identified")
                .allocate(0, 25)
                .allocate(1, 25)
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
            client.addOverride("my_experiment", "qa_pie", 1, "user_name=qa");

            // Let's also add for comparison, cake
            client.addTreatment("my_experiment", 2, "show them cake");
            client
                .updateAllocations("my_experiment")
                .allocate(2, 25)
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
            final TreatmentDto treatment = client.getTreatment("my_experiment", 0);
            println("name: %s, description: %s", treatment.getName(), treatment.getDescription());
            println();

            // You know what, who cares about pie or cake? Let's compare beer and wine!
            client.clearTreatments("my_experiment");
            client.addTreatment("my_experiment", 10, "beer me!");
            client.addTreatment("my_experiment", 20, "wine please");
            client
                .updateAllocations("my_experiment")
                .allocate(10, 90) // slightly biased
                .allocate(20, 10)
                .apply();

            // Ok, that was unfair, let's fix the allocations
            client.clearAllocations("my_experiment");
            client.addOverride("my_experiment", "gene_likes_beer", 10, "user_name=gene");
            client.addOverride("my_experiment", "qa_wine", 20, "user_name=qa");
            client
                .updateAllocations("my_experiment")
                .allocate(10, 51)
                .allocate(20, 49)
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
            client.removeTreatment("my_experiment", 20);

            // Let's nuke it and call it a day
            client.deleteExperiment("my_experiment");

        } catch (final UniformInterfaceException uie) {
            println("ERROR, HTTP %d: %s", uie.getResponse().getStatus(), uie.getResponse().getEntity(String.class));
        }
    }
}
