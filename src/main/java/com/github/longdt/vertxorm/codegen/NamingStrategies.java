package com.github.longdt.vertxorm.codegen;

import com.github.longdt.vertxorm.annotation.NamingStrategy;

public class NamingStrategies {
    public static String resolveName(NamingStrategy namingStrategy, String name) {
        if (NamingStrategy.SNAKE_CASE == namingStrategy) {
            return camelToSnake(name);
        }
        return name;
    }

    public static String camelToSnake(String str) {
        var builder = new StringBuilder(str.length() * 2);
        builder.append(Character.toLowerCase(str.charAt(0)));
        for (int i = 1; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append('_')
                        .append(Character.toLowerCase(ch));
            } else {
                builder.append(ch);
            }
        }

        return builder.toString();
    }
}
