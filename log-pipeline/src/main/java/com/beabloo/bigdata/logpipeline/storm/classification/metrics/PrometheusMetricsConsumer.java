package com.beabloo.bigdata.logpipeline.storm.classification.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.apache.storm.metric.api.IMetricsConsumer;
import org.apache.storm.task.IErrorReporter;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class PrometheusMetricsConsumer implements IMetricsConsumer {

    // @TODO Parameterize the prometheus metric's namespace
    private static final String namespace = "storm_logpipeline";

    private static final Logger log = LoggerFactory.getLogger(PrometheusMetricsConsumer.class);

    private PushGateway pushGateway;

    @Override
    public void prepare(Map stormConf, Object registrationArgument, TopologyContext context, IErrorReporter errorReporter) {
        pushGateway = new PushGateway("stats.local.vm:9091");
    }

    @Override
    public void handleDataPoints(TaskInfo taskInfo, Collection<DataPoint> dataPoints) {
        CollectorRegistry registry = new CollectorRegistry();

        for ( DataPoint dataPoint : dataPoints ) {
            Gauge metric = Gauge.build().name(dataPoint.name).help("").register(registry);

            if ( dataPoint.value instanceof Long ) {
                metric.labels(taskInfo.srcWorkerHost, "" + taskInfo.srcTaskId, taskInfo.srcComponentId);
                metric.set((long) dataPoint.value);
            } else if ( dataPoint.value instanceof Map ) {
                log.warn("Maps currently not supported");
            } else {
                log.warn("Unknown kind of metric");
            }
        }

        try {
            pushGateway.push(registry, namespace);
        } catch ( Exception ex ) {
            log.error("Error while trying to push metrics to the gateway");
        }
    }

    @Override
    public void cleanup() {
    }

}