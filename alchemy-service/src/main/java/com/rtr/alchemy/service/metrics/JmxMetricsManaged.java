package com.rtr.alchemy.service.metrics;

import com.codahale.metrics.JmxReporter;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;

/**
 * Enables logging of metrics to JMX
 */
public class JmxMetricsManaged implements Managed {
    private final JmxReporter reporter;

    public JmxMetricsManaged(Environment environment) {
        reporter = JmxReporter.forRegistry(environment.metrics()).build();
    }

    @Override
    public void start() throws Exception {
        reporter.start();
    }

    @Override
    public void stop() throws Exception {
        reporter.stop();
    }
}
