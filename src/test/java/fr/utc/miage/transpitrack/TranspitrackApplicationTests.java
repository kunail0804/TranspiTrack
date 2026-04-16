package fr.utc.miage.transpitrack;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TranspitrackApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
    void mainMethodRuns() {
        assertDoesNotThrow(() -> TranspitrackApplication.main(new String[] {}));
    }

}
