package com.automation.framework.tests.ui;

import com.automation.framework.constants.FrameworkConstants;
import com.automation.framework.core.base.BaseUITest;
import com.automation.framework.pages.gyansathi.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Smoke test for GyanSathi, driven through the page object model.
 */
public class GyanSathiSmokeTest extends BaseUITest {

    @Test(groups = FrameworkConstants.GROUP_SMOKE,
            description = "Home page loads and is served from a gyansathi URL")
    public void homePageLoads() {
        logStep("Opening the GyanSathi home page");
        HomePage home = pageObject(HomePage.class).open();

        // Pin a screenshot to this exact step in the report.
        attachScreenshot("Home page loaded");

        Assert.assertTrue(home.getCurrentUrl().toLowerCase().contains("gyansathi"),
                "URL should contain 'gyansathi' but was: " + home.getCurrentUrl());
        Assert.assertTrue(home.isLoaded(), "Page body should be rendered");

        logStep("Home page title: " + home.getTitle());
    }
}
