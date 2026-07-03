package com.automation.framework.core.base;

import com.automation.framework.utilities.SeleniumUtils;
import com.automation.framework.utilities.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * The reusable Selenium action layer. All interactions funnel through here so
 * that waiting, scrolling and logging are consistent everywhere. Page objects
 * (including the fully config-driven {@link com.automation.framework.pages.DynamicPage})
 * extend this class and work in terms of {@link By} locators.
 */
public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(getClass());
    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    // ------------------------------------------------------------------- actions

    public void click(By locator) {
        WebElement element = WaitUtils.waitForClickable(driver, locator);
        SeleniumUtils.scrollIntoView(driver, element);
        element.click();
        log.info("Clicked {}", locator);
    }

    public void type(By locator, String text) {
        WebElement element = WaitUtils.waitForVisible(driver, locator);
        element.clear();
        element.sendKeys(text);
        log.info("Typed '{}' into {}", text, locator);
    }

    public void clear(By locator) {
        WaitUtils.waitForVisible(driver, locator).clear();
    }

    public String getText(By locator) {
        return WaitUtils.waitForVisible(driver, locator).getText();
    }

    public String getAttribute(By locator, String attribute) {
        return WaitUtils.waitForPresence(driver, locator).getAttribute(attribute);
    }

    public void selectByVisibleText(By locator, String text) {
        new Select(WaitUtils.waitForVisible(driver, locator)).selectByVisibleText(text);
    }

    public void selectByValue(By locator, String value) {
        new Select(WaitUtils.waitForVisible(driver, locator)).selectByValue(value);
    }

    public void submit(By locator) {
        WaitUtils.waitForVisible(driver, locator).submit();
    }

    // -------------------------------------------------------------------- queries

    public boolean isDisplayed(By locator) {
        try {
            return WaitUtils.waitForVisible(driver, locator).isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isPresent(By locator) {
        return SeleniumUtils.isElementPresent(driver, locator);
    }

    public int count(By locator) {
        return driver.findElements(locator).size();
    }

    public List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    // --------------------------------------------------------------------- waits

    public void waitForVisible(By locator) {
        WaitUtils.waitForVisible(driver, locator);
    }

    public void waitForClickable(By locator) {
        WaitUtils.waitForClickable(driver, locator);
    }

    public void scrollTo(By locator) {
        SeleniumUtils.scrollIntoView(driver, WaitUtils.waitForPresence(driver, locator));
    }

    // ------------------------------------------------------------------ navigation

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
