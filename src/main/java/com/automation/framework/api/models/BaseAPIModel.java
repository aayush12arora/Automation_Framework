package com.automation.framework.api.models;

import com.automation.framework.utilities.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Base type for API request/response models. Provides JSON serialization so any
 * model can be logged or sent as a body. Application-specific models extend this.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseAPIModel {

    public String toJson() {
        return JsonUtils.toJson(this);
    }
}
