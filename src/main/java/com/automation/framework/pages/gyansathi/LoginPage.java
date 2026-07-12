package com.automation.framework.pages.gyansathi;

import com.automation.framework.core.base.BasePage;
import com.automation.framework.models.CustomerData;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The GyanSathi sign-in page at {@code /auth/login}.
 *
 * <p>Two behaviours of this page are load-bearing and are handled here rather
 * than in every test: the form is client-rendered, so it appears a moment after
 * the URL changes; and the Sign In button carries the {@code disabled} attribute
 * until the terms checkbox is ticked.
 */
public class LoginPage extends BasePage {

    private static final String PATH = "/auth/login";

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By AGREE_TO_TERMS = By.id("agreedToTerms");
    private static final By SUBMIT = By.cssSelector("button[type='submit']");
    private static final By ERROR_MESSAGE = By.cssSelector("div.bg-red-50 p");
   
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        navigate(PATH);
        return waitUntilLoaded();
    }

    public LoginPage waitUntilLoaded() {
        waitForVisible(USERNAME);
        return this;
    }

    /** Sign in expecting success, landing on the student's courses. */
    public StudentCoursesPage signIn(CustomerData customer) {
        submitCredentials(customer);
        return new StudentCoursesPage(driver);
    }

    /** Sign in expecting rejection, staying on this page. */
    public LoginPage signInExpectingFailure(CustomerData customer) {
        submitCredentials(customer);
        return this;
    }

    /**
     * Wait for the red banner to show {@code expectedText}. The banner already
     * reads "Profile fetch failed" on a clean load, so waiting for the text to
     * change is the only reliable signal that the submission was answered.
     */
    public LoginPage waitForError(String expectedText) {
        waitForText(ERROR_MESSAGE, expectedText);
        return this;
    }

    public String errorText() {
        return getText(ERROR_MESSAGE);
    }


    public boolean isCurrentPage() {
        return getCurrentUrl().contains(PATH);
    }

    private void submitCredentials(CustomerData customer) {
        type(USERNAME, customer.getUsername());
        type(PASSWORD, customer.getPassword());
        click(AGREE_TO_TERMS);
        click(SUBMIT);
    }

}
