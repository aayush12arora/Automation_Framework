package com.automation.framework.engine;

import com.automation.framework.exceptions.FrameworkException;

/**
 * The vocabulary of the keyword-driven flow engine. A flow is a list of steps,
 * each naming one of these actions. New capabilities are added by extending this
 * enum and handling it in {@link FlowExecutor}.
 */
public enum ActionType {

    /** Navigate to a configured page by name (uses its path + baseUrl). */
    NAVIGATE,
    /** Navigate to an explicit URL (absolute, or relative to baseUrl). */
    NAVIGATE_URL,

    CLICK,
    TYPE,
    CLEAR,
    SELECT,            // select dropdown option by visible text

    WAIT_VISIBLE,
    WAIT_SECONDS,      // fixed pause; use sparingly
    SCROLL_TO,
    SCREENSHOT,

    // Assertions -------------------------------------------------------------
    ASSERT_VISIBLE,
    ASSERT_TEXT,           // element text contains value
    ASSERT_TEXT_EQUALS,    // element text equals value
    ASSERT_TITLE_CONTAINS,
    ASSERT_URL_CONTAINS;

    public static ActionType from(String value) {
        if (value == null || value.isBlank()) {
            throw new FrameworkException("Flow step is missing an 'action'");
        }
        try {
            return ActionType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FrameworkException("Unknown flow action: '" + value + "'");
        }
    }
}
