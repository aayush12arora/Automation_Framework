package com.automation.framework.core.config;

import java.util.Collections;
import java.util.Map;

/**
 * Typed access to the free-form {@code testData} block declared in an
 * application's config. Keeps tests/flows free of casting boilerplate.
 */
public final class TestDataConfig {

    private TestDataConfig() {
    }

    private static Map<String, Object> data() {
        Map<String, Object> data = ConfigurationManager.getInstance()
                .getApplicationConfig().getTestData();
        return data == null ? Collections.emptyMap() : data;
    }

    public static Object get(String key) {
        return data().get(key);
    }

    public static String getString(String key) {
        Object value = get(key);
        return value == null ? null : String.valueOf(value);
    }

    public static String getString(String key, String defaultValue) {
        String value = getString(key);
        return value == null ? defaultValue : value;
    }

    public static int getInt(String key, int defaultValue) {
        Object value = get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return value == null ? defaultValue : Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean has(String key) {
        return data().containsKey(key);
    }
}
