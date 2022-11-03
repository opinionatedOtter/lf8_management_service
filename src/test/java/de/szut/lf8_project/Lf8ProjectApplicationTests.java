package de.szut.lf8_project;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Lf8ProjectApplicationTests {

    @Test
    @Disabled
    // Dieser Test funktioniert (natürlich) nicht in der Pipeline, weil er eine lokale DB vorraussetzt
    void contextLoads() {
    }

}
