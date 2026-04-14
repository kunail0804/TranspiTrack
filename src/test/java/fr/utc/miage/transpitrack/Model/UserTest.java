package fr.utc.miage.transpitrack.Model;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void settersShouldUpdateAllFields() {
        User user = new User();

        user.setFirstName("Bob");
        user.setName("Martin");
        user.setEmail("bob@example.com");
        user.setPassword("newpass");
        user.setAge(30);
        user.setHeight(180.0);
        user.setGender(Gender.MALE);
        user.setWeight(80.0);
        user.setCity("Lyon");

        assertEquals("Bob", user.getFirstName());
        assertEquals("Martin", user.getName());
        assertEquals("bob@example.com", user.getEmail());
        assertEquals("newpass", user.getPassword());
        assertEquals(30, user.getAge());
        assertEquals(180.0, user.getHeight(), 0.0);
        assertEquals(Gender.MALE, user.getGender());
        assertEquals(80.0, user.getWeight(), 0.0);
        assertEquals("Lyon", user.getCity());
    }

    @Test
    void relationshipGettersShouldReturnEmptyListsByDefault() {
        User user = new User();
        assertNotNull(user.getSportsPreference());
        assertNotNull(user.getFriends());
        assertNotNull(user.getGoals());
        assertNotNull(user.getComments());
        assertNotNull(user.getCreatedChallenges());
        assertNotNull(user.getJoinedChallenges());
    }

    // ── Logique métier ─────────────────────────────────────────────

    @Test
    void addPreferenceShouldAddUserSportToList() {
        User user = new User();
        UserSport us = new UserSport();

        user.addPreference(us);

        assertEquals(1, user.getSportsPreference().size());
        assertEquals(us, user.getSportsPreference().get(0));
    }

    @Test
    void addPreferenceShouldSupportMultipleEntries() {
        User user = new User();
        UserSport us1 = new UserSport();
        UserSport us2 = new UserSport();

        user.addPreference(us1);
        user.addPreference(us2);

        assertEquals(2, user.getSportsPreference().size());
    }

    @Test
    void deletePreferenceShouldRemoveUserSportFromList() {
        User user = new User();
        UserSport us = new UserSport();

        user.addPreference(us);
        user.deletePreference(us);

        assertEquals(0, user.getSportsPreference().size());
    }

    @Test
    void deletePreferenceShouldOnlyRemoveTargetEntry() {
        User user = new User();
        UserSport us1 = new UserSport();
        UserSport us2 = new UserSport();

        user.addPreference(us1);
        user.addPreference(us2);
        user.deletePreference(us1);

        assertEquals(1, user.getSportsPreference().size());
        assertEquals(us2, user.getSportsPreference().get(0));
    }
}
