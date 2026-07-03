package com.automation.framework.core.config;

import com.automation.framework.core.driver.BrowserType;

/**
 * Immutable snapshot of the resolved browser settings. A convenience view over
 * {@link ConfigurationManager} for code that wants the browser configuration as
 * a single object.
 */
public final class BrowserConfig {

    private final BrowserType browser;
    private final boolean headless;
    private final boolean incognito;
    private final String windowSize;
    private final String binaryPath;
    private final String driverPath;

    private BrowserConfig(BrowserType browser, boolean headless, boolean incognito,
                          String windowSize, String binaryPath, String driverPath) {
        this.browser = browser;
        this.headless = headless;
        this.incognito = incognito;
        this.windowSize = windowSize;
        this.binaryPath = binaryPath;
        this.driverPath = driverPath;
    }

    public static BrowserConfig fromConfiguration() {
        ConfigurationManager c = ConfigurationManager.getInstance();
        return new BrowserConfig(c.getBrowser(), c.isHeadless(), c.isIncognito(),
                c.getWindowSize(), c.getBrowserBinary(), c.getDriverPath());
    }

    public BrowserType getBrowser() {
        return browser;
    }

    public boolean isHeadless() {
        return headless;
    }

    public boolean isIncognito() {
        return incognito;
    }

    public String getWindowSize() {
        return windowSize;
    }

    public String getBinaryPath() {
        return binaryPath;
    }

    public String getDriverPath() {
        return driverPath;
    }
}
