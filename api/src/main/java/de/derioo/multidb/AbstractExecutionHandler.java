package de.derioo.multidb;

import de.derioo.multidb.method.IndexedMethod;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public abstract class AbstractExecutionHandler {

    @Nullable
    public abstract Object handleBasicExecution(IndexedMethod method, Document args, Object... methodArgs);

}
