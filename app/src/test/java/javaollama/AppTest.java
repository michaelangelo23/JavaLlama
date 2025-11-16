package javaollama;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test void testAppCreation() {
        App app = new App();
        assertNotNull(app, "app should be created");
    }
}
