package com.example.personmanagement.strategy;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.strategy.PersonCreationStrategy;
import com.example.personmanagement.strategy.PersonUpdateStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PersonStrategyFacade {

    private final Map<String, PersonCreationStrategy> creationStrategies;
    private final Map<String, PersonUpdateStrategy> updateStrategies;

    public PersonCreationStrategy getCreationStrategy(String type) {
        String key = type.toLowerCase() + "CreationStrategy";
        PersonCreationStrategy creationStrategy = creationStrategies.get(key);

        if (creationStrategy == null) {
            throw new InvalidStrategyTypeException("Missing strategy type: " + key);
        }
        return creationStrategy;
    }

    public PersonUpdateStrategy getUpdateStrategy(String type) {
        String key = type.toLowerCase() + "UpdateStrategy";
        PersonUpdateStrategy updateStrategy = updateStrategies.get(key);

        if (updateStrategy == null) {
            throw new InvalidStrategyTypeException("Missing strategy type: " + key);
        }
        return updateStrategy;
    }
}
