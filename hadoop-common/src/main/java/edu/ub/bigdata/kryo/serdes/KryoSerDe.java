package edu.ub.bigdata.kryo.serdes;

import edu.ub.bigdata.model.RawLog;
import edu.ub.bigdata.model.WifiPresenceLog;
import com.esotericsoftware.kryo.Kryo;

public class KryoSerDe extends ThreadLocal<Kryo> {

    protected Kryo initialValue() {
        Kryo kryo = new Kryo();
        kryo.register(WifiPresenceLog.class, 1);
        kryo.register(RawLog.class, new RawLogKryoSerializer(), 2);
        return kryo;
    }

}
