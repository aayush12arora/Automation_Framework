package com.automation.framework.pages.interfaces;

/**
 * Minimal contract implemented by page objects. Deliberately generic (no
 * application-specific methods) so it fits any page. Concrete page objects -
 * whether the config-driven {@link com.automation.framework.pages.DynamicPage}
 * or hand-written classes - can implement it to expose a uniform surface.
 */
public interface IPage {

    /** Navigate the browser to this page. */
    void open();

    /** The logical page name (matches the key in application config). */
    String pageName();

    /** Current page title. */
    String getTitle();

    /** Current browser URL. */
    String getCurrentUrl();
}
