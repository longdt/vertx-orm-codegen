package com.github.longdt.vertxorm.codegen;

import com.google.auto.value.AutoValue;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.tools.Diagnostic.Kind.ERROR;

@AutoValue
abstract class EntityDeclaration {
    abstract TypeElement targetType();

    abstract String tableName();

    abstract FieldDeclaration pkField();

    abstract boolean autoGenPK();

    abstract Map<String, FieldDeclaration> fieldsMap();

    public static class Factory {
        private final Elements elements;
        private final Types types;
        private final Messager messager;

        public Factory(Elements elements, Types types, Messager messager) {
            this.elements = elements;
            this.types = types;
            this.messager = messager;
        }

        Optional<EntityDeclaration> createIfValid(RepositoryDeclaration repositoryDeclaration) {
            var repoElement = (TypeElement) repositoryDeclaration.target();
            var declaredType = (DeclaredType) repoElement.getInterfaces().get(0);
            var entityElement = ((DeclaredType) declaredType.getTypeArguments().get(1)).asElement();
            var idType = declaredType.getTypeArguments().get(0);
            var entity = entityElement.getAnnotation(Entity.class);
            var tableName = entity.name();
            var hasNoParameterConstructor = ElementFilter.constructorsIn(entityElement.getEnclosedElements())
                    .stream()
                    .anyMatch(constructor -> constructor.getParameters().isEmpty());
            if (!hasNoParameterConstructor) {
                messager.printMessage(ERROR,
                        String.format("%s is not a valid no param constructor for entity. "
                                        + "Entity must have a no-arg constructor.",
                                entityElement.getSimpleName()),
                        entityElement, null, null);
                return Optional.empty();
            }
            var methods = ElementFilter.methodsIn(elements.getAllMembers((TypeElement) entityElement));
            var idOpt = findIdField(entityElement);
            if (idOpt.isEmpty()) {
                messager.printMessage(ERROR,
                        String.format("%s has no valid id. "
                                        + "Entity must have a id field.",
                                entityElement.getSimpleName()),
                        entityElement, null, null);
                return Optional.empty();
            }
            var pk = createPkField(idOpt.get(), methods);
            var autoGenPk = idOpt.get().getAnnotation(GeneratedValue.class) != null;
            TreeMap<String, FieldDeclaration> fields = createFields((TypeElement) entityElement, methods)
                    .stream()
                    .filter(field -> !field.fieldName().equals(pk.fieldName()))
                    .collect(TreeMap::new, (m, e) -> m.put(e.fieldName(), e), Map::putAll);
            return Optional.of(new AutoValue_EntityDeclaration((TypeElement) entityElement, tableName, pk, autoGenPk, fields));
        }

        Optional<VariableElement> findIdField(Element entityElement) {
            return ElementFilter.fieldsIn(entityElement.getEnclosedElements())
                    .stream()
                    .filter(f -> f.getAnnotation(Id.class) != null)
                    .findAny();
        }

        FieldDeclaration createPkField(VariableElement pkField, List<ExecutableElement> methods) {
            var pkName = pkField.getSimpleName().toString();
            var getter = methods.stream()
                    .filter(m -> isPropertyMethod(m, "get", pkName))
                    .findAny();
            if (getter.isEmpty()) {
                messager.printMessage(ERROR,
                        String.format("%s has no valid getter. "
                                        + "id must have getter method.",
                                pkName));
            }
            var setter = methods.stream()
                    .filter(m -> isPropertyMethod(m, "set", pkName))
                    .findAny();
            var converter = Optional.<TypeMirror>empty();
            Convert convert = pkField.getAnnotation(Convert.class);
            if (convert != null) {
                converter = Optional.ofNullable(AnnotationHelper.getTypeMirror(elements, convert::converter));
            }
            return new AutoValue_FieldDeclaration(pkName, getter.get().getReturnType(), Optional.empty(), converter);
        }

        List<FieldDeclaration> createFields(TypeElement entityElement, List<ExecutableElement> methods) {
            var fieldMap = AnnotationHelper.getAllFields(types, entityElement)
                    .stream()
                    .collect(Collectors.toMap(e -> e.getSimpleName().toString(), Function.identity()));
            var getterMap = new HashMap<String, ExecutableElement>(methods.size());
            var setterMap = new HashMap<String, ExecutableElement>(methods.size());
            for (var method : methods) {
                var methodName = method.getSimpleName().toString();
                if (methodName.startsWith("get")) {
                    String fieldName = getFieldName(methodName);
                    getterMap.put(fieldName, method);
                } else if (methodName.startsWith("set")) {
                    String fieldName = getFieldName(methodName);
                    setterMap.put(fieldName, method);
                }
            }
            return getterMap.entrySet().stream()
                    .filter(entry -> setterMap.containsKey(entry.getKey()))
                    .map(entry -> {
                        var field = fieldMap.get(entry.getKey());
                        Optional<TypeMirror> converter = Optional.empty();
                        Convert convert;
                        if (field != null && (convert = field.getAnnotation(Convert.class)) != null) {
                            converter = Optional.ofNullable(AnnotationHelper.getTypeMirror(elements, convert::converter));
                        }
                        return new AutoValue_FieldDeclaration(entry.getKey(), entry.getValue().getReturnType(), Optional.empty(), converter);
                    })
                    .collect(Collectors.toList());
        }

        private boolean isPropertyMethod(ExecutableElement method, String prefix, String fieldName) {
            String methodName = method.getSimpleName().toString();
            return methodName.length() == prefix.length() + fieldName.length()
                    && methodName.startsWith(prefix)
                    && methodName.charAt(prefix.length()) == Character.toUpperCase(fieldName.charAt(0))
                    && methodName.regionMatches(prefix.length() + 1, fieldName, 1, fieldName.length() - 1);
        }

        private String getFieldName(String propertyMethodName) {
            return Character.toLowerCase(propertyMethodName.charAt(3)) + propertyMethodName.substring(4);
        }
    }


}
