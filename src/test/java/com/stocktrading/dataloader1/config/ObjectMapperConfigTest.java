package com.stocktrading.dataloader1.config;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ObjectMapperConfigTest {

    @Test
    void shouldCreateObjectMapperBean() {
        ObjectMapperConfig config = new ObjectMapperConfig();
        ObjectMapper objectMapper = config.objectMapper();

        assertNotNull(objectMapper);
    }
}
