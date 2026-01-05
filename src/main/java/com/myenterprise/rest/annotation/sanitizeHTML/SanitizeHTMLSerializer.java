package com.myenterprise.rest.annotation.sanitizeHTML;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class SanitizeHTMLSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(
            String value,
            JsonGenerator generator,
            SerializerProvider provider
    ) throws IOException {
        if (value == null || value.indexOf('<') < 0) {
            generator.writeString(value);
            return;
        }
        generator.writeString(SanitizerHTML.sanitizeValue(value));
    }
}
