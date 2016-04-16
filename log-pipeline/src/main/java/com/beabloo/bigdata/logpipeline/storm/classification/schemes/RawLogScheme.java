package com.beabloo.bigdata.logpipeline.storm.classification.schemes;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import com.beabloo.bigdata.loggateway.integrations.kafka.serdes.RawLogSerDe;
import com.beabloo.bigdata.model.RawLog;

import java.nio.ByteBuffer;
import java.util.List;

public class RawLogScheme implements Scheme {

//    @Override
//    public List<Object> deserialize(byte[] ser) {
//        RawLogSerDe serDe = new RawLogSerDe();
//        RawLog rawLog = serDe.deserialize(null, ser);
//        return new Values(rawLog.getTimestamp(), rawLog.getType(), rawLog.getData());
//    }

    @Override
    public List<Object> deserialize(ByteBuffer ser) {
        RawLogSerDe serDe = new RawLogSerDe();
        RawLog rawLog = serDe.deserialize(null, ser.array());
        return new Values(rawLog.getTimestamp(), rawLog.getType(), rawLog.getData());
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("timestamp", "type", "data");
    }

}
