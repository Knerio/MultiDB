package de.derioo.multidb.method.sort;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(value = SortByArray.class)
public @interface SortBy {

    /**
     * @return The required field, which should be sorted.
     */
    String field();

    /**
     * @return The optional direction, which defaults to ascending = "true".
     */
    boolean ascending() default false;
}
