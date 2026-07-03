package com.automation.framework.core.driver;

import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.exceptions.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

/**
 * Creates configured {@link WebDriver} instances (local or remote) for a given
 * {@link BrowserType}. Selenium Manager (built into Selenium 4.6+) provisions
 * the matching browser driver binary automatically, so no manual driver setup
 * is required.
 */
public final class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver create() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        BrowserType browser = config.getBrowser();
        MutableCapabilities options = buildOptions(browser, config);

        WebDriver rawDriver = config.isRemoteExecution()
                ? createRemoteDriver(options, config)
                : createLocalDriver(browser, options, config);

        // Decorate with an event listener for richer logging.
        WebDriver driver = new org.openqa.selenium.support.events.EventFiringDecorator<>(
                new WebDriverEventListenerImpl()).decorate(rawDriver);

        applyTimeouts(driver, config);
        applyWindow(driver, config);
        log.info("Created {} driver (remote={})", browser, config.isRemoteExecution());
        return driver;
    }

    // ------------------------------------------------------------- browser options

    private static MutableCapabilities buildOptions(BrowserType browser, ConfigurationManager config) {
        return switch (browser) {
            case CHROME -> ChromeDriverHelper.buildOptions(config);
            case EDGE -> EdgeDriverHelper.buildOptions(config);
            case FIREFOX -> FirefoxDriverHelper.buildOptions(config);
            // Safari exposes almost no configurable options and ignores headless.
            case SAFARI -> new org.openqa.selenium.safari.SafariOptions();
        };
    }

    // --------------------------------------------------------------- driver builders

    private static WebDriver createLocalDriver(BrowserType browser, MutableCapabilities options,
                                               ConfigurationManager config) {
        String driverPath = config.getDriverPath();
        return switch (browser) {
            case CHROME -> {
                if (driverPath != null) {
                    var service = new org.openqa.selenium.chrome.ChromeDriverService.Builder()
                            .usingDriverExecutable(new java.io.File(driverPath)).build();
                    yield new org.openqa.selenium.chrome.ChromeDriver(service, (ChromeOptions) options);
                }
                yield new org.openqa.selenium.chrome.ChromeDriver((ChromeOptions) options);
            }
            case EDGE -> {
                if (driverPath != null) {
                    var service = new org.openqa.selenium.edge.EdgeDriverService.Builder()
                            .usingDriverExecutable(new java.io.File(driverPath)).build();
                    yield new org.openqa.selenium.edge.EdgeDriver(service, (EdgeOptions) options);
                }
                yield new org.openqa.selenium.edge.EdgeDriver((EdgeOptions) options);
            }
            case FIREFOX -> {
                if (driverPath != null) {
                    var service = new org.openqa.selenium.firefox.GeckoDriverService.Builder()
                            .usingDriverExecutable(new java.io.File(driverPath)).build();
                    yield new org.openqa.selenium.firefox.FirefoxDriver(service, (FirefoxOptions) options);
                }
                yield new org.openqa.selenium.firefox.FirefoxDriver((FirefoxOptions) options);
            }
            case SAFARI -> new org.openqa.selenium.safari.SafariDriver(
                    (org.openqa.selenium.safari.SafariOptions) options);
        };
    }

    private static WebDriver createRemoteDriver(MutableCapabilities options, ConfigurationManager config) {
        String remoteUrl = config.getRemoteUrl();
        if (remoteUrl == null || remoteUrl.isBlank()) {
            throw new FrameworkException("remote.execution=true but remote.url is not set");
        }
        try {
            return new RemoteWebDriver(new URL(remoteUrl), options);
        } catch (Exception e) {
            throw new FrameworkException("Failed to create remote driver at " + remoteUrl, e);
        }
    }

    // ------------------------------------------------------------------- post-setup

    private static void applyTimeouts(WebDriver driver, ConfigurationManager config) {
        driver.manage().timeouts().implicitlyWait(config.getImplicitTimeout());
        driver.manage().timeouts().pageLoadTimeout(config.getPageLoadTimeout());
    }

    private static void applyWindow(WebDriver driver, ConfigurationManager config) {
        String windowSize = config.getWindowSize();
        if ("maximize".equalsIgnoreCase(windowSize)) {
            driver.manage().window().maximize();
        } else if (windowSize.contains("x")) {
            String[] parts = windowSize.toLowerCase().split("x");
            try {
                driver.manage().window().setSize(
                        new Dimension(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())));
            } catch (RuntimeException ignored) {
                // Headless size args already applied; ignore malformed values here.
            }
        }
    }
}
