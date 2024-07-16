package com.example.personmanagement.config.deseralizer;

import com.example.personmanagement.config.createRegistrar.CreateCommandTypeRegistrar;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PersonCreateCommandDeserializer extends JsonDeserializer<CreatePersonCommand> {

    private final Map<String, Class<? extends CreatePersonCommand>> commandTypeMap = new HashMap<>();

    public PersonCreateCommandDeserializer(List<CreateCommandTypeRegistrar> createCommandTypeRegistrars) {
        createCommandTypeRegistrars.forEach(commandTypeRegistrar -> commandTypeRegistrar.registerCreateCommandTypes(commandTypeMap));
    }

    @Override
    public CreatePersonCommand deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);
        String type = node.get("type").asText();

        Class<? extends CreatePersonCommand> commandClass = commandTypeMap.get(type);
        if (commandClass == null) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        return codec.treeToValue(node, commandClass);
    }
}