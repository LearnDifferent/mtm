package com.github.learndifferent.mtm.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Instant Deserializer
 *
 * @author zhou
 * @date 2022/3/20
 */
@Component
public class CustomInstantDeserializer extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String text = jsonParser.getText();
        return Instant.parse(text);
    }
}
