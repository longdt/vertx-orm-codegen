package com.github.longdt.vertxorm.codegen;

import com.google.auto.value.AutoValue;

import javax.lang.model.type.TypeMirror;

@AutoValue
abstract class RepositoryDescriptor {
    abstract PackageAndClass name();
    abstract TypeMirror extendingType();
    abstract RepositoryDeclaration declaration();
}
