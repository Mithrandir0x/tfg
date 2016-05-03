package com.beabloo.bigdata.logpipeline.storm.classification.metrics;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrometheusMetricsConsumerTest {

    @Test
    public void everythingToUnderscoreTest() {
        String metric, result;

        String regex = "[.\\-\\\\/:]+";

        metric = "storm_logpipeline_storm3.local.vm_task:1_memory/nonHeap\\";
        result = "storm_logpipeline_storm3_local_vm_task_1_memory_nonHeap_";
        assertEquals(result, metric.replaceAll(regex, "_"));

        metric = "storm_logpipeline_storm3_local_vm_task9___emit-count";
        result = "storm_logpipeline_storm3_local_vm_task9___emit_count";
        assertEquals(result, metric.replaceAll(regex, "_"));
    }

}