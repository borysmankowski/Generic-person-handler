package com.example.personmanagement.config.createRegistrar;

import com.example.personmanagement.config.CommandDeserializer;
import com.example.personmanagement.config.CommandTypeRegistrar;
import com.example.personmanagement.model.person.CreatePersonCommand;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class PersonCreateConfiguration {

    @Bean
    public Module personCreateDeserializers(List<CommandTypeRegistrar<CreatePersonCommand>> registrars) {
        return new SimpleModule()
                .addDeserializer(CreatePersonCommand.class, new CommandDeserializer<>(registrars));
    }
}
