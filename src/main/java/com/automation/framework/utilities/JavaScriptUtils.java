package com.automation.framework.utilities;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * JavaScript-executor based helpers for actions the standard API cannot do
 * directly (JS click, scroll, value injection). Application-agnostic.
 */
public final class JavaScriptUtils {

    private JavaScriptUtils() {
    }

    private static JavascriptExecutor js(WebDriver driver) {
        return (JavascriptExecutor) driver;
    }

    public static Object execute(WebDriver driver, String script, Object... args) {
        return js(driver).executeScript(script, args);
    }

    public static void click(WebDriver driver, WebElement element) {
        js(driver).executeScript("arguments[0].click();", element);
    }

    public static void scrollToBottom(WebDriver driver) {
        js(driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public static void scrollIntoView(WebDriver driver, WebElement element) {
        js(driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public static void setValue(WebDriver driver, WebElement element, String value) {
        js(driver).executeScript("arguments[0].value=arguments[1];", element, value);
    }

    public static String pageReadyState(WebDriver driver) {
        return String.valueOf(js(driver).executeScript("return document.readyState"));
    }
}
