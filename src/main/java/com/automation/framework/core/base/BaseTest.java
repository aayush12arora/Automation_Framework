package com.automation.framework.core.base;

import com.automation.framework.core.config.ApplicationConfig;
import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.core.data.TestDataReader;
import com.automation.framework.exceptions.FrameworkException;
import com.automation.framework.models.CustomerData;
import com.automation.framework.reporting.StepReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

/**
 * Root of all test classes. Holds shared access to configuration and is the
 * place to put lifecycle hooks that apply to both UI and API tests.
 *
 * <p>Reporting is available everywhere without any per-class wiring: the
 * {@link com.automation.framework.reporting.TestListener} (registered in
 * {@code testng.xml}) creates one report node per test, and the {@link StepReporter}
 * mix-in gives every test (and every page object) {@code logStep(...)},
 * {@code attachScreenshot(...)} and {@code report()} for writing to that node.
 */
public abstract class BaseTest implements StepReporter {

    /**
     * State for the test method executing on this thread. These are thread-local
     * rather than plain fields because the suite runs {@code parallel="methods"}
     * and TestNG shares one instance of a test class across its methods.
     */
    private static final ThreadLocal<String> CURRENT_TEST_NAME = new ThreadLocal<>();
    private static final ThreadLocal<CustomerData> CUSTOMER_DATA = new ThreadLocal<>();

    protected final Logger log = LogManager.getLogger(getClass());
    protected final ConfigurationManager config = ConfigurationManager.getInstance();

    @BeforeSuite(alwaysRun = true)
    public void logSuiteContext() {
        log.info("==============================================================");
        log.info(" Application : {}", config.getApplicationName());
        log.info(" Environment : {}", config.getEnvironmentName());
        log.info("==============================================================");
    }

    /**
     * TestNG injects the method about to run, so the data file is resolved from
     * the test's own name: {@code TestData/<module>/<testName>.json}. Tests that
     * have no data file simply leave {@link #customerData()} unset.
     */
    @BeforeMethod(alwaysRun = true)
    public void loadTestData(Method method) {
        CURRENT_TEST_NAME.set(method.getName());
        CUSTOMER_DATA.set(TestDataReader.readIfPresent(getClass(), method.getName(), CustomerData.class));
    }

    @AfterMethod(alwaysRun = true)
    public void clearTestData() {
        CURRENT_TEST_NAME.remove();
        CUSTOMER_DATA.remove();
    }

    protected ApplicationConfig application() {
        return config.getApplicationConfig();
    }

    // Step logging (logStep / logPass / logFail / attachScreenshot / report) is
    // inherited from StepReporter, so it is available in every test and page object.

    /** This test method's customer, loaded from its JSON data file before the test ran. */
    protected CustomerData customerData() {
        CustomerData data = CUSTOMER_DATA.get();
        if (data == null) {
            throw new FrameworkException("Test '" + CURRENT_TEST_NAME.get() + "' has no data file. Expected "
                    + TestDataReader.ROOT + "/" + TestDataReader.moduleOf(getClass())
                    + "/" + CURRENT_TEST_NAME.get() + ".json");
        }
        return data;
    }

    /** Escape hatch for a test whose data file binds to a model other than {@link CustomerData}. */
    protected <T> T testData(Class<T> type) {
        String testName = CURRENT_TEST_NAME.get();
        if (testName == null) {
            throw new FrameworkException("testData() must be called from inside a @Test method");
        }
        return TestDataReader.read(getClass(), testName, type);
    }
}
