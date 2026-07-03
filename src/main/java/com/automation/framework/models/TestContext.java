package com.automation.framework.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread-local bag for sharing state between steps of a single test (e.g. a value
 * captured on one page and asserted later, or an id returned by an API call).
 * Generic and application-agnostic.
 */
public final class TestContext {

    private static final ThreadLocal<Map<String, Object>> STORE =
            ThreadLocal.withInitial(HashMap::new);

    private TestContext() {
    }

    public static void put(String key, Object value) {
        STORE.get().put(key, value);
    }

    public static Object get(String key) {
        return STORE.get().get(key);
    }

    public static String getString(String key) {
        Object value = get(key);
        return value == null ? null : String.valueOf(value);
    }

    public static boolean has(String key) {
        return STORE.get().containsKey(key);
    }

    /** Clear all context for the current thread - call in an @AfterMethod. */
    public static void clear() {
        STORE.get().clear();
        STORE.remove();
    }
}
