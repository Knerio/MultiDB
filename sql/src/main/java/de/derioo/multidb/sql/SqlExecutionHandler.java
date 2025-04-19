package de.derioo.multidb.sql;

import de.derioo.multidb.AbstractExecutionHandler;
import de.derioo.multidb.method.IndexedMethod;
import de.derioo.multidb.repository.data.RepositoryData;
import de.derioo.multidb.sql.bson.BsonTranslator;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.criteria.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SqlExecutionHandler<E, ID> extends AbstractExecutionHandler {

    private final RepositoryData<E, ID> data;
    private final SqlManager manager;
    private final SessionFactory factory;

    public SqlExecutionHandler(RepositoryData<E, ID> data, SqlManager manager) {
        this.data = data;
        this.manager = manager;
        this.factory = manager.factory(data.getEntityClass());
    }

    public Object handleBasicExecution(IndexedMethod method, Document args, Object... methodArgs) {
        String tableName = data.getEntityClass().getSimpleName();
        final Bson filter = args.toBsonDocument().getDocument("filter");
        final Bson sort = args.toBsonDocument().getDocument("sort");
        int limit = args.get("limit", Integer.class) != null ? args.get("limit", Integer.class) : Integer.MAX_VALUE;
        int offset = args.get("skip", Integer.class) != null ? args.get("skip", Integer.class) : 0;
        try (Session session = factory.openSession()    ) {
            Transaction transaction = session.beginTransaction();
            if (method.isDelete()) {
                BsonTranslator.delete(session, tableName, filter, data.getEntityClass());
                transaction.commit();
                return true;
            }
            if (method.isCount()) {
                long amount = BsonTranslator.count(session, tableName, filter, data.getEntityClass());
                transaction.commit();
                return amount;
            }
            if (method.isExists()) {
                long amount = BsonTranslator.count(session, tableName, filter, data.getEntityClass());
                transaction.commit();
                return amount > 0;
            }
            if (method.isSave()) {
                for (Object entity : ((Object[]) methodArgs[0])) {
                    session.merge(entity);
                }
                transaction.commit();
                return true;
            }
            List<?> result = BsonTranslator.find(session, tableName, filter, sort, limit, offset, data.getEntityClass());
            transaction.commit();
            if (method.list()) return result;
            return result.isEmpty() ? null : result.getFirst();
        }
    }

    private String convertBsonToSqlFilter(Document bsonFilter) {
        StringBuilder filterBuilder = new StringBuilder("1=1");
        bsonFilter.forEach((key, value) -> {
            if (!filterBuilder.isEmpty()) filterBuilder.append(" AND ");
            filterBuilder.append(key).append(" = '").append(value).append("'");
        });
        return filterBuilder.toString();
    }

    private String convertBsonToSqlSort(Document bsonSort) {
        StringBuilder sortBuilder = new StringBuilder();
        bsonSort.forEach((key, value) -> {
            if (!sortBuilder.isEmpty()) sortBuilder.append(", ");
            sortBuilder.append(key).append(value.equals(1) ? " ASC" : " DESC");
        });
        return sortBuilder.toString();
    }

    private String generateInsertQuery(String tableName, List<Field> fields) {
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        query.append(fields.stream().map(Field::getName).collect(Collectors.joining(", ")));
        query.append(") VALUES (");
        query.append("?, ".repeat(fields.size()));
        query.setLength(query.length() - 2);
        query.append(")");
        return query.toString();
    }

    private E mapRow(ResultSet resultSet) throws SQLException {
        try {
            E entity = data.getEntityClass().getDeclaredConstructor().newInstance();
            for (Field field : data.getEntityClass().getFields()) {
                Object value = field.get(entity);
                field.setAccessible(true);
                field.set(entity, value);
            }
            return entity;
        } catch (ReflectiveOperationException e) {
            throw new SQLException("Error mapping row to entity", e);
        }
    }

    private Object getFieldValue(E entity, Field field) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to access field: " + field.getName(), e);
        }
    }
}
