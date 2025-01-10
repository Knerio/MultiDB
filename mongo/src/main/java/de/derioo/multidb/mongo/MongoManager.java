package de.derioo.multidb.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.derioo.multidb.AbstractExecutionHandlerFactory;
import de.derioo.multidb.Credentials;
import de.derioo.multidb.DatabaseManager;
import de.derioo.multidb.mongo.convention.AnnotationConvention;
import de.derioo.multidb.mongo.convention.MethodMappingConvention;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoManager extends DatabaseManager {

    @Getter
    private final MongoDatabase database;
    @Getter
    private final MongoClient client;

    public MongoManager(Credentials credentials) {
        super(credentials);

        ConnectionString connection = new ConnectionString(credentials.getValues().get(MongoCredentials.CONNECT_KEY));


       CodecRegistry codecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder()
                        .automatic(true)
                        .conventions(List.of(
                                new AnnotationConvention(),
                                new MethodMappingConvention(this),
                                Conventions.ANNOTATION_CONVENTION,
                                Conventions.SET_PRIVATE_FIELDS_CONVENTION
                        ))
                        .build())
        );

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applicationName("multidb-client")
                .applyConnectionString(connection)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(codecRegistry)
                .build();

        client = MongoClients.create(clientSettings);
        database = client.getDatabase(credentials.getValues().get(MongoCredentials.DATABASE_KEY));
    }

    @Override
    public AbstractExecutionHandlerFactory factory() {
        return new MongoExecutionHandlerFactory(this);
    }
}
