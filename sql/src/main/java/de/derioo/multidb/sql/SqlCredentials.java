package de.derioo.multidb.sql;

import de.derioo.multidb.Credentials;

import java.util.Map;
import java.util.Set;

public class SqlCredentials extends Credentials {

    public static final String URL = "jdbcurl";
    public static final String TYPE = "type";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public SqlCredentials() {
    }

    public SqlCredentials(Map<String, String> values) {
        super(values);
    }

    public SqlCredentials(String connectionUrl, String username, String password, String type) {
        super(Map.of(URL, connectionUrl, USERNAME, username, PASSWORD, password, TYPE, type));
    }


    @Override
    public Set<String> keys() {
        return Set.of(URL, USERNAME, PASSWORD, TYPE);
    }
}
