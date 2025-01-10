package de.derioo.multidb.sql;

import de.derioo.multidb.AbstractExecutionHandler;
import de.derioo.multidb.AbstractExecutionHandlerFactory;
import de.derioo.multidb.repository.data.RepositoryData;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class SqlExecutionHandlerFactory extends AbstractExecutionHandlerFactory {

    private final SqlManager manager;


    @Override
    public <E, ID> AbstractExecutionHandler newHandler(RepositoryData<E, ID> data) {
        return new SqlExecutionHandler<>(data, manager);
    }
}
