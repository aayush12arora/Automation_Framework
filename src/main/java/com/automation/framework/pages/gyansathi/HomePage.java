package com.automation.framework.pages.gyansathi;

import com.automation.framework.core.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * The GyanSathi landing page.
 */
public class HomePage extends BasePage {

    private static final String PATH = "/";

    private static final By BODY = By.tagName("body");
    private static final By LOGIN_BUTTON = By.xpath("//button[normalize-space()='Login']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        navigate(PATH);
        return this;
    }

    /** The Login button is a nav <button>, not a link; it routes to /auth/login. */
    public LoginPage clickLogin() {
        click(LOGIN_BUTTON);
        return new LoginPage(driver).waitUntilLoaded();
    }

    public boolean isLoaded() {
        return isDisplayed(BODY);
    }
}
