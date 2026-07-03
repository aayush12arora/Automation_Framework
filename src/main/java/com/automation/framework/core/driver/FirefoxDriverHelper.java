package com.automation.framework.core.driver;

import com.automation.framework.core.config.ConfigurationManager;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Builds a configured {@link FirefoxOptions} from the framework configuration.
 */
public final class FirefoxDriverHelper {

    private FirefoxDriverHelper() {
    }

    public static FirefoxOptions buildOptions(ConfigurationManager config) {
        FirefoxOptions options = new FirefoxOptions();
        if (config.isHeadless()) {
            options.addArguments("-headless");
        }
        if (config.isIncognito()) {
            options.addArguments("-private");
        }
        String binary = config.getBrowserBinary();
        if (binary != null) {
            options.setBinary(binary);
        }
        return options;
    }
}
