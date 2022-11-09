package de.szut.lf8_project;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
class Lf8ProjectApplicationTests {

    @Test
    // Dieser Test funktioniert (natürlich) nicht in der Pipeline, weil er eine lokale DB vorraussetzt
    void contextLoads() {
    }

}
