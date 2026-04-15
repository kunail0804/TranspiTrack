package fr.utc.miage.transpitrack;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TranspitrackApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
    void mainMethodRuns() {
        SpringApplication app = mock(SpringApplication.class);
        TranspitrackApplication.main(new String[] {});
    }

}
