package com.github.longdt.vertxorm.codegen;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import java.io.IOException;

import static com.google.auto.common.GeneratedAnnotationSpecs.generatedAnnotationSpec;
import static com.squareup.javapoet.TypeSpec.classBuilder;

public class RepositoryWriter {
    private final Filer filer;
    private final Elements elements;
    private final SourceVersion sourceVersion;

    RepositoryWriter(ProcessingEnvironment processingEnv) {
        this.filer = processingEnv.getFiler();
        this.elements = processingEnv.getElementUtils();
        this.sourceVersion = processingEnv.getSourceVersion();
    }

    void writeRepository(RepositoryDescriptor descriptor)
            throws IOException {
        String factoryName = descriptor.name().className();
        TypeSpec.Builder factory =
                classBuilder(factoryName)
                        .addOriginatingElement(descriptor.declaration().targetType());
        generatedAnnotationSpec(
                elements,
                sourceVersion,
                CodeGenProcessor.class,
                "https://github.com/google/auto/tree/master/factory")
                .ifPresent(factory::addAnnotation);
        factory.superclass(TypeName.get(descriptor.extendingType()));
//        for (TypeMirror implementingType : descriptor.implementingTypes()) {
//            factory.addSuperinterface(TypeName.get(implementingType));
//        }
//
//        ImmutableSet<TypeVariableName> factoryTypeVariables = getFactoryTypeVariables(descriptor);
//
//        addFactoryTypeParameters(factory, factoryTypeVariables);
//        addConstructorAndProviderFields(factory, descriptor);
//        addFactoryMethods(factory, descriptor, factoryTypeVariables);
//        addImplementationMethods(factory, descriptor);
//        addCheckNotNullMethod(factory, descriptor);

        JavaFile.builder(descriptor.name().packageName(), factory.build())
                .skipJavaLangImports(true)
                .build()
                .writeTo(filer);
    }

}
