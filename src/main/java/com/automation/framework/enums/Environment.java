package com.automation.framework.enums;

/**
 * Common environment names. Environments in application config are free-form
 * strings, so this enum is a convenience for the usual cases - it does not
 * constrain what an application may define.
 */
public enum Environment {
    DEV,
    QA,
    STAGE,
    PROD;

    public static Environment from(String value) {
        if (value == null) {
            return PROD;
        }
        try {
            return Environment.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PROD;
        }
    }
}
