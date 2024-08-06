package com.example.personmanagement.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandDeserializer<T> extends JsonDeserializer<T> {

    private final Map<String, Class<? extends T>> commandTypeMap = new HashMap<>();

    public CommandDeserializer(List<CommandTypeRegistrar<T>> commandTypeRegistrars) {
        commandTypeRegistrars.forEach(commandTypeRegistrar -> commandTypeRegistrar.registerCommandTypes(commandTypeMap));
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);
        String type = node.get("type").asText();

        Class<? extends T> commandClass = commandTypeMap.get(type);
        if (commandClass == null) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        return codec.treeToValue(node, commandClass);
    }
}
