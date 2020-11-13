package com.github.longdt.vertxorm.codegen;

import com.google.auto.value.AutoValue;

import javax.lang.model.type.TypeMirror;

@AutoValue
abstract class RepositoryDescriptor {
    abstract PackageAndClass name();
    abstract TypeMirror extendingType();
    abstract RepositoryDeclaration repositoryDeclaration();
    abstract EntityDeclaration entityDeclaration();

    public static RepositoryDescriptor create(RepositoryDeclaration repositoryDeclaration, EntityDeclaration entityDeclaration) {
        return new AutoValue_RepositoryDescriptor(repositoryDeclaration.getRepositoryName(),
                repositoryDeclaration.extendingType().asType(),
                repositoryDeclaration,
                entityDeclaration);
    }
}
