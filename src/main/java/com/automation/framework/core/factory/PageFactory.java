package com.automation.framework.core.factory;

import com.automation.framework.core.base.BasePage;
import com.automation.framework.core.driver.DriverManager;
import com.automation.framework.exceptions.FrameworkException;
import com.automation.framework.pages.DynamicPage;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;

/**
 * Creates page objects. Two modes are supported:
 *
 * <ol>
 *   <li><b>Config-driven (default):</b> {@link #page(String)} returns a
 *       {@link DynamicPage} backed by the page definition in the application
 *       config. This needs no Java class and is how most pages are handled.</li>
 *   <li><b>Custom code (optional):</b> {@link #custom(Class)} instantiates a
 *       hand-written page class (extending {@link BasePage}) via reflection for
 *       the rare page whose behaviour is too complex to express in config.</li>
 * </ol>
 */
public final class PageFactory {

    private PageFactory() {
    }

    /** Config-driven page object for the named page. */
    public static DynamicPage page(String pageName) {
        return new DynamicPage(DriverManager.getDriver(), pageName);
    }

    /** Instantiate a custom page class that has a {@code (WebDriver)} constructor. */
    public static <T extends BasePage> T custom(Class<T> pageClass) {
        return custom(pageClass, DriverManager.getDriver());
    }

    public static <T extends BasePage> T custom(Class<T> pageClass, WebDriver driver) {
        try {
            Constructor<T> constructor = pageClass.getDeclaredConstructor(WebDriver.class);
            constructor.setAccessible(true);
            return constructor.newInstance(driver);
        } catch (NoSuchMethodException e) {
            throw new FrameworkException("Page class " + pageClass.getName()
                    + " must declare a public constructor taking a single WebDriver argument", e);
        } catch (Exception e) {
            throw new FrameworkException("Failed to instantiate page class " + pageClass.getName(), e);
        }
    }
}
