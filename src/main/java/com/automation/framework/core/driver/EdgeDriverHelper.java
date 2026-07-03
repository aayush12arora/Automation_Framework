package com.automation.framework.core.driver;

import com.automation.framework.core.config.ConfigurationManager;
import org.openqa.selenium.edge.EdgeOptions;

/**
 * Builds a configured {@link EdgeOptions} from the framework configuration.
 */
public final class EdgeDriverHelper {

    private EdgeDriverHelper() {
    }

    public static EdgeOptions buildOptions(ConfigurationManager config) {
        EdgeOptions options = new EdgeOptions();
        if (config.isHeadless()) {
            options.addArguments("--headless=new");
        }
        if (config.isIncognito()) {
            options.addArguments("--inprivate");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

        String windowSize = config.getWindowSize();
        if (!"maximize".equalsIgnoreCase(windowSize)) {
            options.addArguments("--window-size=" + windowSize.replace('x', ','));
        }
        String binary = config.getBrowserBinary();
        if (binary != null) {
            options.setBinary(binary);
        }
        return options;
    }
}
