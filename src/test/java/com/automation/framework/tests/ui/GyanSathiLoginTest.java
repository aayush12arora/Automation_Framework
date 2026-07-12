package com.automation.framework.tests.ui;

import com.automation.framework.constants.FrameworkConstants;
import com.automation.framework.core.base.BaseUITest;
import com.automation.framework.core.data.TestModule;
import com.automation.framework.pages.gyansathi.HomePage;
import com.automation.framework.pages.gyansathi.LoginPage;
import com.automation.framework.pages.gyansathi.StudentCoursesPage;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Login tests for GyanSathi. Locators and page mechanics live in the page
 * objects; each test method's data is loaded by {@code BaseTest} from
 * {@code TestData/login/<methodName>.json} before the method runs.
 */
@TestModule("login")
public class GyanSathiLoginTest extends BaseUITest {

    @Test(groups = FrameworkConstants.GROUP_SMOKE,
            description = "A student signs in from the home page and lands on their courses")
    public void loginWithValidCredentials() {
        
        StudentCoursesPage courses = pageObject(HomePage.class)
                .open()
                .clickLogin()
                .signIn(customerData());

        Assert.assertTrue(courses.isLoggedIn(),
                "Logout should be visible after signing in, but the page did not reach "
                        + "an authenticated state. URL: " + courses.getCurrentUrl());
        Assert.assertTrue(courses.getCurrentUrl().contains(customerData().requireExtra("expectedLandingPath")),
                "Expected to land in the student area but was: " + courses.getCurrentUrl());
        Assert.assertEquals(courses.heading(), customerData().requireExtra("expectedHeading"));
    }

    @Test(groups = FrameworkConstants.GROUP_SMOKE,
            description = "Unknown credentials are rejected and the student area stays out of reach")
    public void invalidLoginIsRejected() {
        String expectedError = customerData().requireExtra("expectedError");

        LoginPage login = pageObject(LoginPage.class)
                .open()
                .signInExpectingFailure(customerData())
                .waitForError(expectedError);

        Assert.assertEquals(login.errorText(), expectedError);
        Assert.assertTrue(login.isCurrentPage(),
                "A rejected login should stay on the login page but went to: " + login.getCurrentUrl());
    }

       @Test(groups = FrameworkConstants.GROUP_SMOKE,
            description = "Unknown credentials are rejected and the student area stays out of reach")
    public void NumberOfCourses() {
        // String expectedError = customerData().requireExtra("expectedError");
                 StudentCoursesPage courses = pageObject(HomePage.class)
                .open()
                .clickLogin()
                .signIn(customerData());

          List<String> availableCourses = courses.GetAvailableCourses();
          
    }
}
