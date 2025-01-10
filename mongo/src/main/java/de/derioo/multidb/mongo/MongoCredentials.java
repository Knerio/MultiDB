package de.derioo.multidb.mongo;

import com.mongodb.client.model.Filters;
import de.derioo.multidb.Credentials;

import java.util.Map;
import java.util.Set;

public class MongoCredentials extends Credentials {

    public static final String DATABASE_KEY = "database";
    public static final String CONNECT_KEY = "connectionString";

    public MongoCredentials() {
    }

    public MongoCredentials(Map<String, String> values) {
        super(values);
    }

    public MongoCredentials(String connectionString, String database) {
        super(Map.of(CONNECT_KEY, connectionString, DATABASE_KEY, database));
    }



    @Override
    public Set<String> keys() {
        return Set.of(DATABASE_KEY, CONNECT_KEY);
    }
}
