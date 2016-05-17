package com.beabloo.bigdata.logpipeline.storm.classification.schemes;

import com.beabloo.bigdata.loggateway.integrations.kafka.serdes.RawLogSerDe;
import com.beabloo.bigdata.model.RawLog;
import org.apache.storm.tuple.Values;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

public class RawLogSchemeTest {

    @Test
    public void deserialize() throws Exception {
        final String hexBytes = "00000000000000000000000100087261772D6C6F6773000000010000000000000000000000000002000001C70000000000000001000001BB06D7055B00000000000130000001AC01000001544926929B687474703A2F2F6B61666B61312E6C6F63616C2E766D3A31383038312F6C6F672D676174657761792F6163746976697479547261636B696E672F776966E9E4056A736F6E3D7B2532326576656E74732532323A5B7B2532326578747261506172616D732532323A7B2532326578747261312532323A25323231302532322C2532326578747261322532323A25323232302532327D2C253232706172616D7356616C7565732532323A7B2532326465766963652532323A253232494456313230333231313233322532322C2532326576656E742532323A332C2532326F7267616E697A6174696F6E2532323A25323233373932392532322C2532326F75692532323A25323241413A42423A43432532322C253232706F7765722532323A2532322D37342532322C25323273656E736F722532323A253232353433322532322C253232686F7473706F742532323A25323235373433322532322C25323273746172744576656E742532323A253232313436313439393230302532322C253232746167732532323A253232636C69636B312C636C69636B322C636C69636B332532327D7D5D7D";
        byte[] bytes = hexStringToByteArray(hexBytes);

        assertEquals(499, bytes.length);

        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);

        byte[] skewedBytes = Arrays.copyOfRange(bytes, 71, bytes.length);

        assertEquals(bytes.length - 71, skewedBytes.length);

        RawLogSerDe serDe = new RawLogSerDe();
        RawLog rawLog = serDe.deserialize(null, skewedBytes);

        System.out.println(String.format("rawLog [%s]", rawLog));

        assertEquals(1461516145307l, rawLog.getTimestamp());
        assertEquals("http://kafka1.local.vm:18081/log-gateway/activityTracking/wifi", rawLog.getType());
        assertEquals("json={%22events%22:[{%22extraParams%22:{%22extra1%22:%2210%22,%22extra2%22:%2220%22},%22paramsValues%22:{%22device%22:%22IDV1203211232%22,%22event%22:3,%22organization%22:%2237929%22,%22oui%22:%22AA:BB:CC%22,%22power%22:%22-74%22,%22sensor%22:%225432%22,%22hotspot%22:%2257432%22,%22startEvent%22:%221461499200%22,%22tags%22:%22click1,click2,click3%22}}]}", rawLog.getData());
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
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