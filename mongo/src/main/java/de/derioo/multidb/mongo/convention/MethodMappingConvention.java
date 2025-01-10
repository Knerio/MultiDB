package de.derioo.multidb.mongo.convention;

import de.derioo.multidb.mongo.MongoManager;
import de.derioo.multidb.mongo.utils.FieldUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyModelBuilder;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This convention implementation disables the saving of methods
 * which start with "get*" or "set*". MongoDB Pojo codec thinks that these methods
 * are property read or write methods, but most of the time you don't want to save them.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MethodMappingConvention implements Convention {

    MongoManager mongoManager;
    Map<Class<?>, Set<Field>> reflectedFieldIndex = new ConcurrentHashMap<>();

    /**
     * @param classModelBuilder the ClassModelBuilder to apply the convention to
     * @see Convention
     */
    @Override
    public void apply(ClassModelBuilder<?> classModelBuilder) {
        Class<?> entityClass = classModelBuilder.getType();
        Set<Field> fieldSet = parseEntityFields(entityClass);
        Set<String> nonFieldProperties = new LinkedHashSet<>();
        for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
            String propertyName = propertyModelBuilder.getName();
            Field field = FieldUtils.findFieldByName(propertyName, fieldSet);
            if (field != null) {
                continue;
            }
            nonFieldProperties.add(propertyName);
        }
        for (String nonFieldProperty : nonFieldProperties) {
            classModelBuilder.removeProperty(nonFieldProperty);
        }
    }

    public Set<Field> parseEntityFields(Class<?> entityClass) {
        Set<Field> entityFieldSet = reflectedFieldIndex.get(entityClass);
        if (entityFieldSet != null) {
            return entityFieldSet;
        }
        entityFieldSet = FieldUtils.collectFields(entityClass);
        reflectedFieldIndex.put(entityClass, entityFieldSet);
        return entityFieldSet;
    }
}
