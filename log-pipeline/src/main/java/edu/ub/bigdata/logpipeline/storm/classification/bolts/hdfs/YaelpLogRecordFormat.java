package edu.ub.bigdata.logpipeline.storm.classification.bolts.hdfs;

import edu.ub.bigdata.yaelp.model.YaelpLog;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.tuple.Tuple;

public class YaelpLogRecordFormat implements RecordFormat {

    @Override
    public byte[] format(Tuple input) {
        YaelpLog yaelpLog = (YaelpLog) input.getValueByField("log");
        String logLine = yaelpLog.toString() + YaelpLog.linesTerminatedBy;
        return logLine.getBytes();
    }

}
