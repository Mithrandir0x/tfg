package com.beabloo.bigdata.logpipeline.storm.classification.topologies;

import com.beabloo.bigdata.logpipeline.storm.classification.bolts.CockroachModelParserBolt;
import com.beabloo.bigdata.logpipeline.storm.classification.bolts.CockroachUnpackerBolt;
import com.beabloo.bigdata.logpipeline.storm.classification.bolts.RawLogProtocolSplitterBolt;
import com.beabloo.bigdata.logpipeline.storm.classification.bolts.hdfs.HdfsLogSelector;
import com.beabloo.bigdata.logpipeline.storm.classification.spouts.RawLogKafkaSpout;
import com.endgame.storm.metrics.statsd.StatsdMetricConsumer;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.TimedRotationPolicy;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;
import org.apache.storm.metric.LoggingMetricsConsumer;
import org.apache.storm.topology.TopologyBuilder;

import java.util.HashMap;
import java.util.Map;

public class LogClassificationTopology {

    public static void main(String[] args) {

        if ( args.length != 1 ) {
            System.out.println(String.format(" usage: THE FUCK YOU'RE TALKING ABOUT?"));
            return;
        }

        try {
            // @TODO Statsd host should be parameterized
            Map statsdConfig = new HashMap();
            statsdConfig.put(StatsdMetricConsumer.STATSD_HOST, "stats.local.vm");
            statsdConfig.put(StatsdMetricConsumer.STATSD_PORT, 8125);
            statsdConfig.put(StatsdMetricConsumer.STATSD_PREFIX, "beabloo.storm.metrics.logclassification.");
            statsdConfig.put(Config.TOPOLOGY_NAME, args[0]);

            TopologyBuilder builder = new TopologyBuilder();
            Config config = new Config();

            RawLogKafkaSpout rawLogKafkaSpout = new RawLogKafkaSpout();
            builder.setSpout(RawLogKafkaSpout.ID, rawLogKafkaSpout);

            RawLogProtocolSplitterBolt rawLogProtocolSplitterBolt = new RawLogProtocolSplitterBolt();
            builder.setBolt(RawLogProtocolSplitterBolt.ID, rawLogProtocolSplitterBolt)
                    .shuffleGrouping(RawLogKafkaSpout.ID);

            CockroachUnpackerBolt cockroachUnpackerBolt = new CockroachUnpackerBolt();
            builder.setBolt(CockroachUnpackerBolt.ID, cockroachUnpackerBolt)
                    .shuffleGrouping(RawLogProtocolSplitterBolt.ID, RawLogProtocolSplitterBolt.HTTP_COCKROACH_STREAM);

            CockroachModelParserBolt cockroachModelParserBolt = new CockroachModelParserBolt();
            builder.setBolt(CockroachModelParserBolt.ID, cockroachModelParserBolt)
                    .shuffleGrouping(CockroachUnpackerBolt.ID);

            // sync the filesystem after every 1k tuples
            SyncPolicy syncPolicy = new CountSyncPolicy(10);

            // rotate files when they reach 5MB
            FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(2.0f, FileSizeRotationPolicy.Units.MB);
            //TimedRotationPolicy rotationPolicy = new TimedRotationPolicy(5, TimedRotationPolicy.TimeUnit.SECONDS);

            FileNameFormat fileNameFormat = new DefaultFileNameFormat().withPath("/engine2_big_data/cockroach/");

            HdfsBolt hdfsBolt = new HdfsBolt()
                    .withFsUrl("hdfs://nn.local.vm:8020")
                    .withFileNameFormat(fileNameFormat)
                    .withRecordFormat(HdfsLogSelector.getHdfsLogFormat())
                    .withPartitioner(HdfsLogSelector.getHdfsLogPartitioner())
                    .withRotationPolicy(rotationPolicy)
                    .withSyncPolicy(syncPolicy);
            builder.setBolt("COCKROACH_HDFS_BOLT_ID", hdfsBolt)
                    .shuffleGrouping(CockroachModelParserBolt.ID);

            config.setNumWorkers(4);
            //config.registerMetricsConsumer(LoggingMetricsConsumer.class, 2);
            // @TODO Statsd metrics consumer parallelization hint must be parameterized
            config.registerMetricsConsumer(StatsdMetricConsumer.class, statsdConfig, 2);

            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

}
