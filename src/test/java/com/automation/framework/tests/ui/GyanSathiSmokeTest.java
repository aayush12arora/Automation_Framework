package com.automation.framework.tests.ui;

import com.automation.framework.constants.FrameworkConstants;
import com.automation.framework.core.base.BaseUITest;
import com.automation.framework.pages.DynamicPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Config-driven smoke test for GyanSathi. Notice there is no page-specific Java:
 * pages and elements come from {@code applications/gyansathi/application.yml}.
 * Point the framework at another application (via {@code -Dapplication=...}) and
 * the same style of test works unchanged.
 */
public class GyanSathiSmokeTest extends BaseUITest {

    @Test(groups = FrameworkConstants.GROUP_SMOKE,
            description = "Home page loads and is served from a gyansathi URL")
    public void homePageLoads() {
        DynamicPage home = page("home").open();

        Assert.assertTrue(home.getCurrentUrl().toLowerCase().contains("gyansathi"),
                "URL should contain 'gyansathi' but was: " + home.getCurrentUrl());
        Assert.assertTrue(home.isVisible("body"), "Page body should be rendered");

        log.info("Home page title: {}", home.getTitle());
    }
}
