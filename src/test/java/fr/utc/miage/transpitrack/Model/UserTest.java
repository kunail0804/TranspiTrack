package fr.utc.miage.transpitrack.Model;

import java.lang.reflect.Field;

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
    void constructorShouldInitializeAllFields() {
        User user = new User("Alice", "Dupont", "alice@example.com", "password", 25, 175.0, Gender.MALE, 70.0, "Paris");
        assertEquals("Alice", user.getFirstName());
        assertEquals("Dupont", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(25, user.getAge());
        assertEquals(175.0, user.getHeight(), 0.0);
        assertEquals(Gender.MALE, user.getGender());
        assertEquals(70.0, user.getWeight(), 0.0);
        assertEquals("Paris", user.getCity());
    }

    @Test
    void getIdShouldReturnValueSetByPersistenceLayer() throws Exception {
        User user = new User();
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 42L);
        assertEquals(42L, user.getId());
    }

    @Test
    void setEmailShouldUpdateEmail() {
        User user = new User();
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void setPasswordShouldUpdatePassword() {
        User user = new User();
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }
}
