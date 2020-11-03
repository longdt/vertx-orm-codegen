package com.github.longdt.vertxorm.codegen;

import com.github.longdt.vertxorm.annotation.Driver;
import com.github.longdt.vertxorm.annotation.Repository;
import com.google.auto.value.AutoValue;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import javax.annotation.processing.Messager;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Iterables.getOnlyElement;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.PACKAGE;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.typesIn;
import static javax.tools.Diagnostic.Kind.ERROR;

@AutoValue
abstract class RepositoryDeclaration {
    abstract TypeElement targetType();
    abstract Element target();
    abstract Optional<String> className();
    abstract TypeElement extendingType();
    abstract Driver driver();
    abstract AnnotationMirror mirror();
    abstract ImmutableMap<String, AnnotationValue> valuesMap();

    public static class Factory {
        private final Elements elements;
        private final Messager messager;

        Factory(Elements elements, Messager messager) {
            this.elements = elements;
            this.messager = messager;
        }

        Optional<RepositoryDeclaration> createIfValid(Element element) {
            checkNotNull(element);
            AnnotationMirror mirror = Mirrors.getAnnotationMirror(element, Repository.class).get();
            checkArgument(Mirrors.getQualifiedName(mirror.getAnnotationType()).
                    contentEquals(Repository.class.getName()));
            Map<String, AnnotationValue> values =
                    Mirrors.simplifyAnnotationValueMap(elements.getElementValuesWithDefaults(mirror));
            checkState(values.size() == 3);

            // className value is a string, so we can just call toString
            AnnotationValue classNameValue = values.get("className");
            String className = classNameValue.getValue().toString();
            if (!className.isEmpty() && !isValidIdentifier(className)) {
                messager.printMessage(ERROR,
                        String.format("\"%s\" is not a valid Java identifier", className),
                        element, mirror, classNameValue);
                return Optional.empty();
            }

            AnnotationValue extendingValue = checkNotNull(values.get("extending"));
            TypeElement extendingType = AnnotationValues.asType(extendingValue);
            if (extendingType == null) {
                messager.printMessage(ERROR, "Unable to find the type: " + extendingValue.getValue(),
                        element, mirror, extendingValue);
                return Optional.empty();
            } else if (!isValidSupertypeForClass(extendingType)) {
                messager.printMessage(ERROR,
                        String.format("%s is not a valid supertype for a factory. "
                                        + "Supertypes must be non-final classes.",
                                extendingType.getQualifiedName()),
                        element, mirror, extendingValue);
                return Optional.empty();
            }
            ImmutableList<ExecutableElement> noParameterConstructors =
                    FluentIterable.from(ElementFilter.constructorsIn(extendingType.getEnclosedElements()))
                            .filter(new Predicate<ExecutableElement>() {
                                @Override public boolean apply(ExecutableElement constructor) {
                                    return constructor.getParameters().isEmpty();
                                }
                            })
                            .toList();
            if (noParameterConstructors.isEmpty()) {
                messager.printMessage(ERROR,
                        String.format("%s is not a valid supertype for a factory. "
                                        + "Factory supertypes must have a no-arg constructor.",
                                extendingType.getQualifiedName()),
                        element, mirror, extendingValue);
                return Optional.empty();
            } else if (noParameterConstructors.size() > 1) {
                throw new IllegalStateException("Multiple constructors with no parameters??");
            }

            AnnotationValue driverValue = checkNotNull(values.get("driver"));
            Driver driver = AnnotationValues.asEnum(driverValue, Driver.class);

            return Optional.of(
                    new AutoValue_RepositoryDeclaration(
                            getAnnotatedType(element),
                            element,
                            className.isEmpty() ? Optional.empty() : Optional.of(className),
                            extendingType,
                            driver,
                            mirror,
                            ImmutableMap.copyOf(values)));
        }


    }

    private static TypeElement getAnnotatedType(Element element) {
        List<TypeElement> types = ImmutableList.of();
        while (types.isEmpty()) {
            types = typesIn(Arrays.asList(element));
            element = element.getEnclosingElement();
        }
        return getOnlyElement(types);
    }

    static boolean isValidIdentifier(String identifier) {
        return SourceVersion.isIdentifier(identifier) && !SourceVersion.isKeyword(identifier);
    }

    static boolean isValidSupertypeForClass(TypeElement type) {
        if (!type.getKind().equals(CLASS)) {
            return false;
        }
        if (type.getModifiers().contains(FINAL)) {
            return false;
        }
        if (!type.getEnclosingElement().getKind().equals(PACKAGE)
                && !type.getModifiers().contains(STATIC)) {
            return false;
        }
        if (type.getSimpleName().length() == 0) {
            return false;
        }
        return true;
    }
}
