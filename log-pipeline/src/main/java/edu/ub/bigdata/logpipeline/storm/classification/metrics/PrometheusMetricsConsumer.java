package edu.ub.bigdata.logpipeline.storm.classification.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.apache.storm.metric.api.IMetricsConsumer;
import org.apache.storm.task.IErrorReporter;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PrometheusMetricsConsumer implements IMetricsConsumer {

    // @TODO Parameterize the prometheus metric's namespace
    private static final String namespace = "storm_logpipeline";

    public static final String invalidMetricChars = "[.\\-\\\\/:]+";

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
            String metricName;
            if ( dataPoint.value instanceof Long ) {
                metricName = String.format("%s_%s_%s_%s", namespace, taskInfo.srcWorkerHost, "task" + taskInfo.srcTaskId, dataPoint.name).replaceAll(invalidMetricChars, "_");

                Gauge metric = Gauge.build().name(metricName).help("Storm metric").register(registry);
                metric.set((long) dataPoint.value);

                log.info(String.format("Processing metric [%s] with value [%s]", metricName, dataPoint.value));
            } else if ( dataPoint.value instanceof Map ) {
                Map map = (Map) dataPoint.value;
                for ( Object key : map.keySet()) {
                    Object value = map.get(key);
                    if ( value instanceof Number ) {
                        metricName = String.format("%s_%s_%s_%s", namespace, "task" + taskInfo.srcTaskId, key.toString(), dataPoint.name).replaceAll(invalidMetricChars, "_");
                        Gauge metric = Gauge.build().name(metricName).help("Storm metric").register(registry);
                        if ( value instanceof Integer ) {
                            metric.set((int) value);
                        } else if ( value instanceof Long ) {
                            metric.set((long) value);
                        } else {
                            log.warn("Unknown value passed in map [%s]", value.toString());
                        }

                        log.info(String.format("Processing metric [%s] with value [%s]", metricName, value));
                    } else {
                        log.warn("Unknown metric type inside map");
                    }
                }
            } else {
                log.warn("Unknown kind of metric");
            }
        }

        try {
            Map<String, String> groupingKey = new HashMap<>();
            groupingKey.put("instance", String.format("%s:%s", taskInfo.srcWorkerHost, taskInfo.srcWorkerPort));
            pushGateway.pushAdd(registry, namespace, groupingKey);
        } catch ( Exception ex ) {
            log.error(String.format("Error while trying to push metrics to the gateway [%s]", ex.getMessage()), ex);
        }
    }

    @Override
    public void cleanup() {
    }

}
