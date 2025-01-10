package de.derioo.multidb.method;



import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public enum MethodDelimiter {

    AND("$and"),
    OR("$or"),

    ;

    private final String filterKey;

}
