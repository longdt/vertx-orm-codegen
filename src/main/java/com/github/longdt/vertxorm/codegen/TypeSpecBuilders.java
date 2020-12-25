package com.github.longdt.vertxorm.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.Modifier.*;

public class TypeSpecBuilders {
    public static void addInterfaceInstanceField(TypeSpec.Builder factory, ClassName className) {
        factory.addField(
                FieldSpec.builder(className, Constant.INSTANCE)
                        .addModifiers(PUBLIC, STATIC, FINAL)
                        .initializer("new $T() {}", className)
                        .build());
    }

    public static void addConstantField(TypeSpec.Builder factory, TypeMirror typeMirror, String fieldName) {
        factory.addField(
                FieldSpec.builder(ClassName.get(typeMirror), fieldName)
                        .addModifiers(PUBLIC, STATIC, FINAL)
                        .initializer("new $T()", typeMirror)
                        .build());
    }
}
