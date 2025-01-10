package de.derioo.multidb.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.derioo.multidb.AbstractExecutionHandler;
import de.derioo.multidb.method.IndexedMethod;
import de.derioo.multidb.repository.data.RepositoryData;
import lombok.RequiredArgsConstructor;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoExecutionHandler<E, ID> extends AbstractExecutionHandler {

    private final RepositoryData<E, ID> data;
    private final MongoManager manager;
    private final MongoCollection<E> collection;

    public MongoExecutionHandler(RepositoryData<E, ID> data, MongoManager manager) {
        this.data = data;
        this.manager = manager;

        this.collection = this.manager.getDatabase().getCollection(data.getEntityClass().getSimpleName(), data.getEntityClass());
    }


    @Override
    @SuppressWarnings("unchecked")
    public Object handleBasicExecution(IndexedMethod method, Document args, Object... methodArgs) {
        final Bson filter = args.toBsonDocument().getDocument("filter");
        final Bson sort = args.toBsonDocument().getDocument("sort");
        final int limit = args.toBsonDocument().getNumber("limit", new BsonInt64(Integer.MAX_VALUE)).intValue();
        final int skip = args.toBsonDocument().getNumber("skip", new BsonInt64(0)).intValue();

        if (method.isDelete()) {
            return this.collection.deleteMany(filter).wasAcknowledged();
        }
        if (method.isExists()) {
            return this.collection.countDocuments(filter) > 0;
        }
        if (method.isCount()) {
            return this.collection.countDocuments(filter);
        }
        if (method.isSave()) {
            return this.collection.insertMany(Arrays.asList((E[]) methodArgs[0])).wasAcknowledged();
        }
        final FindIterable<E> iterable = this.collection.find(filter)
                .sort(sort)
                .limit(limit)
                .skip(skip);
        if (method.list()) return iterable.into(new ArrayList<>());
        return iterable.first();
    }
}
