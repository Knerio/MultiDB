package de.derioo.multidb.repository.execution;

import de.derioo.multidb.AbstractExecutionHandler;
import de.derioo.multidb.DatabaseManager;
import de.derioo.multidb.method.IndexedMethod;
import de.derioo.multidb.repository.Repository;
import de.derioo.multidb.repository.data.RepositoryData;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ExecutionHandler<REPOSITORY extends Repository<ENTITY, ID>, ENTITY, ID> implements InvocationHandler {

    private final RepositoryData<ENTITY, ID> data;
    private final DatabaseManager manager;
    private final AbstractExecutionHandler executionHandler;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Optional<IndexedMethod> first = data.getMethods().stream()
                .filter(indexedMethod -> indexedMethod.getMethod().equals(method)).findFirst();
        if (first.isEmpty()) throw new NoSuchMethodException("Method " + method.getName() + " not found");

        Document bson = first.get().buildBson(args);

        if (first.get().isAsync()) {
            return CompletableFuture.supplyAsync(() -> executionHandler.handleBasicExecution(first.get(), bson, args));
        }
        return executionHandler.handleBasicExecution(first.get(), bson, args);
    }
}
