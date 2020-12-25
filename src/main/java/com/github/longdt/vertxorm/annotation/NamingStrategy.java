package com.github.longdt.vertxorm.annotation;

import com.github.longdt.vertxorm.format.Case;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NamingStrategy {
    Case value() default Case.CAMEL_CASE;
}
