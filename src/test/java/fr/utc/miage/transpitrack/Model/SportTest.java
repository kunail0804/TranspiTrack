package fr.utc.miage.transpitrack.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class SportTest {

    @Test
    void constructorShouldSetFields() {
        Sport sport = new Sport("Running", "Endurance sport");

        assertEquals("Running", sport.getName());
        assertEquals("Endurance sport", sport.getDescription());
    }

    @Test
    void defaultConstructorShouldHaveNullId() {
        assertNull(new Sport().getId());
    }

    @Test
    void settersShouldUpdateFields() {
        Sport sport = new Sport();
        SportType sportType = new SportType();
        List<UserSport> users = List.of();

        sport.setName("Cycling");
        sport.setDescription("Speed sport");
        sport.setSportType(sportType);
        sport.setUsers(users);

        assertEquals("Cycling", sport.getName());
        assertEquals("Speed sport", sport.getDescription());
        assertEquals(sportType, sport.getSportType());
        assertEquals(users, sport.getUsers());
    }
}
