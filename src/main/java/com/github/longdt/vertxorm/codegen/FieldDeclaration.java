package com.github.longdt.vertxorm.codegen;

import com.google.auto.value.AutoValue;

import javax.lang.model.type.TypeMirror;
import java.util.Optional;

@AutoValue
abstract class FieldDeclaration {
    abstract String fieldName();
    abstract TypeMirror javaType();
    abstract Optional<TypeMirror> sqlType();
    abstract Optional<TypeMirror> converter();
    abstract Optional<String> columnName();
}
