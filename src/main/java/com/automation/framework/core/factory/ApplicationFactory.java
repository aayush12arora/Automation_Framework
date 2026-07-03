package com.automation.framework.core.factory;

import com.automation.framework.core.config.ApplicationConfig;
import com.automation.framework.core.config.ConfigurationManager;
import com.automation.framework.core.config.EnvironmentConfig;
import com.automation.framework.pages.DynamicPage;

/**
 * Generic, application-agnostic factory that provides access to the active
 * application's configuration and builds config-driven pages for it. This is the
 * generic counterpart of a domain-specific factory: it works for any application
 * selected via the {@code application} property, with no per-application code.
 */
public final class ApplicationFactory {

    private ApplicationFactory() {
    }

    public static ApplicationConfig currentApplication() {
        return ConfigurationManager.getInstance().getApplicationConfig();
    }

    public static String currentApplicationName() {
        return ConfigurationManager.getInstance().getApplicationName();
    }

    public static EnvironmentConfig currentEnvironment() {
        return ConfigurationManager.getInstance().getEnvironment();
    }

    /** Build a config-driven page object for the named page of the active application. */
    public static DynamicPage page(String pageName) {
        return PageFactory.page(pageName);
    }
}
