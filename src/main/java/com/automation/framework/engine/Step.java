package com.automation.framework.engine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single instruction in a flow, deserialized from YAML/JSON.
 *
 * <pre>
 *   - action: TYPE
 *     page: home
 *     element: searchInput
 *     value: "physics"
 *     description: "Search for physics"
 * </pre>
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {

    /** One of {@link ActionType}. */
    private String action;

    /** Page name (optional; defaults to the last navigated page). */
    private String page;

    /** Logical element name on the page (for element-scoped actions). */
    private String element;

    /** Free-form value: text to type, option to select, URL, seconds, expected text, etc. */
    private String value;

    /** Optional human-readable description used in logs and the report. */
    private String description;

    /** Optional per-step timeout override (seconds). */
    private Integer timeoutSeconds;

    public ActionType actionType() {
        return ActionType.from(action);
    }

    public String label() {
        if (description != null && !description.isBlank()) {
            return description;
        }
        StringBuilder sb = new StringBuilder(action == null ? "?" : action.toUpperCase());
        if (page != null) {
            sb.append(" page=").append(page);
        }
        if (element != null) {
            sb.append(" element=").append(element);
        }
        if (value != null) {
            sb.append(" value=").append(value);
        }
        return sb.toString();
    }
}
