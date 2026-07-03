package com.automation.framework.engine;

import com.automation.framework.utilities.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A named, ordered list of steps that describes an end-to-end user journey.
 * Flows are authored in YAML/JSON and executed by {@link FlowExecutor}, so a new
 * test can be added without writing any Java.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flow {

    private String name;
    private String description;

    /** Optional: which application this flow targets (informational). */
    private String application;

    private List<Step> steps = new ArrayList<>();

    public static Flow fromClasspath(String resource) {
        return JsonUtils.fromClasspath(resource, Flow.class);
    }

    public static Flow fromFile(Path path) {
        return JsonUtils.fromFile(path, Flow.class);
    }

    public String displayName() {
        return (name != null && !name.isBlank()) ? name : "unnamed-flow";
    }
}
