package com.automation.framework.pages;

import com.automation.framework.core.base.BasePage;
import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.core.config.ElementConfig;
import com.automation.framework.core.config.PageConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * A page object whose elements and URL come entirely from configuration.
 *
 * <p>This is the core of the framework's "any application" capability: instead
 * of writing a Java class per page, you describe pages and elements in
 * {@code application.yml} and interact with them here by their logical names.
 *
 * <pre>
 *   DynamicPage home = new DynamicPage("home");
 *   home.open();
 *   home.type("searchInput", "physics");
 *   home.click("searchButton");
 *   String title = home.getText("resultsHeader");
 * </pre>
 *
 * All actions inherit the waiting/scrolling/logging behaviour of {@link BasePage}.
 */
public class DynamicPage extends BasePage {

    private final PageConfig pageConfig;
    private final ConfigurationManager config;

    public DynamicPage(String pageName) {
        this(com.automation.framework.core.driver.DriverManager.getDriver(), pageName);
    }

    public DynamicPage(WebDriver driver, String pageName) {
        super(driver);
        this.config = ConfigurationManager.getInstance();
        this.pageConfig = config.getApplicationConfig().page(pageName);
    }

    /** Navigate to this page (environment baseUrl + the page's configured path). */
    public DynamicPage open() {
        String url = joinUrl(config.getBaseUrl(), pageConfig.getPath());
        log.info("Opening page '{}' at {}", pageConfig.getName(), url);
        driver.get(url);
        return this;
    }

    /** Resolve a logical element name into a Selenium {@link By}. */
    public By locator(String elementName) {
        return pageConfig.element(elementName).toBy();
    }

    public ElementConfig elementConfig(String elementName) {
        return pageConfig.element(elementName);
    }

    // ---- action overloads that take logical element names ----

    public DynamicPage click(String elementName) {
        click(locator(elementName));
        return this;
    }

    public DynamicPage type(String elementName, String text) {
        type(locator(elementName), text);
        return this;
    }

    public DynamicPage clearField(String elementName) {
        clear(locator(elementName));
        return this;
    }

    public String textOf(String elementName) {
        return getText(locator(elementName));
    }

    public String attributeOf(String elementName, String attribute) {
        return getAttribute(locator(elementName), attribute);
    }

    public boolean isVisible(String elementName) {
        return isDisplayed(locator(elementName));
    }

    public boolean exists(String elementName) {
        return isPresent(locator(elementName));
    }

    public DynamicPage selectByText(String elementName, String visibleText) {
        selectByVisibleText(locator(elementName), visibleText);
        return this;
    }

    public DynamicPage waitFor(String elementName) {
        waitForVisible(locator(elementName));
        return this;
    }

    public DynamicPage waitForText(String elementName, String text) {
        waitForText(locator(elementName), text);
        return this;
    }

    public String pageName() {
        return pageConfig.getName();
    }
}
