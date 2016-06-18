package edu.ub.bigdata.loggateway.integrations.kafka.serdes;

import edu.ub.bigdata.kryo.serdes.KryoSerDe;
import edu.ub.bigdata.model.RawLog;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class RawLogSerDe implements Serializer<RawLog>, Deserializer<RawLog> {

    private KryoSerDe kryo = new KryoSerDe();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, RawLog rawLog) {
        ByteBufferOutput output = new ByteBufferOutput(1024, 1024 * 1024);
        kryo.get().writeObject(output, rawLog);
        byte[] bytes = output.toBytes();
        //Logger.getLogger(RawLogSerDe.class.getName()).log(Level.SEVERE, String.format("bytes [%s]", bytesToHex(bytes)));
        return bytes;
    }

    @Override
    public RawLog deserialize(String topic, byte[] bytes) {
        try {
            //Logger.getLogger(RawLogSerDe.class.getName()).log(Level.SEVERE, String.format("bytes [%s]", bytesToHex(bytes)));

            return kryo.get().readObject(new ByteBufferInput(bytes), RawLog.class);
        } catch(Exception e) {
            throw new IllegalArgumentException("Error reading bytes", e);
        }
    }

    @Override
    public void close() {
    }

}
