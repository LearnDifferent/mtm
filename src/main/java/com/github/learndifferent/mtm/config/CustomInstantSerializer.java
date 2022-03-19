package com.github.learndifferent.mtm.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Instant Serializer
 *
 * @author zhou
 * @date 2022/3/19
 */
@Component
public class CustomInstantSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        String format = DateTimeFormatter.ISO_INSTANT.format(instant);
        jsonGenerator.writeString(format);
    }
}
