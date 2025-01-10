package de.derioo.multidb.sql;


import de.derioo.multidb.AbstractExecutionHandlerFactory;
import de.derioo.multidb.Credentials;
import de.derioo.multidb.DatabaseManager;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlManager extends DatabaseManager {

    private final Credentials credentials;

    public SqlManager(Credentials credentials) {
        super(credentials);
        this.credentials = credentials;
    }

    public SessionFactory factory(Class<?> entityClass) {
        Configuration configuration = new Configuration();

        switch (credentials.getValues().getOrDefault(SqlCredentials.TYPE, "sqlite").toLowerCase()) {
            case "mysql" -> {
                configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
                configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
                configuration.setProperty("hibernate.connection.url", credentials.getValues().get(SqlCredentials.URL));
                configuration.setProperty("hibernate.connection.username", credentials.getValues().get(SqlCredentials.USERNAME));
                configuration.setProperty("hibernate.connection.password", credentials.getValues().get(SqlCredentials.PASSWORD));
            }
            case "sqlite" -> {
                configuration.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
                configuration.setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
                configuration.setProperty("hibernate.connection.url", credentials.getValues().get(SqlCredentials.URL));
                configuration.setProperty("hibernate.connection.username", "");
                configuration.setProperty("hibernate.connection.password", "");
            }
        }

        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("spring.jpa.hibernate.ddl-auto", "auto");

        configuration.addAnnotatedClass(entityClass);

        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = registryBuilder.build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Override
    public AbstractExecutionHandlerFactory factory() {
        return new SqlExecutionHandlerFactory(this);
    }
}
