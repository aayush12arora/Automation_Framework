package com.automation.framework.tests.unit;

import com.automation.framework.core.config.ApplicationConfig;
import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.engine.ActionType;
import com.automation.framework.engine.Flow;
import com.automation.framework.engine.FlowResources;
import com.automation.framework.enums.LocatorType;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Browser-free verification of the dynamic core: configuration loading, YAML
 * parsing, locator construction, and flow parsing. Runs in CI without a browser
 * and guards against broken application config or flow files.
 */
public class ConfigAndFlowParsingTest {

    @Test(groups = "unit", description = "Application config loads and exposes environments/pages")
    public void applicationConfigLoads() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        ApplicationConfig app = config.getApplicationConfig();

        Assert.assertEquals(app.getName(), "gyansathi");
        Assert.assertEquals(config.getBaseUrl(), "https://www.gyansathi.com");
        Assert.assertTrue(app.getPages().containsKey("home"), "home page should be defined");
        Assert.assertTrue(app.getPages().containsKey("login"), "login page should be defined");
    }

    @Test(groups = "unit", description = "Config-defined elements build valid Selenium locators")
    public void elementsResolveToLocators() {
        ApplicationConfig app = ConfigurationManager.getInstance().getApplicationConfig();

        By body = app.page("home").element("body").toBy();
        Assert.assertEquals(body, By.tagName("body"));

        // Every element on every page must produce a non-null By without throwing.
        app.getPages().forEach((pageName, page) ->
                page.getElements().forEach((elementName, element) ->
                        Assert.assertNotNull(element.toBy(),
                                "Locator null for " + pageName + "." + elementName)));
    }

    @Test(groups = "unit", description = "Locator type parsing accepts aliases")
    public void locatorAliases() {
        Assert.assertEquals(LocatorType.from("cssSelector"), LocatorType.CSS);
        Assert.assertEquals(LocatorType.from("class"), LocatorType.CLASS_NAME);
        Assert.assertEquals(LocatorType.from("XPATH"), LocatorType.XPATH);
    }

    @Test(groups = "unit", description = "Smoke flow files parse and every step has a known action")
    public void smokeFlowsParse() {
        List<Flow> flows = FlowResources.loadFromDirectory("flows/gyansathi/smoke");
        Assert.assertFalse(flows.isEmpty(), "at least one smoke flow should exist");

        for (Flow flow : flows) {
            Assert.assertFalse(flow.getSteps().isEmpty(), flow.displayName() + " should have steps");
            for (var step : flow.getSteps()) {
                ActionType action = step.actionType(); // throws if unknown
                Assert.assertNotNull(action);
            }
        }
    }

    @Test(groups = "unit", description = "Example flows are also valid so they stay runnable")
    public void exampleFlowsParse() {
        List<Flow> flows = FlowResources.loadFromDirectory("flows/gyansathi/examples");
        for (Flow flow : flows) {
            for (var step : flow.getSteps()) {
                Assert.assertNotNull(step.actionType());
            }
        }
    }
}
