package de.derioo.multidb.sql.utils;

import de.derioo.multidb.annotations.MongoId;
import jakarta.persistence.Id;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class FieldUtils {

    public Field getIdField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) return field;
            if (field.isAnnotationPresent(MongoId.class)) return field;
        }
        return null;
    }

}
