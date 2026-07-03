package com.automation.framework.utilities;

import com.automation.framework.exceptions.FrameworkException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Small Jackson wrapper for reading JSON and YAML from the classpath or disk
 * into POJOs. Used for test data and flow definitions.
 */
public final class JsonUtils {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

    private JsonUtils() {
    }

    private static ObjectMapper mapperFor(String resourceOrPath) {
        String lower = resourceOrPath.toLowerCase();
        return (lower.endsWith(".yml") || lower.endsWith(".yaml")) ? YAML : JSON;
    }

    /** Read a classpath resource (JSON or YAML) into the given type. */
    public static <T> T fromClasspath(String resource, Class<T> type) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new FrameworkException("Resource not found on classpath: " + resource);
            }
            return mapperFor(resource).readValue(in, type);
        } catch (FrameworkException fe) {
            throw fe;
        } catch (Exception e) {
            throw new FrameworkException("Failed to read '" + resource + "' as " + type.getSimpleName(), e);
        }
    }

    /** Read a file (JSON or YAML) into the given type. */
    public static <T> T fromFile(Path path, Class<T> type) {
        try {
            return mapperFor(path.toString()).readValue(Files.newInputStream(path), type);
        } catch (Exception e) {
            throw new FrameworkException("Failed to read '" + path + "' as " + type.getSimpleName(), e);
        }
    }

    public static String toJson(Object value) {
        try {
            return JSON.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (Exception e) {
            throw new FrameworkException("Failed to serialise object to JSON", e);
        }
    }
}
