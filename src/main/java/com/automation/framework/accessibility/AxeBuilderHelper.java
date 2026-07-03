package com.automation.framework.accessibility;

import com.automation.framework.core.driver.DriverManager;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Runs Deque axe-core accessibility scans on the current page. Generic - the
 * caller chooses which WCAG tags to check. Application-agnostic.
 */
public final class AxeBuilderHelper {

    private static final Logger log = LogManager.getLogger(AxeBuilderHelper.class);

    private AxeBuilderHelper() {
    }

    /** Analyze the current page for the given WCAG tags (e.g. "wcag2a", "wcag2aa"). */
    public static Results analyze(WebDriver driver, String... tags) {
        AxeBuilder builder = new AxeBuilder();
        if (tags != null && tags.length > 0) {
            builder.withTags(List.of(tags));
        }
        Results results = builder.analyze(driver);
        log.info("Accessibility scan found {} violation(s)", results.getViolations().size());
        return results;
    }

    public static Results analyze(String... tags) {
        return analyze(DriverManager.getDriver(), tags);
    }

    public static List<Rule> violations(WebDriver driver, String... tags) {
        return analyze(driver, tags).getViolations();
    }
}
