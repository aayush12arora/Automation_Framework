package com.automation.framework.engine;

import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.core.driver.DriverManager;
import com.automation.framework.exceptions.FrameworkException;
import com.automation.framework.pages.DynamicPage;
import com.automation.framework.reporting.ExtentTestManager;
import com.automation.framework.reporting.ReportLogger;
import com.automation.framework.utilities.ScreenshotUtils;
import com.automation.framework.utilities.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interprets a {@link Flow} against a live browser session. This is the engine
 * that makes the framework "fully dynamic": any user journey for any application
 * can be expressed as data (a flow file) and run here with no bespoke code.
 *
 * <p>The executor tracks the "current page" so element steps may omit {@code page}
 * and reuse the page from the most recent {@code NAVIGATE}.
 */
public class FlowExecutor {

    private static final Logger log = LogManager.getLogger(FlowExecutor.class);

    private final WebDriver driver;
    private final ConfigurationManager config;
    private final Map<String, DynamicPage> pageCache = new HashMap<>();
    private String currentPageName;

    public FlowExecutor() {
        this(DriverManager.getDriver());
    }

    public FlowExecutor(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigurationManager.getInstance();
    }

    /**
     * Run every step in order. In fail-fast mode (default) the first failing
     * assertion throws immediately; otherwise all failures are collected and a
     * combined {@link AssertionError} is thrown at the end.
     */
    public void execute(Flow flow) {
        log.info("Executing flow '{}' ({} steps)", flow.displayName(), flow.getSteps().size());
        ExtentTestManager.info("Flow: <b>" + flow.displayName() + "</b>");

        boolean failFast = config.isFlowsFailFast();
        List<String> failures = new ArrayList<>();

        int index = 0;
        for (Step step : flow.getSteps()) {
            index++;
            String label = index + ". " + step.label();
            try {
                execute(step);
                ExtentTestManager.pass(label);
                log.info("PASS step {}", label);
            } catch (AssertionError ae) {
                String message = label + " -> ASSERTION FAILED: " + ae.getMessage();
                ExtentTestManager.fail(message);
                log.error(message);
                failures.add(message);
                if (failFast) {
                    throw ae;
                }
            } catch (RuntimeException re) {
                String message = label + " -> ERROR: " + re.getMessage();
                ExtentTestManager.fail(message);
                log.error(message, re);
                failures.add(message);
                if (failFast) {
                    throw re;
                }
            }
        }

        if (!failures.isEmpty()) {
            throw new AssertionError("Flow '" + flow.displayName() + "' had "
                    + failures.size() + " failure(s):\n - " + String.join("\n - ", failures));
        }
    }

    // ------------------------------------------------------------- step dispatch

    public void execute(Step step) {
        ActionType action = step.actionType();
        switch (action) {
            case NAVIGATE -> navigate(step);
            case NAVIGATE_URL -> navigateUrl(step);
            case CLICK -> page(step).click(requireElement(step));
            case TYPE -> page(step).type(requireElement(step), requireValue(step));
            case CLEAR -> page(step).clearField(requireElement(step));
            case SELECT -> page(step).selectByText(requireElement(step), requireValue(step));
            case WAIT_VISIBLE -> waitVisible(step);
            case WAIT_SECONDS -> waitSeconds(step);
            case SCROLL_TO -> page(step).scrollTo(page(step).locator(requireElement(step)));
            case SCREENSHOT -> screenshot(step);
            case ASSERT_VISIBLE -> assertVisible(step);
            case ASSERT_TEXT -> assertTextContains(step);
            case ASSERT_TEXT_EQUALS -> assertTextEquals(step);
            case ASSERT_TITLE_CONTAINS -> assertTitleContains(step);
            case ASSERT_URL_CONTAINS -> assertUrlContains(step);
            default -> throw new FrameworkException("No handler for action: " + action);
        }
    }

    // ------------------------------------------------------------------- handlers

    private void screenshot(Step step) {
        String label = step.getValue() == null ? "flow_step" : step.getValue();
        // Save a file copy for CI artifacts and embed the image in the report.
        ScreenshotUtils.capture(label);
        ReportLogger.screenshot("Screenshot: " + label);
    }

    private void navigate(Step step) {
        String pageName = step.getPage() != null ? step.getPage() : step.getValue();
        if (pageName == null || pageName.isBlank()) {
            throw new FrameworkException("NAVIGATE requires a 'page' (or 'value') naming a configured page");
        }
        page(pageName).open();
        currentPageName = pageName;
    }

    private void navigateUrl(Step step) {
        String url = requireValue(step);
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            String base = config.getBaseUrl();
            url = (base.endsWith("/") ? base.substring(0, base.length() - 1) : base)
                    + (url.startsWith("/") ? url : "/" + url);
        }
        driver.get(url);
    }

    private void waitVisible(Step step) {
        DynamicPage page = page(step);
        Duration timeout = timeout(step);
        WaitUtils.waitForVisible(driver, page.locator(requireElement(step)), timeout);
    }

    private void waitSeconds(Step step) {
        try {
            long millis = (long) (Double.parseDouble(requireValue(step)) * 1000);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (NumberFormatException e) {
            throw new FrameworkException("WAIT_SECONDS requires a numeric 'value'");
        }
    }

    private void assertVisible(Step step) {
        DynamicPage page = page(step);
        String element = requireElement(step);
        if (!page.isVisible(element)) {
            throw new AssertionError("Expected element '" + element + "' on page '"
                    + page.pageName() + "' to be visible");
        }
    }

    private void assertTextContains(Step step) {
        String actual = page(step).textOf(requireElement(step));
        String expected = requireValue(step);
        if (actual == null || !actual.contains(expected)) {
            throw new AssertionError("Expected text to contain '" + expected + "' but was '" + actual + "'");
        }
    }

    private void assertTextEquals(Step step) {
        String actual = page(step).textOf(requireElement(step));
        String expected = requireValue(step);
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected text '" + expected + "' but was '" + actual + "'");
        }
    }

    private void assertTitleContains(Step step) {
        String expected = requireValue(step);
        try {
            WaitUtils.waitForTitleContains(driver, expected);
        } catch (RuntimeException e) {
            throw new AssertionError("Expected title to contain '" + expected
                    + "' but was '" + driver.getTitle() + "'");
        }
    }

    private void assertUrlContains(Step step) {
        String expected = requireValue(step);
        try {
            WaitUtils.waitForUrlContains(driver, expected);
        } catch (RuntimeException e) {
            throw new AssertionError("Expected URL to contain '" + expected
                    + "' but was '" + driver.getCurrentUrl() + "'");
        }
    }

    // -------------------------------------------------------------------- helpers

    private DynamicPage page(Step step) {
        String pageName = step.getPage() != null ? step.getPage() : currentPageName;
        if (pageName == null) {
            throw new FrameworkException("Step '" + step.label()
                    + "' needs a 'page' (no page has been navigated yet)");
        }
        currentPageName = pageName;
        return page(pageName);
    }

    private DynamicPage page(String pageName) {
        return pageCache.computeIfAbsent(pageName, name -> new DynamicPage(driver, name));
    }

    private Duration timeout(Step step) {
        return step.getTimeoutSeconds() != null
                ? Duration.ofSeconds(step.getTimeoutSeconds())
                : config.getExplicitTimeout();
    }

    private static String requireElement(Step step) {
        if (step.getElement() == null || step.getElement().isBlank()) {
            throw new FrameworkException(step.actionType() + " requires an 'element'");
        }
        return step.getElement();
    }

    private static String requireValue(Step step) {
        if (step.getValue() == null) {
            throw new FrameworkException(step.actionType() + " requires a 'value'");
        }
        return step.getValue();
    }
}
