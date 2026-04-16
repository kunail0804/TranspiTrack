package fr.utc.miage.transpitrack.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class SportTypeTest {

    @Test
    void constructorShouldSetFields() {
        List<Sport> sports = List.of(new Sport());
        SportType sportType = new SportType(1L, "Endurance", "Long efforts", sports);

        assertEquals(1L, sportType.getId());
        assertEquals("Endurance", sportType.getName());
        assertEquals("Long efforts", sportType.getDescription());
        assertEquals(sports, sportType.getSports());
    }

    @Test
    void defaultConstructorShouldHaveNullId() {
        assertNull(new SportType().getId());
    }

    @Test
    void settersShouldUpdateFields() {
        SportType sportType = new SportType();
        List<Sport> sports = List.of(new Sport());

        sportType.setName("Strength");
        sportType.setDescription("Muscle training");
        sportType.setSports(sports);

        assertEquals("Strength", sportType.getName());
        assertEquals("Muscle training", sportType.getDescription());
        assertEquals(sports, sportType.getSports());
    }
}
