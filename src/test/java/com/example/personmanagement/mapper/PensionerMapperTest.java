package com.example.personmanagement.mapper;

import com.example.personmanagement.exception.InvalidStrategyTypeException;
import com.example.personmanagement.model.pensioner.Pensioner;
import com.example.personmanagement.model.pensioner.PensionerDto;
import com.example.personmanagement.model.person.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PensionerMapperTest {

    private final PensionerMapper pensionerMapper = new PensionerMapper();

    @Test
    void supports() {
        assertTrue(pensionerMapper.supports("PENSIONER"));
        assertFalse(pensionerMapper.supports("EMPLOYEE"));
    }

    @Test
    void toDto() {
        Pensioner pensioner = Pensioner.builder()
                .id(1L)
                .name("Alice")
                .surname("Smith")
                .pesel("1234567890")
                .height(165)
                .weight(70)
                .emailAddress("alice.smith@example.com")
                .pensionAmount(1500.0)
                .workedYears(40)
                .build();

        PensionerDto pensionerDto = (PensionerDto) pensionerMapper.toDto(pensioner);

        assertEquals(1L, pensionerDto.getId());
        assertEquals("Alice", pensionerDto.getName());
        assertEquals("Smith", pensionerDto.getSurname());
        assertEquals("1234567890", pensionerDto.getPesel());
        assertEquals(165, pensionerDto.getHeight());
        assertEquals(70, pensionerDto.getWeight());
        assertEquals("alice.smith@example.com", pensionerDto.getEmailAddress());
        assertEquals(1500.0, pensionerDto.getPensionAmount());
        assertEquals(40, pensionerDto.getWorkedYears());
    }

    @Test
    void toDto_withInvalidType() {
        Person invalidPerson = new Person() {
        };
        assertThrows(InvalidStrategyTypeException.class, () -> pensionerMapper.toDto(invalidPerson));
    }
}