package model;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.json.Json;
import io.vertx.core.json.jackson.DatabindCodec;

import javax.persistence.AttributeConverter;
import java.util.Map;

public class ArgumentsConverter implements AttributeConverter<Map<String, ArgumentDescription>, Object> {
    @Override
    public Object convertToDatabaseColumn(Map<String, ArgumentDescription> attribute) {
        return Json.encode(attribute);
    }

    @Override
    public Map<String, ArgumentDescription> convertToEntityAttribute(Object dbData) {
        return ((DatabindCodec) Json.CODEC).fromString((String) dbData, new TypeReference<>() {});
    }
}
