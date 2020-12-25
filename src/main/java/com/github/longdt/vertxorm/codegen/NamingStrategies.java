package com.github.longdt.vertxorm.codegen;

import com.github.longdt.vertxorm.format.Case;

public class NamingStrategies {
    public static String resolveName(Case format, String name) {
        if (Case.SNAKE_CASE == format) {
            return camelToSnake(name);
        } else if (Case.SCREAMING_SNAKE_CASE == format) {
            return camelToScreamSnake(name);
        }
        return name;
    }

    public static String camelToScreamSnake(String name) {
        var builder = new StringBuilder(name.length() * 2);
        builder.append(Character.toUpperCase(name.charAt(0)));
        for (int i = 1; i < name.length(); ++i) {
            char ch = name.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append('_')
                        .append(ch);
            } else {
                builder.append(Character.toUpperCase(ch));
            }
        }
        return builder.toString();
    }

    public static String camelToSnake(String name) {
        var builder = new StringBuilder(name.length() * 2);
        builder.append(Character.toLowerCase(name.charAt(0)));
        for (int i = 1; i < name.length(); ++i) {
            char ch = name.charAt(i);
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
