package com.automation.framework.listeners;

import com.automation.framework.core.config.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test up to {@code retry.count} times (from framework.properties)
 * to absorb environmental flakiness. Wire in per-test with
 * {@code @Test(retryAnalyzer = RetryAnalyzer.class)} or globally via a listener.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);
    private final int maxRetries = ConfigurationManager.getInstance().getRetryCount();
    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < maxRetries) {
            attempts++;
            log.warn("Retrying {} (attempt {}/{})",
                    result.getMethod().getMethodName(), attempts, maxRetries);
            return true;
        }
        return false;
    }
}
