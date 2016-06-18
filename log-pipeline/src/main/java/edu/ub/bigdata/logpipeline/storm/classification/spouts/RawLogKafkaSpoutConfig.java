package edu.ub.bigdata.logpipeline.storm.classification.spouts;

import edu.ub.bigdata.logpipeline.storm.classification.schemes.RawLogScheme;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;

public class RawLogKafkaSpoutConfig extends SpoutConfig {

    public RawLogKafkaSpoutConfig() {
        // @TODO Zookeeper hosts should be parameterized
        super(new ZkHosts("nn.local.vm:2181,hive.local.vm:2181,hbase.local.vm:2181,nimbus.local.vm:2181"),
                "raw-logs",
                "/raw-logs",
                "raw-log-consumer-group");

        this.scheme = new SchemeAsMultiScheme(new RawLogScheme());
        this.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
    }

}
