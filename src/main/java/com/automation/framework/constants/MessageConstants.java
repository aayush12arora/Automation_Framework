package com.automation.framework.constants;

/**
 * Reusable framework message templates for logs, reports, and assertions.
 */
public final class MessageConstants {

    private MessageConstants() {
    }

    public static final String ELEMENT_NOT_VISIBLE = "Element was not visible: %s";
    public static final String ELEMENT_NOT_FOUND = "Element not found: %s";
    public static final String PAGE_NOT_LOADED = "Page did not load in time: %s";
    public static final String UNEXPECTED_URL = "Unexpected URL. Expected to contain '%s' but was '%s'";
    public static final String UNEXPECTED_TITLE = "Unexpected title. Expected to contain '%s' but was '%s'";
    public static final String API_STATUS_MISMATCH = "Expected status %d but got %d";
}
