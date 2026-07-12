package com.automation.framework.core.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares which module folder a test class reads its data files from, i.e. the
 * {@code <module>} in {@code TestData/<module>/<testName>.json}.
 *
 * <pre>
 *   &#64;TestModule("login")
 *   public class GyanSathiLoginTest extends BaseUITest { ... }
 * </pre>
 *
 * When absent, the module defaults to the last segment of the test's package
 * (so {@code ...tests.ui.FooTest} reads from {@code TestData/ui/}).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface TestModule {

    /** Folder name under {@code TestData/}. */
    String value();
}
