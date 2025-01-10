package de.derioo.multidb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public abstract class Credentials {

    // key -> value (from Keys())
    private final Map<String, String> values = new HashMap<>();

    public Credentials() {

    }

    public Credentials(Map<String, String> values) {
        this.values.putAll(values);
    }

    public Credentials readFromStream(InputStream stream) {
        try {
            Properties properties = new Properties();
            properties.load(stream);
            for (String key : keys()) {
                values.put(key, properties.getProperty(key));
            }
            return this;
        } catch (IOException e) {
            throw new RuntimeException("Error while loading credentials");
        }
    }

    public Credentials readFromFile() {
        return readFromFile(new File(".", defaultFileName()));
    }

    public Credentials readFromFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!file.exists()) {
            throw new RuntimeException("File '" + file + "' not found");
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            return readFromStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read file from path \"" + file + "\": ", e);
        }
    }

    public final Map<String, String> getValues() {
        if (values.isEmpty()) throw new IllegalStateException("credentials was not set, set them via read...");
        return values;
    }

    // Method if anybody want to override it, go ahead
    public String defaultFileName() {
        return "credentials.properties";
    }

    public abstract Set<String> keys();


}
