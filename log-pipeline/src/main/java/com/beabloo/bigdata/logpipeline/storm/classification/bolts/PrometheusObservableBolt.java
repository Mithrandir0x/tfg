package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class PrometheusObservableBolt extends BaseRichBolt {

    private static final Logger log = LoggerFactory.getLogger(PrometheusObservableBolt.class);

    private transient PushGateway pushGateway;
    private transient CollectorRegistry collectorRegistry;
    private transient String taskId;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        pushGateway = new PushGateway("stats.local.vm:9091");
        taskId = String.format("%s_%s_%s", context.getThisComponentId(), "" + context.getThisTaskId(), context.getThisWorkerPort());
        collectorRegistry = new CollectorRegistry();
    }

    @Override
    public void execute(Tuple input) {
        processTuple(input);

        try {
            Map<String, String> groupingKey = new HashMap<>();
            groupingKey.put("instance", taskId);
            pushGateway.push(collectorRegistry, "storm_logpipeline", groupingKey);
        } catch ( Exception ex ) {
            log.error("Error while trying to send metrics to push gateway", ex);
        }
    }

    protected CollectorRegistry getCollectorRegistry() {
        return collectorRegistry;
    }



    public abstract void processTuple(Tuple input);

}
