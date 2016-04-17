package com.beabloo.bigdata.logpipeline.storm.classification.topologies;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

public class LogClassificationTopology {

    private static final String RAWLOG_KAFKA_SPOUT_ID = "rawlogs_kafka_spout";

    private static final String TOPOLOGY_NAME = "rawlog_consumer_topology";

    public static void main(String[] args) {

        if ( args.length != 1 ) {
            System.out.println(String.format(" usage: THE FUCK YOU'RE TALKING ABOUT?"));
            return;
        }

        try {
            TopologyBuilder builder = new TopologyBuilder();
            Config config = new Config();



            StormSubmitter.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

}
