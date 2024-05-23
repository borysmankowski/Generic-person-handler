package com.example.personmanagement.mapper.updateRegistrar;

import com.example.personmanagement.mapper.deserializer.PersonUpdateCommandDeserializer;
import com.example.personmanagement.person.model.UpdatePersonCommand;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class PersonUpdateConfiguration {
    @Bean
    public Module personUpdateDeserializers(List<UpdateCommandTypeRegistrar> registrars) {
        return new SimpleModule()
                .addDeserializer(UpdatePersonCommand.class, new PersonUpdateCommandDeserializer(registrars));

    }
}