package de.derioo.multidb.sql;


import de.derioo.multidb.AbstractExecutionHandlerFactory;
import de.derioo.multidb.Credentials;
import de.derioo.multidb.DatabaseManager;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;

public class SqlManager extends DatabaseManager {

    private final Credentials credentials;
    private final List<Class<?>> entities;

    private SessionFactory sessionFactory;

    public SqlManager(Credentials credentials, List<Class<?>> entities) {
        super(credentials);
        this.credentials = credentials;
        this.entities = entities;
    }

    public SessionFactory sessionFactory() {
        if (sessionFactory != null) {
            return sessionFactory;
        }

        Configuration configuration = new Configuration();

        switch (credentials.getValues().getOrDefault(SqlCredentials.TYPE, "sqlite").toLowerCase()) {
            case "postgresql", "postgres" -> {
                configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
                configuration.setProperty("hibernate.connection.url", credentials.getValues().get(SqlCredentials.URL));
                configuration.setProperty("hibernate.connection.username", credentials.getValues().get(SqlCredentials.USERNAME));
                configuration.setProperty("hibernate.connection.password", credentials.getValues().get(SqlCredentials.PASSWORD));
            }
            case "mysql" -> {
                configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
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

        configuration.setProperty("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("spring.jpa.hibernate.ddl-auto", "auto");

        configuration.setProperty("hibernate.hikari.maximumPoolSize", "10");
        configuration.setProperty("hibernate.hikari.minimumIdle", "2");
        configuration.setProperty("hibernate.hikari.idleTimeout", "600000");
        configuration.setProperty("hibernate.hikari.maxLifetime", "1800000");
        configuration.setProperty("hibernate.hikari.connectionTimeout", "30000");
        configuration.setProperty("hibernate.c3p0.timeout", "5000");
        configuration.setProperty("hibernate.c3p0.idle_test_period", "3000");

        configuration.setProperty("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");


        for (Class<?> aClass : this.entities) {
            configuration.addAnnotatedClass(aClass);
        }

        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = registryBuilder.build();

        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }

    @Override
    public AbstractExecutionHandlerFactory factory() {
        return new SqlExecutionHandlerFactory(this);
    }
}
