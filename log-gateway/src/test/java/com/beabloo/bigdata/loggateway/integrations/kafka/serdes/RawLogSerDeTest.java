package com.beabloo.bigdata.loggateway.integrations.kafka.serdes;

import com.beabloo.bigdata.model.RawLog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RawLogSerDeTest {

    @Test
    public void anotherTest() throws Exception {
        RawLog rawLog = new RawLog(
                0l,
                "/helloworld",
                "%7B%22events%22:%5B%7B%22extraParams%22:%7B%22userid%22:%2217127638%22%7D,%22pa" +
                "ramsValues%22:%7B%22event%22:1,%22organization%22:%2238041%22,%22post%22:%22203" +
                "626%22,%22startevent%22:%221455727751%22,%22tags%22:%22web%22%7D%7D%5D%7D");

        RawLogSerDe serDe = new RawLogSerDe();

        byte[] result = serDe.serialize("test", rawLog);

        String test = "01 00 00 00 00 00 00 00 00 2F 68 65 6C 6C 6F 77 6F 72 " +
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

        assertEquals(test, bytesToHex(result));
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
