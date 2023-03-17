package com.github.learndifferent.mtm.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomInstantDeserializerTest {

    @InjectMocks
    private CustomInstantDeserializer deserializer;
    @Mock
    private JsonParser jsonParser;
    @Mock
    private DeserializationContext deserializationContext;

    @Test
    void shouldDeserializeValidInstant() throws IOException {
        String validInstant = "2022-03-20T10:15:30Z";
        when(jsonParser.getText()).thenReturn(validInstant);

        Instant expected = Instant.parse(validInstant);
        Instant actual = deserializer.deserialize(jsonParser, deserializationContext);

        assertEquals(expected, actual);
    }
}
