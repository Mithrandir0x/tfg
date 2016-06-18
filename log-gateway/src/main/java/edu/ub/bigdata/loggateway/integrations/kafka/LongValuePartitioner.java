package edu.ub.bigdata.loggateway.integrations.kafka;

import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;

public class LongValuePartitioner extends DefaultPartitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        long numPartitions = (long) partitions.size();
        int partition;

        if ( key instanceof Long ) {
            Long longValue = (Long) key;
            partition = (int) (longValue % numPartitions);
        } else {
            partition = super.partition(topic, key, keyBytes, value, valueBytes, cluster);
        }

        return partition;
    }

}
