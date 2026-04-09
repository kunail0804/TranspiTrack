package fr.utc.miage.transpitrack.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fr.utc.miage.transpitrack.Model.Enum.Gender;

class UserTest {

    @Test
    void getIdShouldReturnNullWhenNotPersisted() {
        User user = new User();
        assertNull(user.getId());
    }

    @Test
    void testUserConstructor() {
        User user = new User("a", "a", "a@a.com", "password", 25, 175.0, Gender.MALE, 70.0, "City");
        assertEquals("a", user.getFirstName());
        assertEquals("a", user.getName());
        assertEquals("a@a.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(25, user.getAge());
        assertEquals(175.0, user.getHeight(), 0.0);
        assertEquals(Gender.MALE, user.getGender());
        assertEquals(70.0, user.getWeight(), 0.0);
        assertEquals("City", user.getCity());
    }
}
