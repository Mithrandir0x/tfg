package edu.ub.bigdata.kryo.serdes;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import edu.ub.bigdata.model.RawLog;

public class RawLogKryoSerializer extends Serializer<RawLog> {

    @Override
    public void write(Kryo kryo, Output output, RawLog rawLog) {
        output.writeLong(rawLog.getTimestamp());
        output.writeString(rawLog.getType());
        output.writeString(rawLog.getData());
    }

    @Override
    public RawLog read(Kryo kryo, Input input, Class<RawLog> aClass) {
        long timestamp = input.readLong();
        String type = input.readString();
        String rawData = input.readString();
        return new RawLog(timestamp, type, rawData);
    }

}
