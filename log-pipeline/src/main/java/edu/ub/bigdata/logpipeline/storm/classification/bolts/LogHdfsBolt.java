package edu.ub.bigdata.logpipeline.storm.classification.bolts;

import edu.ub.bigdata.logpipeline.storm.classification.bolts.hdfs.YaelpLogRecordFormat;
import edu.ub.bigdata.logpipeline.storm.classification.bolts.hdfs.TimeHdfsPartitioner;
import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;

public class LogHdfsBolt extends HdfsBolt {

    public static final String ID = "YAELP_HDFS_BOLT_ID";

    public LogHdfsBolt(String path) {
        super();

        // sync the filesystem after every 1k tuples
        SyncPolicy syncPolicy = new CountSyncPolicy(10);

        // rotate files when they reach 5MB
        FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(2.0f, FileSizeRotationPolicy.Units.MB);
        //FileRotationPolicy rotationPolicy = new TimedRotationPolicy(5, TimedRotationPolicy.TimeUnit.SECONDS);

        FileNameFormat fileNameFormat = new DefaultFileNameFormat().withPath(path);

        withFsUrl("hdfs://nn.local.vm:8020");

        withFileNameFormat(fileNameFormat);
        withRecordFormat(new YaelpLogRecordFormat());
        withPartitioner(new TimeHdfsPartitioner());

        withRotationPolicy(rotationPolicy);
        withSyncPolicy(syncPolicy);
    }

}
