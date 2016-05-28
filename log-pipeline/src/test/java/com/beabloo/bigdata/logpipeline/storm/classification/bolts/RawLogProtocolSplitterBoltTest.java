package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.model.RawLog;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RawLogProtocolSplitterBoltTest {

    @Test
    public void hashingTest() throws Exception {
        Tuple tuple;
        RawLog rawLog;

        RawLogProtocolSplitterBolt bolt = new RawLogProtocolSplitterBolt();

        Map conf = mock(Map.class);
        TopologyContext context = mock(TopologyContext.class);
        OutputCollector collector = mock(OutputCollector.class);

        bolt.prepare(conf, context, collector);

        tuple = mockTuple(0, "", "");
        assertEquals("cbc357ccb763df2852fee8c4fc7d55f2", bolt.getUniqueRawLogId(tuple));

        rawLog = new RawLog(
                1464419666l,
                "http://kafka4.local.vm:18084/log-gateway/activityTracking/wifi",
                "json=%7B%22events%22%3A+%5B%7B%22paramsValues%22%3A+%7B%22oui%" +
                        "22%3A+%22ef%3Ac2%3A42%22%2C+%22power%22%3A+-13%2C+%22s" +
                        "tartEvent%22%3A+1464417832%2C+%22tags%22%3A+%22%22%2C+" +
                        "%22device%22%3A+%22f73cea0aec5f9026fdef5ae5c2b4581a%22" +
                        "%2C+%22organization%22%3A+39159%2C+%22sensor%22%3A+517" +
                        "3%2C+%22event%22%3A+3%2C+%22hotspot%22%3A+57469%7D%2C+" +
                        "%22extraParams%22%3A+%7B%7D%7D%5D%7D");

        tuple = mockTuple(rawLog.getTimestamp(), rawLog.getType(), rawLog.getData());
        assertEquals("3fb7192a06c4e17e087e15dd9595baba", bolt.getUniqueRawLogId(tuple));

        bolt.cleanup();
    }

    public static Tuple mockTuple(long timestamp, String type, String data) {
        Tuple tuple = mock(Tuple.class);
        when(tuple.getSourceComponent()).thenReturn("TEST");
        when(tuple.getSourceStreamId()).thenReturn("TEST");
        when(tuple.getLongByField("timestamp")).thenReturn(timestamp);
        when(tuple.getStringByField("type")).thenReturn(type);
        when(tuple.getStringByField("data")).thenReturn(data);
        return tuple;
    }

}
