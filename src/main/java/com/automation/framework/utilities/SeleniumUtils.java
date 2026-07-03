package com.automation.framework.utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Stateless Selenium helper methods that do not belong to a single page.
 */
public final class SeleniumUtils {

    private SeleniumUtils() {
    }

    public static boolean isElementPresent(WebDriver driver, By locator) {
        try {
            return !driver.findElements(locator).isEmpty();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void scrollIntoView(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
    }

    public static void jsClick(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public static void highlight(WebDriver driver, WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.border='3px solid red';", element);
        } catch (RuntimeException ignored) {
            // Highlighting is best-effort only.
        }
    }

    /** True once document.readyState is 'complete'. */
    public static boolean isPageLoaded(WebDriver driver) {
        try {
            Object state = ((JavascriptExecutor) driver).executeScript("return document.readyState");
            return "complete".equals(state);
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static WebElement find(WebDriver driver, By locator) {
        try {
            return driver.findElement(locator);
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
