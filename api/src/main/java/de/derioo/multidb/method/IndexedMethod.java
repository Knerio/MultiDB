package de.derioo.multidb.method;

import de.derioo.multidb.annotations.Limit;
import de.derioo.multidb.annotations.Skip;
import de.derioo.multidb.method.arg.ArgumentData;
import de.derioo.multidb.method.sort.Sort;
import de.derioo.multidb.method.sort.SortBy;
import de.derioo.multidb.utils.ObjectUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.BsonRegularExpression;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class IndexedMethod {

    @Getter
    private final Method method;

    @Getter
    private final Long definedResultCount;

    private final MethodDelimiter chain;
    private final List<ArgumentData> arguments;
    private final Set<MethodPrefix> prefixes;


    public Document buildBson(Object[] args) {
        Document bson = new Document();

        // Filter
        List<Bson> filters = new ArrayList<>();

        // "query" filters (e.g. findFirstByKEY)
        for (int i = 0; i < arguments.size(); i++) {
            ArgumentData data = arguments.get(i);
            Document filter = null;
            switch (data.option()) {
                case EQ -> {
                    filter = new Document(data.finalFieldName(), new Document("$eq", args[i]));
                }
                case GREATER_THAN -> {
                    filter = new Document(data.finalFieldName(), new Document("$gt", args[i]));
                }
                case GREATER_EQ -> {
                    filter = new Document(data.finalFieldName(), new Document("$gte", args[i]));
                }
                case LESS_THAN -> {
                    filter = new Document(data.finalFieldName(), new Document("$lt", args[i]));
                }
                case LESS_EQ -> {
                    filter = new Document(data.finalFieldName(), new Document("$lte", args[i]));
                }
                case CONTAINS -> {
                    filter = new Document(data.finalFieldName(), new BsonRegularExpression(".*[" + args[i] + "].*"));
                }
                case REGEX -> {
                    Object arg = args[i];
                    if (arg instanceof Pattern pattern) {
                        filter = new Document(data.finalFieldName(), new BsonRegularExpression(pattern.pattern()));
                    } else {
                        filter = new Document(data.finalFieldName(), new BsonRegularExpression(arg.toString()));
                    }
                }
            }
            if (filter != null) {
                if (data.not()) {
                    filters.add(new Document(data.finalFieldName(), new Document("$not", filter.get(data.finalFieldName()))));
                } else {
                    filters.add(filter);
                }
            }
        }
        Bson filter;
        if (filters.isEmpty()) {
            filter = new Document();
        } else {
            filter = new Document(this.chain.getFilterKey(), filters);
        }
        // Add prefix filters, currently only custom filters, appended with and
        for (MethodPrefix prefix : prefixes) {
            switch (prefix) {
                case FILTER -> {
                    filter = new Document("$and", List.of(filter, ObjectUtils.findLastOfType(Bson.class, args)));
                }
            }
        }
        bson.append("filter", filter);
        // End Filter

        // Sort
        Sort sort = ObjectUtils.findLastOfType(Sort.class, args);
        if (sort == null) sort = new Sort();


        for (SortBy sortBy : method.getAnnotationsByType(SortBy.class)) {
            sort.order(sortBy.field(), sortBy.ascending());
        }

        // Limit
        if (method.isAnnotationPresent(Limit.class)) {
            sort.skip(method.getAnnotation(Limit.class).value());
        }
        // End limit

        // Skip
        if (method.isAnnotationPresent(Skip.class)) {
            sort.skip(method.getAnnotation(Skip.class).value());
        }
        // End Skip


        bson.append("sort", sort.getSortBson());
        bson.append("limit", sort.getLimit());
        bson.append("skip", sort.getSkip());
        // End sort

        return bson;
    }

    public boolean list() {
        return this.definedResultCount != 1;
    }

    public boolean isExists() {
        return prefixes.contains(MethodPrefix.EXISTS);
    }

    public boolean isDelete() {
        return prefixes.contains(MethodPrefix.DELETE);
    }

    public boolean isCount() {
        return prefixes.contains(MethodPrefix.COUNT);
    }

    public boolean isSave() {
        return prefixes.contains(MethodPrefix.SAVE);
    }


    public final @NotNull String getFinalMethodName() {
        return this.method.getName();
    }


    public boolean isAsync() {
        return prefixes.contains(MethodPrefix.ASYNC);
    }


}
