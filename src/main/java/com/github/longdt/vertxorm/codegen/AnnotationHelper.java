package com.github.longdt.vertxorm.codegen;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.function.Supplier;

public class AnnotationHelper {
    public static List<VariableElement> getAllFields(Types types, TypeElement element) {
        var result = ElementFilter.fieldsIn(element.getEnclosedElements());
        TypeMirror superClass;
        while ((superClass = element.getSuperclass()).getKind() != TypeKind.NONE) {
            element = (TypeElement) types.asElement(superClass);
            result.addAll(ElementFilter.fieldsIn(element.getEnclosedElements()));
        }
        return result;
    }

    public static TypeMirror getTypeMirror(Elements elements, Supplier<Class> classSupplier) {
        try {
            Class clazz = classSupplier.get();
            return elements.getTypeElement(clazz.getCanonicalName()).asType();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
    }
}
