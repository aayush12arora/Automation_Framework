package com.automation.framework.utilities;

import com.automation.framework.core.config.ConfigurationManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Central explicit-wait helpers. All timeouts default to the configured
 * explicit timeout but can be overridden per call. No {@code Thread.sleep}.
 */
public final class WaitUtils {

    private WaitUtils() {
    }

    private static WebDriverWait wait(WebDriver driver, Duration timeout) {
        ConfigurationManager config = ConfigurationManager.getInstance();
        WebDriverWait wait = new WebDriverWait(driver, timeout, config.getPollingInterval());
        return wait;
    }

    private static Duration defaultTimeout() {
        return ConfigurationManager.getInstance().getExplicitTimeout();
    }

    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return waitForVisible(driver, locator, defaultTimeout());
    }

    public static WebElement waitForVisible(WebDriver driver, By locator, Duration timeout) {
        return wait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return waitForClickable(driver, locator, defaultTimeout());
    }

    public static WebElement waitForClickable(WebDriver driver, By locator, Duration timeout) {
        return wait(driver, timeout).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return wait(driver, defaultTimeout()).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean waitForInvisible(WebDriver driver, By locator) {
        return wait(driver, defaultTimeout()).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean waitForTextPresent(WebDriver driver, By locator, String text) {
        return wait(driver, defaultTimeout()).until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public static boolean waitForUrlContains(WebDriver driver, String fragment) {
        return wait(driver, defaultTimeout()).until(ExpectedConditions.urlContains(fragment));
    }

    public static boolean waitForTitleContains(WebDriver driver, String fragment) {
        return wait(driver, defaultTimeout()).until(ExpectedConditions.titleContains(fragment));
    }

    public static List<WebElement> waitForAllVisible(WebDriver driver, By locator) {
        return wait(driver, defaultTimeout())
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }
}
