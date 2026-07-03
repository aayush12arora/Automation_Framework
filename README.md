# Dynamic Automation Framework

A **fully dynamic, configuration-driven** UI + API test automation framework built
on **Java 17, Selenium 4, and TestNG**. Any web application is onboarded through
external **YAML/JSON configuration** — environments, pages, element locators, and
executable test flows — **with no code changes**. It is configured out of the box
for **[www.gyansathi.com](https://www.gyansathi.com)**.

The design generalizes the Page Object Model blueprint in
`Java Automation Framework – Comprehensive Development Guide` into an
application-agnostic engine: instead of one Java class per page per application,
pages and elements are declared as data and driven by a generic runtime.

---

## Why "dynamic"?

| Traditional POM framework | This framework |
|---|---|
| One Java class per page | Pages declared in `application.yml` |
| Locators hard-coded in Java | Locators declared in config (`type` + `value`) |
| New app = new source tree | New app = new config folder |
| New test = new Java method | New test = new flow file (YAML/JSON) |
| App-specific (e.g. billers/payments) | Application-agnostic core |

You *can* still write Java page objects and tests when a scenario is genuinely
complex — the framework supports both — but the default path needs no code.

---

## Quick start

```bash
# Prerequisites: JDK 17+, Maven 3.9+, and a local browser (Chrome/Edge/Firefox).
# Selenium Manager auto-provisions the matching driver when it has network access.

# Run the default smoke suite against gyansathi (prod)
mvn clean test

# Choose application / environment / browser at runtime
mvn test -Dapplication=gyansathi -Denvironment=prod -Dbrowser=chrome -Dheadless=true

# Run the browser-free config/flow validation suite (no browser needed)
mvn test -DsuiteXmlFile=testng-unit.xml
```

Reports land in `test-output/reports/`, screenshots in `test-output/screenshots/`,
logs in `test-output/logs/`.

---

## Project layout

```
src/main/java/com/automation/framework/
├── core/
│   ├── config/          ConfigurationManager + config POJOs (Application/Page/Element/Environment)
│   ├── driver/          DriverManager (thread-local) + DriverFactory (multi-browser/remote)
│   ├── base/            BasePage, BaseTest, BaseUITest, BaseAPITest
│   └── factory/         PageFactory (config-driven + reflection for custom pages)
├── pages/               DynamicPage — the config-driven page object
├── engine/              Flow, Step, ActionType, FlowExecutor, FlowResources (keyword engine)
├── api/                 RestClient (generic REST-Assured wrapper)
├── reporting/           ExtentManager, ExtentTestManager, TestListener
├── listeners/           RetryAnalyzer
├── utilities/           WaitUtils, SeleniumUtils, ScreenshotUtils, JsonUtils, RandomDataGenerator
├── enums/               BrowserType, LocatorType
├── constants/           FrameworkConstants
└── exceptions/          FrameworkException

src/main/resources/
├── config/
│   ├── framework.properties     Global, application-agnostic settings
│   └── log4j2.xml
├── applications/
│   └── gyansathi/
│       └── application.yml       Environments + pages + elements for GyanSathi
└── flows/
    └── gyansathi/
        ├── smoke/                Flows run by the smoke suite
        └── examples/             Example journeys to adapt

src/test/java/com/automation/framework/tests/
├── ui/                  GyanSathiSmokeTest (config-driven), FlowDrivenTest (data-driven by flow files)
└── unit/                ConfigAndFlowParsingTest (browser-free validation)
```

---

## How configuration resolves

Global settings (`framework.properties`) can be overridden at runtime. Precedence,
highest first:

1. JVM system property — `-Dbrowser=firefox`
2. OS environment variable — `BROWSER=firefox` (upper snake case; `.` → `_`)
3. `config/framework.properties`

Key settings:

| Property | Meaning | Default |
|---|---|---|
| `application` | Which app config folder to load | `gyansathi` |
| `environment` | Which environment in that app | `prod` |
| `browser` | `chrome` \| `firefox` \| `edge` \| `safari` | `chrome` |
| `headless` | Headless mode | `true` |
| `window.size` | `WxH` or `maximize` | `1920x1080` |
| `browser.binary` | Explicit browser binary path (CI/containers) | *(auto)* |
| `driver.path` | Explicit driver binary path | *(Selenium Manager)* |
| `timeout.explicit` | Explicit wait seconds | `20` |
| `remote.execution` / `remote.url` | Selenium Grid / cloud hub | `false` |
| `retry.count` | Retry attempts for flaky tests | `1` |
| `flows.failfast` | Stop a flow on first assertion failure | `true` |

---

## Onboarding a new application (no code)

1. Create `src/main/resources/applications/<yourapp>/application.yml`.
2. Declare its environments, pages, and elements (see the annotated
   `gyansathi/application.yml`).
3. Run against it: `mvn test -Dapplication=<yourapp> -Denvironment=<env>`.

```yaml
name: myapp
environments:
  prod:
    baseUrl: "https://app.example.com"
    apiBaseUrl: "https://app.example.com/api"
pages:
  login:
    path: "/login"
    elements:
      username:
        type: CSS            # ID | NAME | CSS | XPATH | CLASS_NAME | TAG_NAME | LINK_TEXT | PARTIAL_LINK_TEXT
        value: "#email"
      password:
        type: CSS
        value: "#password"
      submit:
        type: CSS
        value: "button[type='submit']"
```

---

## Writing tests

### 1. Config-driven page objects (`DynamicPage`)

```java
public class LoginTest extends BaseUITest {
    @Test(groups = "smoke")
    public void invalidLogin() {
        DynamicPage login = page("login").open();     // navigate using config path
        login.type("username", "bad@user.com")
             .type("password", "wrong")
             .click("submit");
        Assert.assertTrue(login.isVisible("errorMessage"));
    }
}
```

### 2. Keyword/flow-driven tests (zero Java per test)

Describe a journey as data and drop it in a flow folder:

```yaml
# src/main/resources/flows/myapp/smoke/login.yml
name: "Invalid login shows error"
steps:
  - { action: NAVIGATE, page: login }
  - { action: TYPE, page: login, element: username, value: "bad@user.com" }
  - { action: TYPE, page: login, element: password, value: "wrong" }
  - { action: CLICK, page: login, element: submit }
  - { action: ASSERT_VISIBLE, page: login, element: errorMessage }
```

`FlowDrivenTest` executes every flow in a folder as its own test case, so adding a
test means adding a file.

**Supported flow actions:** `NAVIGATE`, `NAVIGATE_URL`, `CLICK`, `TYPE`, `CLEAR`,
`SELECT`, `WAIT_VISIBLE`, `WAIT_SECONDS`, `SCROLL_TO`, `SCREENSHOT`,
`ASSERT_VISIBLE`, `ASSERT_TEXT`, `ASSERT_TEXT_EQUALS`, `ASSERT_TITLE_CONTAINS`,
`ASSERT_URL_CONTAINS`. After a `NAVIGATE`, later steps may omit `page` to reuse it.

### 3. API tests

```java
public class HealthApiTest extends BaseAPITest {
    @Test(groups = "api")
    public void ping() {
        Response r = restClient.get("/health", Map.of());
        Assert.assertEquals(r.statusCode(), 200);
    }
}
```

---

## Parallel execution & reporting

- **Parallel**: the driver is thread-local (`DriverManager`), and `testng.xml`
  runs `parallel="methods" thread-count="3"`. Tune as needed.
- **Reporting**: `TestListener` (registered in `testng.xml`) drives ExtentReports,
  attaching a screenshot on failure. System info (app, env, browser) is recorded.
- **Retries**: annotate a test with
  `@Test(retryAnalyzer = RetryAnalyzer.class)` to absorb flakiness.

---

## GyanSathi notes

`applications/gyansathi/application.yml` ships with **starter locators** based on
common web patterns. Because the site's live DOM was not accessible from the build
environment, these should be **verified/tuned against the real page** (inspect the
element, update `type`/`value`). The shipped **smoke flow uses only DOM-agnostic
checks** (page loads, URL contains `gyansathi`, `<body>` renders) so it is green
without DOM knowledge; the `examples/` flows (search, login) are templates to
enable once real selectors are confirmed.

---

## Running in constrained/CI environments

If the browser is installed in a non-standard location or Selenium Manager cannot
reach the driver download host, point the framework directly at the binaries:

```bash
mvn test \
  -Dbrowser.binary=/path/to/chrome \
  -Ddriver.path=/path/to/chromedriver
```

For a Selenium Grid or cloud provider:

```bash
mvn test -Dremote.execution=true -Dremote.url=https://<grid-or-hub>/wd/hub
```

---

## Tech stack

Selenium 4 · TestNG 7 · ExtentReports 5 · REST-Assured 5 · Jackson (YAML/JSON) ·
Log4j 2 · AssertJ · Datafaker · HikariCP · Lombok.
