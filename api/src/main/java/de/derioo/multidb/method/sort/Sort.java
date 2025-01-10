package de.derioo.multidb.method.sort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.Map;

/**
 * This object is used to enable dynamic sorting, without defining the sorting options statically through annotations.
 * See documentation: <a href="https://koboo.gitbook.io/en2do/usage/sorting/sorting-by-parameter">...</a>
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Sort {



    final Map<String, Integer> fieldDirectionMap;
    int limit;
    int skip;

    public Sort() {
        this.fieldDirectionMap = new HashMap<>();
        this.limit = Integer.MAX_VALUE;
        this.skip = 0;
    }

    /**
     * Use this method to define the order / sorting by the given fields
     *
     * @param fieldName The field which is used to sort
     * @param ascending The direction of the sorting
     * @return The used Sort object
     */
    public Sort order(String fieldName, boolean ascending) {
        fieldDirectionMap.put(fieldName, ascending ? 1 : -1);
        return this;
    }

    /**
     * Use this method to define the order / sorting by the given fields,
     * but sets the ascending value to "true"
     *
     * @param fieldName The field which is used to sort
     * @return The used Sort object
     */
    public Sort order(String fieldName) {
        return order(fieldName, true);
    }

    /**
     * Use this method to set the limit of entities of the current sorting.
     *
     * @param limit The amount of the entities in the returned List.
     * @return The used Sort object
     */
    public Sort limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Use this method to set the skipped entities of the current sorting.
     *
     * @param skip The amount of the entities, which should be skipped, before creating the List.
     * @return The used Sort object
     */
    public Sort skip(int skip) {
        this.skip = skip;
        return this;
    }

    public Bson getSortBson() {
        Document document = new Document();

        this.fieldDirectionMap.forEach(document::append);

        return document;
    }
}
