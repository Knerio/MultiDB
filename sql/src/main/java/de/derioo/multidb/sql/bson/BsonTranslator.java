package de.derioo.multidb.sql.bson;

import de.derioo.multidb.sql.utils.FieldUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bson.*;
import org.bson.conversions.Bson;
import org.hibernate.Session;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class BsonTranslator {


    public void delete(Session session, String tableName, Bson filter, Class<?> entityClass) {
        StringBuilder query = new StringBuilder("DELETE FROM " + tableName + " ");
        Map<String, Object> parameterMap = createQueryByFilter(filter, query, entityClass);
        MutationQuery createdQuery = session.createNativeMutationQuery(query.toString());
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            createdQuery.setParameter(entry.getKey(), entry.getValue());
        }
        createdQuery.executeUpdate();
    }

    public long count(Session session, String tableName, Bson filter, Class<?> entityClass) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM " + tableName + " ");
        Map<String, Object> parameterMap = createQueryByFilter(filter, query, entityClass);
        Query<Long> createdQuery = session.createNativeQuery(query.toString(), Long.class);
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            createdQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return createdQuery.uniqueResult();
    }

    private Map<String, Object> createQueryByFilter(Bson filter, StringBuilder query, Class<?> entityClass) {
        Map<String, Object> parameters = new HashMap<>();
        if (!filter.toBsonDocument().isEmpty()) {
            String chainType = filter.toBsonDocument().getFirstKey().equals("$and") ? "AND" : "OR";
            if (chainType.equalsIgnoreCase("and")) {
                query.append("WHERE 1=1 ");
            } else {
                query.append("WHERE 1!=1 ");
            }

            for (BsonValue condition : filter.toBsonDocument().getArray(filter.toBsonDocument().getFirstKey())) {
                BsonDocument conditionDocument = condition.asDocument();
                String field = conditionDocument.getFirstKey();
                BsonValue comparatorField = conditionDocument.get(field);
                boolean not = false;
                if (comparatorField.isDocument() && comparatorField.asDocument().getFirstKey().equals("$not")) {
                    not = true;
                    comparatorField = comparatorField.asDocument().get("$not");
                }
                if (comparatorField.isRegularExpression()) throw new IllegalStateException("Cannot use regex in sql");
                BsonComparator comparator = BsonComparator.getByBson(comparatorField.asDocument().getFirstKey());
                if (field.equals("_id")) {
                    field = FieldUtils.getIdField(entityClass).getName();
                }
                query.append(chainType).append(not ? " NOT " : " ").append("(")
                        .append(field).append(comparator.sqlKey).append(":").append(field).append(")");
                Object value = translateToSqlValue(comparatorField.asDocument().get(comparatorField.asDocument().getFirstKey()));
                parameters.put(field, value);
            }
        }
        return parameters;
    }

    public <E> List<E> find(Session session, String tableName, Bson filter, Bson sort, int limit, int skip, Class<E> entityClass) {
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " ");
        Map<String, Object> parameterMap = createQueryByFilter(filter, query, entityClass);

        if (!sort.toBsonDocument().isEmpty()) query.append("ORDER BY ");
        List<Map.Entry<String, BsonValue>> entries = sort.toBsonDocument().entrySet().stream().toList();
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, BsonValue> entry = entries.get(i);
            String key = entry.getKey();
            if (key.equals("_id")) {
                key = FieldUtils.getIdField(entityClass).getName();
            }
            query.append(key).append(" ").append(entry.getValue().asNumber().intValue() == 1 ? "ASC" : "DESC");
            if (i != entries.size() - 1) query.append(",");
            query.append(" ");
        }

        Query<E> createdQuery = session.createNativeQuery(query.toString(), entityClass);
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            createdQuery.setParameter(entry.getKey(), entry.getValue());
        }

        createdQuery.setMaxResults(limit).setFirstResult(skip);
        return createdQuery.getResultList();
    }


    @RequiredArgsConstructor
    @Getter
    public enum BsonComparator {

        EQ("$eq", "="),
        GREATER_THAN("$gt", ">"),
        LESS_THAN("$lt", "<"),
        GREATER_EQ("$gte", ">="),
        LESS_EQ("$lte", "<="),

        ;

        private final String bsonKey;
        private final String sqlKey;

        public static BsonComparator getByBson(String s) {
            for (BsonComparator value : values()) {
                if (value.bsonKey.equals(s)) return value;
            }
            return EQ;
        }

    }

    public Object translateToSqlValue(BsonValue bsonValue) {
        if (bsonValue == null || bsonValue.isNull()) {
            return null;
        }

        if (bsonValue.isString()) {
            return bsonValue.asString().getValue();
        }

        if (bsonValue.isInt32()) {
            return bsonValue.asInt32().getValue();
        }

        if (bsonValue.isInt64()) {
            return bsonValue.asInt64().getValue();
        }

        if (bsonValue.isDouble()) {
            return bsonValue.asDouble().getValue();
        }

        if (bsonValue.isDecimal128()) {
            return bsonValue.asDecimal128().getValue();
        }

        if (bsonValue.isBoolean()) {
            return bsonValue.asBoolean().getValue();
        }

        if (bsonValue.isDateTime()) {
            return new Timestamp(bsonValue.asDateTime().getValue());
        }

        if (bsonValue.isObjectId()) {
            return bsonValue.asObjectId().getValue();
        }

        if (bsonValue.isBinary()) {
            return bsonValue.asBinary().getData();
        }

        if (bsonValue.isArray()) {
            return arrayToSqlValue(bsonValue.asArray());
        }

        if (bsonValue.isDocument()) {
            return bsonValue.asDocument().toJson();
        }

        if (bsonValue.isRegularExpression()) {
            return "'" + bsonValue.asRegularExpression().getPattern() + "'";
        }

        if (bsonValue.isSymbol()) {
            return bsonValue.asSymbol().getSymbol();
        }

        if (bsonValue.isTimestamp()) {
            BsonTimestamp timestamp = bsonValue.asTimestamp();
            return timestamp.getValue();
        }

        return null;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String arrayToSqlValue(BsonArray array) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < array.size(); i++) {
            sb.append(translateToSqlValue(array.get(i)));
            if (i < array.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
