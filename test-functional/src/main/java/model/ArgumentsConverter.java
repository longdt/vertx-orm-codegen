package model;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.json.Json;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.core.json.jackson.JacksonCodec;
import io.vertx.core.spi.json.JsonCodec;

import javax.persistence.AttributeConverter;
import java.util.Map;

public class ArgumentsConverter implements AttributeConverter<Map<String, ArgumentDescription>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, ArgumentDescription> attribute) {
        return Json.encode(attribute);
    }

    @Override
    public Map<String, ArgumentDescription> convertToEntityAttribute(String dbData) {
        return ((DatabindCodec) Json.CODEC).fromString(dbData, new TypeReference<>() {
        });
    }
}
