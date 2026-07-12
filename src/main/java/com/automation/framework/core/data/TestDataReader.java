package com.automation.framework.core.data;

import com.automation.framework.exceptions.FrameworkException;
import com.automation.framework.utilities.JsonUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Loads a test's data from {@code TestData/<module>/<testName>.json} on the
 * classpath and binds it to a model such as
 * {@link com.automation.framework.models.CustomerData}.
 *
 * <p>The module comes from {@link TestModule} on the test class (falling back to
 * the last segment of its package). The file name is the running test method's
 * name; if no such file exists the test class's simple name is tried, which lets
 * several test methods share one data file.
 *
 * <pre>
 *   TestData/
 *     login/
 *       loginWithValidCredentials.json
 *       invalidLoginIsRejected.json
 * </pre>
 */
public final class TestDataReader {

    /** Classpath root under which all data files live. */
    public static final String ROOT = "TestData";

    private TestDataReader() {
    }

    /**
     * Read the data file belonging to {@code testName} on {@code testClass}.
     *
     * @throws FrameworkException when no candidate file exists, listing what was
     *                            tried and what the module folder actually holds
     */
    public static <T> T read(Class<?> testClass, String testName, Class<T> type) {
        return read(moduleOf(testClass), testName, testClass.getSimpleName(), type);
    }

    /** Read {@code TestData/<module>/<fileName>.json} with no fallback. */
    public static <T> T read(String module, String fileName, Class<T> type) {
        return read(module, fileName, null, type);
    }

    /**
     * As {@link #read(Class, String, Class)}, but returns {@code null} when the
     * test has no data file. Lets the base class load data for every test
     * without forcing a data file on tests that need none.
     */
    public static <T> T readIfPresent(Class<?> testClass, String testName, Class<T> type) {
        String module = moduleOf(testClass);
        for (String candidate : candidates(testName, testClass.getSimpleName())) {
            String resource = ROOT + "/" + module + "/" + candidate + ".json";
            if (exists(resource)) {
                return JsonUtils.fromClasspath(resource, type);
            }
        }
        return null;
    }

    private static <T> T read(String module, String fileName, String fallbackName, Class<T> type) {
        List<String> attempted = new ArrayList<>();
        for (String candidate : candidates(fileName, fallbackName)) {
            String resource = ROOT + "/" + module + "/" + candidate + ".json";
            attempted.add(resource);
            if (exists(resource)) {
                return JsonUtils.fromClasspath(resource, type);
            }
        }
        throw new FrameworkException("No test data file for '" + fileName + "'. Looked for "
                + attempted + ". " + describeModule(module));
    }

    private static List<String> candidates(String fileName, String fallbackName) {
        List<String> names = new ArrayList<>();
        names.add(fileName);
        if (fallbackName != null && !fallbackName.equals(fileName)) {
            names.add(fallbackName);
        }
        return names;
    }

    /** {@code @TestModule} value, else the last segment of the package name. */
    public static String moduleOf(Class<?> testClass) {
        TestModule annotation = testClass.getAnnotation(TestModule.class);
        if (annotation != null && !annotation.value().isBlank()) {
            return annotation.value();
        }
        String packageName = testClass.getPackageName();
        int lastDot = packageName.lastIndexOf('.');
        return lastDot < 0 ? packageName : packageName.substring(lastDot + 1);
    }

    private static boolean exists(String resource) {
        return Thread.currentThread().getContextClassLoader().getResource(resource) != null;
    }

    /** Best-effort listing of a module folder, to make the failure actionable. */
    private static String describeModule(String module) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(ROOT + "/" + module);
        if (url == null) {
            return "The folder " + ROOT + "/" + module + " does not exist.";
        }
        try (Stream<Path> files = Files.list(Path.of(url.toURI()))) {
            List<String> names = files.map(p -> p.getFileName().toString())
                    .sorted(Comparator.naturalOrder())
                    .toList();
            return ROOT + "/" + module + " contains: " + names;
        } catch (Exception e) {
            return "Could not list " + ROOT + "/" + module + ".";
        }
    }
}
