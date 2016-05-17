package com.beabloo.monitoring.storm.aspects;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import org.apache.storm.task.TopologyContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Aspect
public class StormBoltMetricsGeneratorInterceptor {

    private static final Logger log = LoggerFactory.getLogger(StormBoltMetricsGeneratorInterceptor.class);

    transient PushGateway pushGateway;
    transient CollectorRegistry collectorRegistry;
    transient String taskId;

    @Around("execution(* (@StormBoltMetricsGenerator org.apache.storm.task.IBolt+).prepare(..))")
    public void wrapBoltPreparation(ProceedingJoinPoint pjp) throws Throwable {
        pjp.proceed();

        Object args[] = pjp.getArgs();

        Map stormConf = (Map) args[0];
        TopologyContext context = (TopologyContext) args[1];

        pushGateway = new PushGateway("stats.local.vm:9091");
        taskId = String.format("%s_%s_%s", context.getThisComponentId(), "" + context.getThisTaskId(), context.getThisWorkerPort());
        collectorRegistry = (CollectorRegistry) stormConf.get("collectorRegistry");
    }

    @Around("execution(* (@StormBoltMetricsGenerator org.apache.storm.task.IBolt+).execute(..))")
    public void sendMetrics(ProceedingJoinPoint pjp) throws Throwable {
        pjp.proceed();

        try {
            Map<String, String> groupingKey = new HashMap<>();
            groupingKey.put("instance", taskId);
            pushGateway.push(collectorRegistry, "storm_logpipeline", groupingKey);
        } catch ( Exception ex ) {
            log.error("Error while trying to send metrics to push gateway", ex);
        }
    }

}
