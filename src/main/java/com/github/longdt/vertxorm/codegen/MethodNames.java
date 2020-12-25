package com.github.longdt.vertxorm.codegen;

public class MethodNames {
    public static String toPropertyMethodSuffix(String fieldName) {
        return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }
}
