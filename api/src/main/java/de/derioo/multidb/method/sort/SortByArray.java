package de.derioo.multidb.method.sort;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SortByArray {

    /**
     * @return The array of the @SortBy annotations.
     */
    SortBy[] value();
}
