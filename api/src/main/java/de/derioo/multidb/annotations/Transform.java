package de.derioo.multidb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to "rename" a method name of the repository.
 * If you use this annotation, keep in mind, that the validation uses the
 * value of the annotation instead of the method name itself.
 * See documentation: <a href="https://koboo.gitbook.io/en2do/usage/transform">...</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Transform {

    /**
     * @return The "real" method declaration according to the en2do specification.
     */
    String value();
}
