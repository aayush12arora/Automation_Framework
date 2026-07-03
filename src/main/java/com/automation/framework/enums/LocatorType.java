package com.automation.framework.enums;

import com.automation.framework.exceptions.FrameworkException;
import org.openqa.selenium.By;

/**
 * Every locator strategy Selenium supports, expressed so that element locators
 * can be declared entirely in configuration (YAML/JSON) instead of Java code.
 *
 * <p>Example config entry:
 * <pre>
 *   loginButton: { type: CSS, value: "button[type='submit']" }
 * </pre>
 */
public enum LocatorType {
    ID,
    NAME,
    CSS,
    XPATH,
    CLASS_NAME,
    TAG_NAME,
    LINK_TEXT,
    PARTIAL_LINK_TEXT;

    /**
     * Convert a strategy + raw locator string into a Selenium {@link By}.
     */
    public By toBy(String value) {
        return switch (this) {
            case ID -> By.id(value);
            case NAME -> By.name(value);
            case CSS -> By.cssSelector(value);
            case XPATH -> By.xpath(value);
            case CLASS_NAME -> By.className(value);
            case TAG_NAME -> By.tagName(value);
            case LINK_TEXT -> By.linkText(value);
            case PARTIAL_LINK_TEXT -> By.partialLinkText(value);
        };
    }

    /**
     * Case-insensitive lookup. Accepts a few friendly aliases
     * ({@code cssSelector}, {@code class}, {@code tag}, {@code link}).
     */
    public static LocatorType from(String value) {
        if (value == null || value.isBlank()) {
            throw new FrameworkException("Locator type is missing. "
                    + "Declare one of: ID, NAME, CSS, XPATH, CLASS_NAME, TAG_NAME, LINK_TEXT, PARTIAL_LINK_TEXT");
        }
        String normalized = value.trim().toUpperCase()
                .replace("CSSSELECTOR", "CSS")
                .replace("CLASSNAME", "CLASS_NAME")
                .replace("LINKTEXT", "LINK_TEXT");
        switch (normalized) {
            case "CLASS":
                normalized = "CLASS_NAME";
                break;
            case "TAG":
                normalized = "TAG_NAME";
                break;
            case "LINK":
                normalized = "LINK_TEXT";
                break;
            default:
                break;
        }
        try {
            return LocatorType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new FrameworkException("Unsupported locator type: '" + value + "'");
        }
    }
}
