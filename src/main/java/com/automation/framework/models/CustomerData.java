package com.automation.framework.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A customer/user record loaded from a JSON data file by
 * {@link com.automation.framework.core.data.TestDataReader}.
 *
 * <p>Fields the JSON declares beyond the ones below are kept in
 * {@link #getAdditional()} and read with {@link #extra(String)}, so a data file
 * can carry per-test expectations (an expected landing path, an expected error)
 * without this class growing a field for every test.
 */
@Data
@NoArgsConstructor
public class CustomerData {

    private String id;
    private String firstName;
    private String lastName;
    private String username;

    /** Excluded from toString so it never reaches a log line or an Extent report. */
    @ToString.Exclude
    private String password;

    private String email;
    private String phone;

    /** Any JSON property not mapped to a field above. */
    private Map<String, Object> additional = new LinkedHashMap<>();

    @JsonAnySetter
    public void addExtra(String key, Object value) {
        additional.put(key, value);
    }

    /** An unmapped JSON property, or null when the data file omits it. */
    public String extra(String key) {
        Object value = additional.get(key);
        return value == null ? null : String.valueOf(value);
    }

    /** An unmapped JSON property, failing loudly when the data file omits it. */
    public String requireExtra(String key) {
        String value = extra(key);
        if (value == null) {
            throw new IllegalStateException("Data file is missing required property '" + key
                    + "'. Present properties: " + additional.keySet());
        }
        return value;
    }

    public String fullName() {
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }
}
