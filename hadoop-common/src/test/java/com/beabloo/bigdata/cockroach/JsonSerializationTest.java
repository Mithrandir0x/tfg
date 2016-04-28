package com.beabloo.bigdata.cockroach;

import com.beabloo.bigdata.cockroach.model.CockroachEventHttpRequestContainer;
import com.beabloo.bigdata.cockroach.model.ParamsContainer;
import com.beabloo.bigdata.cockroach.serdes.ParamsContainerFastDeserializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Test;

import java.io.IOException;
import java.net.URLDecoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonSerializationTest {

    @Test
    public void serializationTest() throws IOException {
        String json = "{\n" +
                "    \"events\": [\n" +
                "        {\n" +
                "            \"extraParams\": {\n" +
                "                \"extra1\": \"10\",\n" +
                "                \"extra2\": \"20\"\n" +
                "            },\n" +
                "            \"paramsValues\": {\n" +
                "                \"device\": \"IDV1203211232\",\n" +
                "                \"event\": 3,\n" +
                "                \"organization\": \"37929\",\n" +
                "                \"oui\": \"AA:BB:CC\",\n" +
                "                \"power\": \"-74\",\n" +
                "                \"sensor\": \"5432\",\n" +
                "                \"startevent\": \"1426101101\",\n" +
                "                \"tags\": \"click1,click2,click3\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("UNK", Version.unknownVersion());
        simpleModule.addDeserializer(ParamsContainer.class, new ParamsContainerFastDeserializer());
        objectMapper.registerModule(simpleModule);

        CockroachEventHttpRequestContainer container =  objectMapper.readValue(json, CockroachEventHttpRequestContainer.class);

        assertEquals(1, container.getEvents().size());

        ParamsContainer paramsContainer = container.getEvents().get(0);

        String paramsValue = "{\"device\":\"IDV1203211232\",\"event\":3,\"organization\":\"37929\",\"oui\":\"AA:BB:CC\",\"power\":\"-74\",\"sensor\":\"5432\",\"startevent\":\"1426101101\",\"tags\":\"click1,click2,click3\"}";
        assertEquals(paramsValue, paramsContainer.getParamsValues());

        String extraValue = "{\"extra1\":\"10\",\"extra2\":\"20\"}";
        assertEquals(extraValue, paramsContainer.getExtraParams());
    }

    @Test
    public void testPythonGeneratedJson() throws Exception {
        String json = "%7B%22events%22%3A+%5B%7B%22paramsValues%22%3A+%7B%22organization_id%22%3A+39159%2C+%22hotspot_id" +
                "%22%3A+57469%2C+%22oui%22%3A+%2255%3A3b%3Aee%22%2C+%22sensor_id%22%3A+5176%2C+%22power%22%3A+-63%2C+%22" +
                "start_event%22%3A+1461827857%2C+%22md5_mac%22%3A+%220c29f6c410c7a5ebcc8551a0ff5fddd7%22%2C+%22event%22%" +
                "3A+3%7D%2C+%22extraParams%22%3A+%7B%7D%7D%2C+%7B%22paramsValues%22%3A+%7B%22organization_id%22%3A+39159" +
                "%2C+%22hotspot_id%22%3A+57469%2C+%22oui%22%3A+%22a0%3A24%3A50%22%2C+%22sensor_id%22%3A+5171%2C+%22power" +
                "%22%3A+-24%2C+%22start_event%22%3A+1461827857%2C+%22md5_mac%22%3A+%22157e430ebd1d5cd54149e36f2cd04a42%2" +
                "2%2C+%22event%22%3A+3%7D%2C+%22extraParams%22%3A+%7B%7D%7D%5D%7D";

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("UNK", Version.unknownVersion());
        simpleModule.addDeserializer(ParamsContainer.class, new ParamsContainerFastDeserializer());
        objectMapper.registerModule(simpleModule);

        String urlDecodedJson = URLDecoder.decode(json, "UTF-8");

        assertNotNull(urlDecodedJson);
        assertEquals(urlDecodedJson, "{\"events\": [{\"paramsValues\": {\"organization_id\": 39159, \"hotspot_id\": 57469, \"oui\": \"55:3b:ee\", \"sensor_id\": 5176, \"power\": -63, \"start_event\": 1461827857, \"md5_mac\": \"0c29f6c410c7a5ebcc8551a0ff5fddd7\", \"event\": 3}, \"extraParams\": {}}, {\"paramsValues\": {\"organization_id\": 39159, \"hotspot_id\": 57469, \"oui\": \"a0:24:50\", \"sensor_id\": 5171, \"power\": -24, \"start_event\": 1461827857, \"md5_mac\": \"157e430ebd1d5cd54149e36f2cd04a42\", \"event\": 3}, \"extraParams\": {}}]}");

        CockroachEventHttpRequestContainer container =  objectMapper.readValue(urlDecodedJson, CockroachEventHttpRequestContainer.class);

        assertEquals(2, container.getEvents().size());

        ParamsContainer paramsContainer = container.getEvents().get(0);
        assertParamsValuesAndExtraParams(
                paramsContainer,
                "{\"organization_id\":39159,\"hotspot_id\":57469,\"oui\":\"55:3b:ee\",\"sensor_id\":5176,\"power\":-63,\"start_event\":1461827857,\"md5_mac\":\"0c29f6c410c7a5ebcc8551a0ff5fddd7\",\"event\":3}",
                "{}"
                );

        paramsContainer = container.getEvents().get(1);
        assertParamsValuesAndExtraParams(
                paramsContainer,
                "{\"organization_id\":39159,\"hotspot_id\":57469,\"oui\":\"a0:24:50\",\"sensor_id\":5171,\"power\":-24,\"start_event\":1461827857,\"md5_mac\":\"157e430ebd1d5cd54149e36f2cd04a42\",\"event\":3}",
                "{}"
        );
    }

    private void assertParamsValuesAndExtraParams(ParamsContainer paramsContainer, String paramsValues, String extraParams) {
        assertEquals(paramsValues, paramsContainer.getParamsValues());
        assertEquals(extraParams, paramsContainer.getExtraParams());
    }

}
