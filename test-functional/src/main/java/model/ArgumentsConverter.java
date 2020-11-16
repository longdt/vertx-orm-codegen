package model;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.json.jackson.JacksonCodec;
import io.vertx.core.spi.json.JsonCodec;

import javax.persistence.AttributeConverter;
import java.util.Map;

public class ArgumentsConverter implements AttributeConverter<Map<String, ArgumentDescription>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, ArgumentDescription> attribute) {
        return JsonCodec.INSTANCE.toString(attribute);
    }

    @Override
    public Map<String, ArgumentDescription> convertToEntityAttribute(String dbData) {
        return JacksonCodec.fromString(dbData, new TypeReference<>() {
        });
    }
}
