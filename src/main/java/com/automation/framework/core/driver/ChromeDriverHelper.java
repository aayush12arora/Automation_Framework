package com.automation.framework.core.driver;

import com.automation.framework.core.config.ConfigurationManager;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Builds a configured {@link ChromeOptions} from the framework configuration.
 * Extracted from {@link DriverFactory} so browser-specific option logic lives in
 * one place per browser, matching the reference framework structure.
 */
public final class ChromeDriverHelper {

    private ChromeDriverHelper() {
    }

    public static ChromeOptions buildOptions(ConfigurationManager config) {
        ChromeOptions options = new ChromeOptions();
        if (config.isHeadless()) {
            options.addArguments("--headless=new");
        }
        if (config.isIncognito()) {
            options.addArguments("--incognito");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage",
                "--disable-gpu", "--remote-allow-origins=*");

        String windowSize = config.getWindowSize();
        boolean maximize = "maximize".equalsIgnoreCase(windowSize);
        if (config.isHeadless()) {
            // Headless Chrome has no window manager, so window().maximize() does
            // nothing and the viewport stays at the 800x600 default - which trips
            // responsive sites into their mobile layout. Force a desktop size:
            // an explicit "WxH" is honoured; "maximize" maps to a large default.
            options.addArguments("--window-size=" + (maximize ? "1920,1080" : windowSize.replace('x', ',')));
        } else if (!maximize) {
            options.addArguments("--window-size=" + windowSize.replace('x', ','));
        }
        String binary = config.getBrowserBinary();
        if (binary != null) {
            options.setBinary(binary);
        }
        return options;
    }
}
