package de.derioo.multidb.method;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PredefinedMethodResultCount {

    MANY(-1L),
    FIRST(1L),
    TOP(null),

    ;
    private final Long resultCount;

}
