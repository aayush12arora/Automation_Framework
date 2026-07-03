package com.automation.framework.enums;

/**
 * Generic UI surface types. An application's pages can be tagged with a UI type
 * when it ships multiple front-ends (e.g. desktop web vs mobile web). Kept
 * application-agnostic - no product-specific "old UI / new UI" assumptions.
 */
public enum UIType {
    WEB,
    MOBILE_WEB,
    RESPONSIVE;

    public static UIType from(String value) {
        if (value == null || value.isBlank()) {
            return WEB;
        }
        try {
            return UIType.valueOf(value.trim().toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return WEB;
        }
    }
}
