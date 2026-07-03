package com.automation.framework.cloudproviders;

import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.exceptions.FrameworkException;
import org.openqa.selenium.MutableCapabilities;

/**
 * Helper for running against Sauce Labs. Builds the hub URL and capabilities
 * from configuration. Generic and application-agnostic.
 */
public final class SauceLabsHelper {

    private SauceLabsHelper() {
    }

    public static String hubUrl() {
        ConfigurationManager c = ConfigurationManager.getInstance();
        String user = c.get("saucelabs.username");
        String key = c.get("saucelabs.accesskey");
        if (user == null || key == null) {
            throw new FrameworkException("saucelabs.username / saucelabs.accesskey must be set");
        }
        return String.format("https://%s:%s@ondemand.us-west-1.saucelabs.com:443/wd/hub", user, key);
    }

    public static MutableCapabilities decorate(MutableCapabilities options, String testName) {
        MutableCapabilities sauce = new MutableCapabilities();
        sauce.setCapability("name", testName);
        sauce.setCapability("build", ConfigurationManager.getInstance().get("saucelabs.build", "local-build"));
        options.setCapability("sauce:options", sauce);
        return options;
    }
}
