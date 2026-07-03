package com.automation.framework.cloudproviders;

import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.exceptions.FrameworkException;
import org.openqa.selenium.MutableCapabilities;

/**
 * Helper for running against BrowserStack. Builds the hub URL and capabilities
 * from configuration. Generic - the caller supplies browser options; no
 * application-specific data is embedded.
 */
public final class BrowserStackHelper {

    private BrowserStackHelper() {
    }

    public static String hubUrl() {
        ConfigurationManager c = ConfigurationManager.getInstance();
        String user = c.get("browserstack.username");
        String key = c.get("browserstack.accesskey");
        if (user == null || key == null) {
            throw new FrameworkException("browserstack.username / browserstack.accesskey must be set");
        }
        return String.format("https://%s:%s@hub-cloud.browserstack.com/wd/hub", user, key);
    }

    /** Attach BrowserStack-specific options to an existing capabilities set. */
    public static MutableCapabilities decorate(MutableCapabilities options, String testName) {
        ConfigurationManager c = ConfigurationManager.getInstance();
        MutableCapabilities bstack = new MutableCapabilities();
        bstack.setCapability("sessionName", testName);
        bstack.setCapability("projectName", c.get("browserstack.project", c.getApplicationName()));
        bstack.setCapability("buildName", c.get("browserstack.build", "local-build"));
        options.setCapability("bstack:options", bstack);
        return options;
    }
}
