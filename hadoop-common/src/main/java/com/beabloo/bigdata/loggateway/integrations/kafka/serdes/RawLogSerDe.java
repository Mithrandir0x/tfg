package com.beabloo.bigdata.loggateway.integrations.kafka.serdes;

import com.beabloo.bigdata.kryo.serdes.KryoSerDe;
import com.beabloo.bigdata.model.RawLog;
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
        return output.toBytes();
    }

    @Override
    public RawLog deserialize(String topic, byte[] bytes) {
        try {
            return kryo.get().readObject(new ByteBufferInput(bytes), RawLog.class);
        } catch(Exception e) {
            throw new IllegalArgumentException("Error reading bytes", e);
        }
    }

    @Override
    public void close() {
    }

}
