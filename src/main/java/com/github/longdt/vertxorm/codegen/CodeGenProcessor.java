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
import java.util.Optional;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.github.longdt.vertxorm.annotation.Repository")
public class CodeGenProcessor extends AbstractProcessor {
    private RepositoryDeclaration.Factory declarationFactory;
    private Messager messager;
    private Elements elements;
    private Types types;

    @Override
    public void init(ProcessingEnvironment env) {
        super.init(env);
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        messager = env.getMessager();
        declarationFactory = new RepositoryDeclaration.Factory(elements, messager);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            doProcess(roundEnv);
        } catch (Throwable e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Failed to process @AutoFactory annotations:\n"
                    + Throwables.getStackTraceAsString(e));
        }
        return false;
    }

    private void doProcess(RoundEnvironment roundEnv) {
        // Iterate over the classes and methods that are annotated with @Repository.
        for (Element element : roundEnv.getElementsAnnotatedWith(Repository.class)) {
            Optional<RepositoryDeclaration> declaration = declarationFactory.createIfValid(element);
            System.out.println(declaration);
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
