package com.example.personmanagement.config;

import java.util.Map;

public interface CommandTypeRegistrar<T> {
    void registerCommandTypes(Map<String, Class<? extends T>> commandTypeMap);
}
