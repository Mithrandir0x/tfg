package com.beabloo.bigdata.loggateway.integrations.kafka.serdes;

import com.beabloo.bigdata.model.RawLog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RawLogSerDeTest {

    @Test
    public void anotherTest() throws Exception {
        RawLog rawLog = new RawLog(
                0x7fff_ffff_ffff_ffffL,
                "/helloworld",
                "%7B%22events%22:%5B%7B%22extraParams%22:%7B%22userid%22:%2217127638%22%7D,%22pa" +
                "ramsValues%22:%7B%22event%22:1,%22organization%22:%2238041%22,%22post%22:%22203" +
                "626%22,%22startevent%22:%221455727751%22,%22tags%22:%22web%22%7D%7D%5D%7D");

        RawLogSerDe serDe = new RawLogSerDe();

        byte[] result = serDe.serialize("test", rawLog);

        String hexRepresentation = "01 7F FF FF FF FF FF FF FF 2F 68 65 6C 6C 6F 77 6F 72 " +
                "6C E4 E8 03 25 37 42 25 32 32 65 76 65 6E 74 73 25 32 32 3A 25 35 42 " +
                "25 37 42 25 32 32 65 78 74 72 61 50 61 72 61 6D 73 25 32 32 3A 25 37 " +
                "42 25 32 32 75 73 65 72 69 64 25 32 32 3A 25 32 32 31 37 31 32 37 36 " +
                "33 38 25 32 32 25 37 44 2C 25 32 32 70 61 72 61 6D 73 56 61 6C 75 65 " +
                "73 25 32 32 3A 25 37 42 25 32 32 65 76 65 6E 74 25 32 32 3A 31 2C 25 " +
                "32 32 6F 72 67 61 6E 69 7A 61 74 69 6F 6E 25 32 32 3A 25 32 32 33 38 " +
                "30 34 31 25 32 32 2C 25 32 32 70 6F 73 74 25 32 32 3A 25 32 32 32 30 " +
                "33 36 32 36 25 32 32 2C 25 32 32 73 74 61 72 74 65 76 65 6E 74 25 32 " +
                "32 3A 25 32 32 31 34 35 35 37 32 37 37 35 31 25 32 32 2C 25 32 32 74 " +
                "61 67 73 25 32 32 3A 25 32 32 77 65 62 25 32 32 25 37 44 25 37 44 25 " +
                "35 44 25 37 44";

        assertEquals(hexRepresentation, bytesToHex(result));

        RawLog test = serDe.deserialize("test", result);

        assertEquals(rawLog.getType(), test.getType());
        assertEquals(rawLog.getTimestamp(), test.getTimestamp());
        assertEquals(rawLog.getData(), test.getData());
    }

    @Test
    public void anotherTest2() throws Exception {
        RawLog rawLog = new RawLog(
                1461516145307l,
                "http://kafka1.local.vm:18081/log-gateway/activityTracking/wifi",
                "json={%22events%22:[{%22extraParams%22:{%22extra1%22:%2210%22,%22extra2%22:%2220%22},%22paramsValues%22:{%22device%22:%22IDV1203211232%22,%22event%22:3,%22organization%22:%2237929%22,%22oui%22:%22AA:BB:CC%22,%22power%22:%22-74%22,%22sensor%22:%225432%22,%22hotspot%22:%2257432%22,%22startEvent%22:%221461499200%22,%22tags%22:%22click1,click2,click3%22}}]}");

        RawLogSerDe serDe = new RawLogSerDe();

        byte[] result = serDe.serialize("test", rawLog);

        String hexRepresentation = "01 00 00 01 54 49 26 92 9B 68 74 74 70 3A 2F 2F 6B 61 66 6B 61 31 2E 6C 6F 63 61 6C 2E 76 6D 3A 31 38 30 38 31 2F 6C 6F 67 2D 67 61 74 65 77 61 79 2F 61 63 74 69 76 69 74 79 54 72 61 63 6B 69 6E 67 2F 77 69 66 E9 E4 05 6A 73 6F 6E 3D 7B 25 32 32 65 76 65 6E 74 73 25 32 32 3A 5B 7B 25 32 32 65 78 74 72 61 50 61 72 61 6D 73 25 32 32 3A 7B 25 32 32 65 78 74 72 61 31 25 32 32 3A 25 32 32 31 30 25 32 32 2C 25 32 32 65 78 74 72 61 32 25 32 32 3A 25 32 32 32 30 25 32 32 7D 2C 25 32 32 70 61 72 61 6D 73 56 61 6C 75 65 73 25 32 32 3A 7B 25 32 32 64 65 76 69 63 65 25 32 32 3A 25 32 32 49 44 56 31 32 30 33 32 31 31 32 33 32 25 32 32 2C 25 32 32 65 76 65 6E 74 25 32 32 3A 33 2C 25 32 32 6F 72 67 61 6E 69 7A 61 74 69 6F 6E 25 32 32 3A 25 32 32 33 37 39 32 39 25 32 32 2C 25 32 32 6F 75 69 25 32 32 3A 25 32 32 41 41 3A 42 42 3A 43 43 25 32 32 2C 25 32 32 70 6F 77 65 72 25 32 32 3A 25 32 32 2D 37 34 25 32 32 2C 25 32 32 73 65 6E 73 6F 72 25 32 32 3A 25 32 32 35 34 33 32 25 32 32 2C 25 32 32 68 6F 74 73 70 6F 74 25 32 32 3A 25 32 32 35 37 34 33 32 25 32 32 2C 25 32 32 73 74 61 72 74 45 76 65 6E 74 25 32 32 3A 25 32 32 31 34 36 31 34 39 39 32 30 30 25 32 32 2C 25 32 32 74 61 67 73 25 32 32 3A 25 32 32 63 6C 69 63 6B 31 2C 63 6C 69 63 6B 32 2C 63 6C 69 63 6B 33 25 32 32 7D 7D 5D 7D";

        assertEquals(hexRepresentation, bytesToHex(result));

        RawLog test = serDe.deserialize("test", result);

        assertEquals(rawLog.getType(), test.getType());
        assertEquals(rawLog.getTimestamp(), test.getTimestamp());
        assertEquals(rawLog.getData(), test.getData());
    }

    @Test
    public void problemHtmlEntitiesTest() throws Exception {
        String json = "json={%22events%22:[{%22extraParams%22:{%22extra1%22:%2210%22,%22extra2%22:%2220%22},%22paramsValues%22:{%22device%22:%22IDV1203211232%22,%22event%22:3,%22organization%22:%2237929%22,%22oui%22:%22AA:BB:CC%22,%22power%22:%22-74%22,%22sensor%22:%225432%22,%22hotspot%22:%2257432%22,%22startEvent%22:%221461499200%22,%22tags%22:%22click1,click2,click3%22}}]}";

        RawLog rawLog = new RawLog(
                1l,
                "http://kafka1.local.vm:18081/log-gateway/activityTracking/wifi",
                json);

        RawLogSerDe serDe = new RawLogSerDe();

        byte[] result = serDe.serialize("test", rawLog);

        String hex = "01 00 00 00 00 00 00 00 01 68 74 74 70 3A 2F 2F 6B 61 66 6B 61 31 2E " +
                "6C 6F 63 61 6C 2E 76 6D 3A 31 38 30 38 31 2F 6C 6F 67 2D 67 61 74 65 77 61 " +
                "79 2F 61 63 74 69 76 69 74 79 54 72 61 63 6B 69 6E 67 2F 77 69 66 E9 E4 05 " +
                "6A 73 6F 6E 3D 7B 25 32 32 65 76 65 6E 74 73 25 32 32 3A 5B 7B 25 32 32 65 " +
                "78 74 72 61 50 61 72 61 6D 73 25 32 32 3A 7B 25 32 32 65 78 74 72 61 31 25 " +
                "32 32 3A 25 32 32 31 30 25 32 32 2C 25 32 32 65 78 74 72 61 32 25 32 32 3A " +
                "25 32 32 32 30 25 32 32 7D 2C 25 32 32 70 61 72 61 6D 73 56 61 6C 75 65 73 " +
                "25 32 32 3A 7B 25 32 32 64 65 76 69 63 65 25 32 32 3A 25 32 32 49 44 56 31 " +
                "32 30 33 32 31 31 32 33 32 25 32 32 2C 25 32 32 65 76 65 6E 74 25 32 32 3A " +
                "33 2C 25 32 32 6F 72 67 61 6E 69 7A 61 74 69 6F 6E 25 32 32 3A 25 32 32 33 " +
                "37 39 32 39 25 32 32 2C 25 32 32 6F 75 69 25 32 32 3A 25 32 32 41 41 3A 42 " +
                "42 3A 43 43 25 32 32 2C 25 32 32 70 6F 77 65 72 25 32 32 3A 25 32 32 2D 37 " +
                "34 25 32 32 2C 25 32 32 73 65 6E 73 6F 72 25 32 32 3A 25 32 32 35 34 33 32 " +
                "25 32 32 2C 25 32 32 68 6F 74 73 70 6F 74 25 32 32 3A 25 32 32 35 37 34 33 " +
                "32 25 32 32 2C 25 32 32 73 74 61 72 74 45 76 65 6E 74 25 32 32 3A 25 32 32 " +
                "31 34 36 31 34 39 39 32 30 30 25 32 32 2C 25 32 32 74 61 67 73 25 32 32 3A " +
                "25 32 32 63 6C 69 63 6B 31 2C 63 6C 69 63 6B 32 2C 63 6C 69 63 6B 33 25 32 " +
                "32 7D 7D 5D 7D";

        assertEquals(hex, bytesToHex(result));

        RawLog test = serDe.deserialize("test", result);

        assertEquals(rawLog.getType(), test.getType());
        assertEquals(rawLog.getTimestamp(), test.getTimestamp());
        assertEquals(rawLog.getData(), test.getData());
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        char l, r;
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            l = hexArray[v >>> 4];
            r = hexArray[v & 0x0F];
            if ( j != bytes.length - 1 ) {
                buffer.append(String.format("%c%c ", l, r));
            } else {
                buffer.append(String.format("%c%c", l, r));
            }
        }
        return buffer.toString();
    }

}
