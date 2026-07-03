package com.automation.framework.utilities;

import com.automation.framework.exceptions.FrameworkException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Minimal file helpers used for artifacts and test data. Application-agnostic.
 */
public final class FileUtils {

    private FileUtils() {
    }

    public static void ensureDirectory(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new FrameworkException("Could not create directory: " + dir, e);
        }
    }

    public static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FrameworkException("Could not read file: " + path, e);
        }
    }

    public static void write(Path path, String content) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FrameworkException("Could not write file: " + path, e);
        }
    }
}
