package model;

import javax.persistence.AttributeConverter;

public class ActiveConverter implements AttributeConverter<Boolean, Byte> {
    @Override
    public Byte convertToDatabaseColumn(Boolean attribute) {
        return attribute ? (byte) 1 : 0;
    }

    @Override
    public Boolean convertToEntityAttribute(Byte dbData) {
        return dbData > 0;
    }
}
