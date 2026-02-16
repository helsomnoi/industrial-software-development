package bank.runner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class ConsoleRunnerTest {

    @Autowired(required = false)
    private ConsoleRunner consoleRunner;

    @Test
    void contextLoads() {
        assertNull(consoleRunner);
    }
}