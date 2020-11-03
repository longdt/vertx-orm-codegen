package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Predicate;

public class ArgumentDescription {
    private String name;
    private ValueType type;
    private String description;

    public static enum ValueType {
        INTEGER(o -> o instanceof Long),
        DECIMAL(o -> {
            if (o instanceof String) {
                try {
                    new BigDecimal((String) o);
                    return true;
                } catch (NumberFormatException ignored) {}
            }
            return false;
        }),
        STRING(o -> o instanceof String),
        DATE(o -> {
            if (o instanceof String) {
                try {
                    LocalDate.parse((CharSequence) o);
                    return true;
                } catch (Exception ignored) {}
            }
            return false;
        });

        private Predicate<Object> predicate;

        ValueType(Predicate<Object> predicate) {
            this.predicate = predicate;
        }

        public boolean isValid(Object value) {
            return predicate.test(value);
        }
    }

    public String getName() {
        return name;
    }

    public ArgumentDescription setName(String name) {
        this.name = name;
        return this;
    }

    public ValueType getType() {
        return type;
    }

    public ArgumentDescription setType(ValueType type) {
        this.type = type;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ArgumentDescription setDescription(String description) {
        this.description = description;
        return this;
    }
}
