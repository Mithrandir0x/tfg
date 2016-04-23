package com.beabloo.bigdata.cockroach.serdes;

import com.beabloo.bigdata.cockroach.model.ParamsContainer;
import org.apache.commons.io.IOExceptionWithCause;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

public class ParamsContainerFastDeserializer extends JsonDeserializer<ParamsContainer> {

    public ParamsContainer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode node = objectCodec.readTree(jsonParser);

        ParamsContainer paramsContainer = new ParamsContainer();
        paramsContainer.setParamsValues(node.get("paramsValues").toString());
        paramsContainer.setExtraParams(node.get("extraParams").toString());

        return paramsContainer;
    }

}
