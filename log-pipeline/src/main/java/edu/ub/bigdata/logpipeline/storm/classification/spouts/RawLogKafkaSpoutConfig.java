package edu.ub.bigdata.logpipeline.storm.classification.spouts;

import edu.ub.bigdata.logpipeline.storm.classification.schemes.RawLogScheme;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;

public class RawLogKafkaSpoutConfig extends SpoutConfig {

    public RawLogKafkaSpoutConfig(String zookeeperHosts) {
        // @TODO Zookeeper hosts should be parameterized
        super(new ZkHosts(zookeeperHosts),
                "raw-logs",
                "/raw-logs",
                "raw-log-consumer-group");

        this.scheme = new SchemeAsMultiScheme(new RawLogScheme());
        this.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
    }

}
