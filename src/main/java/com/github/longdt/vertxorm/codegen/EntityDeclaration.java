package com.github.longdt.vertxorm.codegen;

import com.github.longdt.vertxorm.annotation.NamingStrategy;
import com.github.longdt.vertxorm.format.Case;
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
import javax.persistence.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.tools.Diagnostic.Kind.ERROR;

@AutoValue
abstract class EntityDeclaration {
    abstract TypeElement targetType();

    abstract String tableName();

    abstract FieldDeclaration idField();

    abstract boolean autoId();

    abstract Map<String, FieldDeclaration> fieldsMap();

    abstract Case namingStrategy();

    Stream<FieldDeclaration> fieldStream() {
        return Stream.concat(Stream.of(idField()), fieldsMap().values().stream());
    }

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
            var idField = createIdField(idOpt.get(), methods);
            var autoId = idOpt.get().getAnnotation(GeneratedValue.class) != null;
            TreeMap<String, FieldDeclaration> fields = createFields((TypeElement) entityElement, methods)
                    .stream()
                    .filter(field -> !field.fieldName().equals(idField.fieldName()))
                    .collect(TreeMap::new, (m, e) -> m.put(e.fieldName(), e), Map::putAll);
            var namingStrategy = entityElement.getAnnotation(NamingStrategy.class);
            var namingStrategyValue = namingStrategy != null ? namingStrategy.value() : Case.CAMEL_CASE;
            return Optional.of(new AutoValue_EntityDeclaration((TypeElement) entityElement, tableName, idField, autoId, fields, namingStrategyValue));
        }

        Optional<VariableElement> findIdField(Element entityElement) {
            return ElementFilter.fieldsIn(entityElement.getEnclosedElements())
                    .stream()
                    .filter(f -> f.getAnnotation(Id.class) != null)
                    .findAny();
        }

        FieldDeclaration createIdField(VariableElement idField, List<ExecutableElement> methods) {
            var idName = idField.getSimpleName().toString();
            var getter = methods.stream()
                    .filter(m -> isPropertyMethod(m, "get", idName))
                    .findAny();
            if (getter.isEmpty()) {
                messager.printMessage(ERROR,
                        String.format("%s has no valid getter. "
                                        + "id must have getter method.",
                                idName));
            }
            var setter = methods.stream()
                    .filter(m -> isPropertyMethod(m, "set", idName))
                    .findAny();
            return createFieldDeclaration(idField, idName);
        }

        private FieldDeclaration createFieldDeclaration(VariableElement field, String fieldName) {
            Convert convert = field.getAnnotation(Convert.class);
            TypeMirror converter = null;
            TypeMirror sqlType = null;
            if (convert != null) {
                var converterElement = AnnotationHelper.getTypeElement(elements, types, convert::converter);
                converter = converterElement.asType();
                var declaredType = (DeclaredType) converterElement.getInterfaces().get(0);
                sqlType = ((DeclaredType) declaredType.getTypeArguments().get(1)).asElement().asType();
            }

            Column column = field.getAnnotation(Column.class);
            String columnName = column != null ? column.name() : null;
            return new AutoValue_FieldDeclaration(fieldName, field.asType(), Optional.ofNullable(sqlType), Optional.ofNullable(converter), Optional.ofNullable(columnName));
        }

        List<FieldDeclaration> createFields(TypeElement entityElement, List<ExecutableElement> methods) {
            var fieldMap = AnnotationHelper.getAllFields(types, entityElement)
                    .stream()
                    .filter(ve -> ve.getAnnotation(Transient.class) == null)
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
            return fieldMap.entrySet().stream()
                    .filter(fieldEntry -> getterMap.containsKey(fieldEntry.getKey()) && setterMap.containsKey(fieldEntry.getKey()))
                    .map(fieldEntry -> createFieldDeclaration(fieldEntry.getValue(), fieldEntry.getKey()))
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
