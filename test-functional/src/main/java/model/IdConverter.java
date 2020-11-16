package model;

import javax.persistence.AttributeConverter;

public class IdConverter implements AttributeConverter<Long, String> {
    @Override
    public String convertToDatabaseColumn(Long attribute) {
        return attribute.toString();
    }

    @Override
    public Long convertToEntityAttribute(String dbData) {
        return Long.parseLong(dbData);
    }
}
