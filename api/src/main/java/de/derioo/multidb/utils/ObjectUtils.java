package de.derioo.multidb.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ObjectUtils {

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T findLastOfType(Class<T> clazz, Object... args) {
        if (args == null) return null;
        for (int i = args.length - 1; i >= 0; i--) {
            if (clazz.isAssignableFrom(args[i].getClass())) return (T) args[i];
            if (clazz.equals(args[i].getClass())) return (T) args[i];
        }
        return null;
    }
    
}

