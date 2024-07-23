package com.example.personmanagement.config;

import com.example.personmanagement.model.person.CreatePersonCommand;
import com.example.personmanagement.model.person.UpdatePersonCommand;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class CommandConfiguration {

    private final List<CommandTypeRegistrar<CreatePersonCommand>> createCommandRegistrars;
    private final List<CommandTypeRegistrar<UpdatePersonCommand>> updateCommandRegistrars;

    @Bean
    public SimpleModule commandDeserializers() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CreatePersonCommand.class, new GenericCommandDeserializer<>(createCommandRegistrars));
        module.addDeserializer(UpdatePersonCommand.class, new GenericCommandDeserializer<>(updateCommandRegistrars));
        return module;
    }
}
