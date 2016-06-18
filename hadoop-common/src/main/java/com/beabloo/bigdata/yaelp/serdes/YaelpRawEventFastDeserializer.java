package com.beabloo.bigdata.yaelp.serdes;

import com.beabloo.bigdata.yaelp.model.YaelpRawEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class YaelpRawEventFastDeserializer extends JsonDeserializer<YaelpRawEvent> {

    @Override
    public YaelpRawEvent deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode node = objectCodec.readTree(jsonParser);

        YaelpRawEvent yaelpRawEvent = new YaelpRawEvent();
        yaelpRawEvent.setData(node.get("data").toString());
        yaelpRawEvent.setMeta(node.get("meta").toString());

        return yaelpRawEvent;
    }

}
