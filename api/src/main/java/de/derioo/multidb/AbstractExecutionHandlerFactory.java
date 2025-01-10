package de.derioo.multidb;

import de.derioo.multidb.method.IndexedMethod;
import de.derioo.multidb.repository.Repository;
import de.derioo.multidb.repository.data.RepositoryData;
import org.bson.conversions.Bson;

public abstract class AbstractExecutionHandlerFactory {

    public abstract <E, ID> AbstractExecutionHandler newHandler(RepositoryData<E, ID> data);

}
