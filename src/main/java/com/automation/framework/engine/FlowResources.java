package com.automation.framework.engine;

import com.automation.framework.exceptions.FrameworkException;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Discovers and loads {@link Flow} definition files (*.yml / *.yaml / *.json)
 * from a classpath directory, so tests can be data-driven purely by dropping
 * files into a folder. Resolves the directory from the running classpath (works
 * when tests run from the exploded {@code target/classes} directory, as Surefire
 * does by default).
 */
public final class FlowResources {

    private FlowResources() {
    }

    /**
     * Load every flow file found directly under the given classpath directory.
     *
     * @param classpathDir e.g. {@code "flows/gyansathi/smoke"}
     */
    public static List<Flow> loadFromDirectory(String classpathDir) {
        Path dir = resolveDirectory(classpathDir);
        try (Stream<Path> files = Files.list(dir)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(FlowResources::isFlowFile)
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .map(Flow::fromFile)
                    .toList();
        } catch (Exception e) {
            throw new FrameworkException("Failed to load flows from '" + classpathDir + "'", e);
        }
    }

    private static Path resolveDirectory(String classpathDir) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(classpathDir);
        if (url == null) {
            throw new FrameworkException("Flow directory not found on classpath: " + classpathDir);
        }
        try {
            return Path.of(url.toURI());
        } catch (Exception e) {
            throw new FrameworkException("Cannot resolve flow directory URL: " + url, e);
        }
    }

    private static boolean isFlowFile(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".yml") || name.endsWith(".yaml") || name.endsWith(".json");
    }
}
