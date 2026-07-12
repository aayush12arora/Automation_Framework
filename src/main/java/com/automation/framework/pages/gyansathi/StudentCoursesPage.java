package com.automation.framework.pages.gyansathi;

import com.automation.framework.core.base.BasePage;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Where a successful sign-in lands: the student's available courses.
 */
public class StudentCoursesPage extends BasePage {

    private static final String PATH = "/student/courses/available_courses";

    private static final By HEADING = By.xpath("//h1[normalize-space()='Available Courses']");
    private static final By LOGOUT_BUTTON = By.xpath("//button[normalize-space()='Logout']");
 private static final By ENROLLED_COURSES= By.xpath("//div[contains(@class,'border-green')]");
    public StudentCoursesPage(WebDriver driver) {
        super(driver);
    }

    public StudentCoursesPage open() {
        navigate(PATH);
        return this;
    }

    /** Logout only renders for an authenticated session, so it doubles as the login assertion. */
    public boolean isLoggedIn() {
        return isDisplayed(LOGOUT_BUTTON);
    }

    public String heading() {
        return getText(HEADING);
    }

    
    public List<String>GetAvailableCourses(){

        waitForVisible(ENROLLED_COURSES);
        List<String> courses = new ArrayList<>();
        List<WebElement> courseElements = findAll(ENROLLED_COURSES);
        for (WebElement element : courseElements) {
            courses.add(element.getText());
            logStep("Available course: {}", element.getText());
        }
        attachScreenshot("Available courses ({} found)", courses.size());
        return courses;
    }

    private void ClickOnOkforPasswordAlert() {
        try {
            driver.switchTo().alert().accept();
            log.info("Clicked OK on password alert");
        } catch (Exception e) {
            log.info("No password alert present");
        }
    }
}
