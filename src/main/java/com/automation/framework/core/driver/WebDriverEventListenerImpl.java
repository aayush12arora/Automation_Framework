package com.automation.framework.core.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Logs low-level WebDriver events (navigation, clicks, input, element lookups)
 * for richer diagnostics. Attached to the driver via an
 * {@link org.openqa.selenium.support.events.EventFiringDecorator} in
 * {@link DriverFactory}.
 */
public class WebDriverEventListenerImpl implements WebDriverListener {

    private static final Logger log = LogManager.getLogger(WebDriverEventListenerImpl.class);

    @Override
    public void beforeGet(WebDriver driver, String url) {
        log.debug("Navigating to: {}", url);
    }

    @Override
    public void beforeClick(WebElement element) {
        log.debug("Clicking element");
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        log.debug("Sending keys to element");
    }

    @Override
    public void beforeFindElement(WebDriver driver, By locator) {
        log.debug("Finding element: {}", locator);
    }

    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        log.warn("WebDriver error during {}: {}", method.getName(), e.getMessage());
    }
}
