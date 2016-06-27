package edu.ub.bigdata.logpipeline.storm.classification.schemes;

import edu.ub.bigdata.kafka.serdes.RawLogSerDe;
import edu.ub.bigdata.model.RawLog;
import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.nio.ByteBuffer;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawLogScheme implements Scheme {

    private static final Logger log = LoggerFactory.getLogger(RawLogScheme.class);

    @Override
    public List<Object> deserialize(ByteBuffer payload) {
        byte[] bytes = new byte[payload.limit()];
        payload.get(bytes);

//        log.debug(String.format("bytes [%s]", bytesToHex(bytes)));

        RawLogSerDe serDe = new RawLogSerDe();
        RawLog rawLog = serDe.deserialize(null, bytes);

        return new Values(rawLog.getTimestamp(), rawLog.getType(), rawLog.getData());
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("timestamp", "type", "data");
    }

}
