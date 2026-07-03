package com.automation.framework.core.config;

import com.automation.framework.enums.LocatorType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openqa.selenium.By;

/**
 * A single named UI element, declared entirely in configuration.
 *
 * <pre>
 *   searchInput:
 *     type: CSS
 *     value: "input[name='q']"
 *     description: "Home page search box"
 * </pre>
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElementConfig {

    /** Locator strategy: ID, NAME, CSS, XPATH, CLASS_NAME, TAG_NAME, LINK_TEXT, PARTIAL_LINK_TEXT. */
    private String type;

    /** Raw locator value interpreted according to {@link #type}. */
    private String value;

    /** Optional human-readable description used in logs and reports. */
    private String description;

    /** Build the Selenium {@link By} for this element. */
    public By toBy() {
        return LocatorType.from(type).toBy(value);
    }

    /** Description if present, otherwise a compact "type=value" summary. */
    public String label() {
        return (description != null && !description.isBlank())
                ? description
                : (type + "=" + value);
    }
}
