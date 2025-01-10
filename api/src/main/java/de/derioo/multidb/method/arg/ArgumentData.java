package de.derioo.multidb.method.arg;

import de.derioo.multidb.annotations.MongoId;
import de.derioo.multidb.annotations.Transform;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

public record ArgumentData(Field field, boolean not, ArgumentOption option) {


    public String finalFieldName() {
        if (field.isAnnotationPresent(MongoId.class)) return "_id";
        if (field.isAnnotationPresent(Transform.class)) {
            return field.getAnnotation(Transform.class).value();
        }
        return field.getName();
    }


    @RequiredArgsConstructor
    @Getter
    public enum ArgumentOption {

        EQ(""),
        CONTAINS("contains"),
        GREATER_THAN("greaterthan"),
        LESS_THAN("lessthan"),
        GREATER_EQ("greatereq"),
        LESS_EQ("lesseq"),
        REGEX("regex")
        ;

        private final String keyword;

    }

}
