package com.beabloo.bigdata.logpipeline.storm.classification.spouts;

import com.beabloo.bigdata.logpipeline.storm.classification.schemes.RawLogScheme;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;

import java.util.UUID;

public class RawLogKafkaSpoutConfig extends SpoutConfig {

    public RawLogKafkaSpoutConfig() {
        super(new ZkHosts("nn.local.vm:2181,hive.local.vm:2181,hbase.local.vm:2181,nimbus.local.vm:2181"),
                "raw-logs",
                "/raw-logs",
                "raw-log-consumer-1");

        this.scheme = new SchemeAsMultiScheme(new RawLogScheme());
    }

}
