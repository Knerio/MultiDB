package de.derioo.multidb.method;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MethodPrefix  {

    ASYNC("async", true),
    SAVE("save", false),
    FIND("find", false),
    FILTER("filter", false),
    DELETE("delete", false),
    EXISTS("exists", false),
    COUNT("count", false),

    ;

    private final String prefix;

    private final boolean compatible;


}