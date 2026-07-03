package com.automation.framework.core.base;

import com.automation.framework.api.soap.SoapAPIExecutor;
import org.testng.annotations.BeforeClass;

/**
 * Base class for SOAP API tests. Exposes a ready-to-use {@link SoapAPIExecutor}.
 * Kept generic - supply the endpoint, SOAPAction, and envelope per test.
 */
public abstract class BaseSoapAPITest extends BaseTest {

    protected SoapAPIExecutor soapExecutor;

    @BeforeClass(alwaysRun = true)
    public void initSoap() {
        soapExecutor = new SoapAPIExecutor();
        log.info("SOAP executor initialised for application '{}'", config.getApplicationName());
    }
}
