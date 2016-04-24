package com.beabloo.bigdata.cockroach.serdes;

import com.beabloo.bigdata.cockroach.model.ParamsContainer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class ParamsContainerFastDeserializer extends JsonDeserializer<ParamsContainer> {

    @Override
    public ParamsContainer deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode node = objectCodec.readTree(jsonParser);

        ParamsContainer paramsContainer = new ParamsContainer();
        paramsContainer.setParamsValues(node.get("paramsValues").toString());
        paramsContainer.setExtraParams(node.get("extraParams").toString());

        return paramsContainer;
    }

}
