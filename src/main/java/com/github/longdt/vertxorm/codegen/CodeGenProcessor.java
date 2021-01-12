package com.github.longdt.vertxorm.codegen;

import com.github.longdt.vertxorm.annotation.Repository;
import com.google.auto.service.AutoService;
import com.google.common.base.Throwables;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@AutoService(Processor.class)
public class CodeGenProcessor extends AbstractProcessor {
    private RepositoryDeclaration.Factory repositoryDF;
    private EntityDeclaration.Factory entityDF;
    private Messager messager;

    @Override
    public void init(ProcessingEnvironment env) {
        super.init(env);
        var elements = processingEnv.getElementUtils();
        var types = processingEnv.getTypeUtils();
        messager = env.getMessager();
        repositoryDF = new RepositoryDeclaration.Factory(elements, messager);
        entityDF = new EntityDeclaration.Factory(elements, types, messager);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            doProcess(roundEnv);
        } catch (Throwable e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Failed to process @Repository annotations:\n"
                    + Throwables.getStackTraceAsString(e));
        }
        return false;
    }

    private void doProcess(RoundEnvironment roundEnv) throws IOException {
        // Iterate over the classes and methods that are annotated with @Repository.
        for (Element element : roundEnv.getElementsAnnotatedWith(Repository.class)) {
            Optional<RepositoryDeclaration> declaration = repositoryDF.createIfValid(element);
            if (declaration.isPresent()) {
                var repositoryDeclaration = declaration.get();
                Optional<EntityDeclaration> entityDeclarationOpt = entityDF.createIfValid(repositoryDeclaration);
                if (entityDeclarationOpt.isEmpty()) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Failed to process repository: " + repositoryDeclaration.className());
                    continue;
                }
                var entityDeclaration = entityDeclarationOpt.get();
                var descriptor = RepositoryDescriptor.create(repositoryDeclaration, entityDeclaration);
                new EntityTableWriter(processingEnv).writeColumns(entityDeclaration);
                new IdAccessorWriter(processingEnv).writeIdAccessor(entityDeclaration, repositoryDeclaration.dialect());
                new ParametersMapperWriter(processingEnv).writeParametersMapper(entityDeclaration);
                new RowMapperWriter(processingEnv).writeRowMapper(entityDeclaration);
                new RepositoryWriter(processingEnv).writeRepository(descriptor);
            }

            System.out.println(declaration);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Repository.class.getName());
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
