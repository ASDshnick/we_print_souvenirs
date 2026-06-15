package com.weprintsouvenirs.we_print_souvenirs;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class WePrintSouvenirsApplicationTests {

    @Test
    void mainStartsSpringApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            WePrintSouvenirsApplication.main(new String[]{"--test"});

            springApplication.verify(() -> SpringApplication.run(WePrintSouvenirsApplication.class, new String[]{"--test"}));
        }
    }
}
