package com.beabloo.bigdata.logpipeline.storm.classification.bolts.hdfs;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.tuple.Tuple;

public class CockroachLogRecordFormat implements RecordFormat {

    @Override
    public byte[] format(Tuple input) {
        CockroachLog cockroachLog = (CockroachLog) input.getValueByField("log");
        String logLine = cockroachLog.toString() + CockroachLog.linesTerminatedBy;
        return logLine.getBytes();
    }

}
