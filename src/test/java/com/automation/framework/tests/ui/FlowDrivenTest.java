package com.automation.framework.tests.ui;

import com.automation.framework.constants.FrameworkConstants;
import com.automation.framework.core.base.BaseUITest;
import com.automation.framework.engine.Flow;
import com.automation.framework.engine.FlowResources;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Runs every flow file under {@code flows/gyansathi/smoke} as its own test case.
 * Adding a new end-to-end test is therefore just dropping a YAML/JSON flow file
 * into that folder - no Java, no recompilation of test logic.
 */
public class FlowDrivenTest extends BaseUITest {

    private static final String SMOKE_FLOWS_DIR = "flows/gyansathi/smoke";

    @DataProvider(name = "smokeFlows")
    public Object[][] smokeFlows() {
        List<Flow> flows = FlowResources.loadFromDirectory(SMOKE_FLOWS_DIR);
        return flows.stream().map(flow -> new Object[]{flow}).toArray(Object[][]::new);
    }

    /**
     * Named {@code executeFlow} rather than {@code runFlow}: a method with the
     * latter name would override {@link BaseUITest#runFlow(Flow)} and recurse
     * into itself instead of delegating to the executor.
     */
    @Test(dataProvider = "smokeFlows",
            groups = FrameworkConstants.GROUP_SMOKE,
            description = "Execute a config-defined flow")
    public void executeFlow(Flow flow) {
        log.info("Running flow: {}", flow.displayName());
        runFlow(flow);
    }
}
