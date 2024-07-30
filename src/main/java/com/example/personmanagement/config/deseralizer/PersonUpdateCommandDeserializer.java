package com.example.personmanagement.config.deseralizer;

import com.example.personmanagement.config.updateRegistrar.UpdateCommandTypeRegistrar;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonUpdateCommandDeserializer extends JsonDeserializer<UpdatePersonCommand> {

    private final Map<String, Class<? extends UpdatePersonCommand>> commandTypeMap = new HashMap<>();


    public PersonUpdateCommandDeserializer(List<UpdateCommandTypeRegistrar> updateCommandTypeRegistrars) {
        updateCommandTypeRegistrars.forEach(commandTypeRegistrar -> commandTypeRegistrar.registerUpdateCommandTypes(commandTypeMap));
    }

    @Override
    public UpdatePersonCommand deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);
        String type = node.get("type").asText();

        Class<? extends UpdatePersonCommand> commandClass = commandTypeMap.get(type);
        if (commandClass == null) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        return codec.treeToValue(node, commandClass);
    }

}
