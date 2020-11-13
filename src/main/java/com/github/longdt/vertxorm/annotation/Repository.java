package com.github.longdt.vertxorm.annotation;

import com.github.longdt.vertxorm.repository.CrudRepository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Repository {
    /**
     * The <i>simple</i> name of the generated repository; the repository is always generated in the same
     * package as the annotated type.  The default value (the empty string) will result in a factory
     * with the name of the type being created with {@code Impl} appended to the end. For example,
     * the default name for a factory for {@code MyRepository} will be {@code MyRepositoryImpl}.
     */
    String className() default "";

    /**
     * The type that the generated repository is require to extend.
     */
    Class<? extends CrudRepository> extending() default CrudRepository.class;

    Driver driver() default Driver.POSTGRESQL;

    NamingStrategy namingStrategy() default NamingStrategy.SNAKE_CASE;
}
