package de.derioo.multidb.mongo;

import com.mongodb.client.MongoDatabase;
import de.derioo.multidb.AbstractExecutionHandler;
import de.derioo.multidb.AbstractExecutionHandlerFactory;
import de.derioo.multidb.Credentials;
import de.derioo.multidb.method.IndexedMethod;
import de.derioo.multidb.repository.data.RepositoryData;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class MongoExecutionHandlerFactory extends AbstractExecutionHandlerFactory {

    private final MongoManager manager;


    @Override
    public <E, ID> AbstractExecutionHandler newHandler(RepositoryData<E, ID> data) {
        return new MongoExecutionHandler<>(data, manager);
    }
}
