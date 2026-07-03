package com.automation.framework.accessibility;

import com.deque.html.axecore.results.Rule;
import com.automation.framework.reporting.ExtentTestManager;

import java.util.List;

/**
 * Formats axe-core violations into a readable summary and logs them to the
 * Extent report. Generic and application-agnostic.
 */
public final class AxeViolationsReporter {

    private AxeViolationsReporter() {
    }

    public static String format(List<Rule> violations) {
        if (violations == null || violations.isEmpty()) {
            return "No accessibility violations.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(violations.size()).append(" accessibility violation(s):\n");
        for (Rule rule : violations) {
            sb.append(" - [").append(rule.getImpact()).append("] ")
                    .append(rule.getId()).append(": ").append(rule.getHelp())
                    .append(" (").append(rule.getNodes().size()).append(" node(s))\n");
        }
        return sb.toString();
    }

    /** Log the violations to the current Extent test node. */
    public static void report(List<Rule> violations) {
        String summary = format(violations);
        if (violations == null || violations.isEmpty()) {
            ExtentTestManager.pass(summary);
        } else {
            ExtentTestManager.warning(summary);
        }
    }
}
