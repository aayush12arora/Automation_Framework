package com.automation.framework.core.config;

import com.automation.framework.exceptions.FrameworkException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A logical page: a relative URL path plus a map of named elements.
 * Pages are declared under {@code pages:} in an application's config file.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageConfig {

    /** Set automatically from the config map key; also settable in config. */
    private String name;

    /** Relative path appended to the environment baseUrl, e.g. "/login". */
    private String path = "";

    /** Logical element name -> element definition. */
    private Map<String, ElementConfig> elements = new LinkedHashMap<>();

    /** Look up an element by its logical name, failing clearly if absent. */
    public ElementConfig element(String elementName) {
        ElementConfig element = elements.get(elementName);
        if (element == null) {
            throw new FrameworkException("Element '" + elementName + "' is not defined on page '"
                    + name + "'. Known elements: " + elements.keySet());
        }
        return element;
    }

    public boolean hasElement(String elementName) {
        return elements.containsKey(elementName);
    }
}
