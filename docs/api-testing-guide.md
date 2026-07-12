# API testing with this framework

This framework does UI **and** API testing from the same configuration and
reporting stack. This guide explains the API layer that ships in the repo, then
walks a working REST example against the public dummy API
[JSONPlaceholder](https://jsonplaceholder.typicode.com). Everything here was run
green: `mvn test -DsuiteXmlFile=testng-api.xml -Dapplication=jsonplaceholder` →
`Tests run: 5, Failures: 0`.

---

## 1. What the framework provides for API testing

| Class | Package | Role |
|-------|---------|------|
| `RestAPIExecutor` | `api.executors` | Thin REST-Assured wrapper: `get/post/put/delete(endpoint, [body], headers)` |
| `SoapAPIExecutor` | `api.soap` | Raw SOAP: POSTs a SOAP envelope with a `SOAPAction` header, returns the body |
| `BaseAPITest` | `core.base` | Base class for REST tests; builds `restClient` from the environment's `apiBaseUrl` |
| `BaseSoapAPITest` | `core.base` | Base class for SOAP tests; exposes a `soapExecutor` |
| `ApiRequest` / `ApiResponse` | `api.models.*` | Generic request/response POJOs |
| `BaseAPIModel` | `api.models` | Parent for API models; gives every model `toJson()` |
| `EnvironmentConfig.apiBaseUrl` | `core.config` | Per-environment API base URL, read via `config.getApiBaseUrl()` |
| `FrameworkConstants.GROUP_API` (`"api"`) | `constants` | TestNG group for API tests |

### Libraries actually used

- **REST-Assured 5.5.0** — the HTTP client and fluent assertion DSL. `get/post/…`
  return `io.restassured.response.Response`.
- **Hamcrest 2.2** — matchers (`equalTo`, `notNullValue`) for the fluent
  `response.then().body(...)` style. Comes transitively with REST-Assured.
- **AssertJ 3.26** — `assertThat(...)` for the extract-then-assert style.
- **Jackson** — request bodies (a `Map` or POJO) are serialised to JSON; responses
  are parsed via `response.jsonPath()` or `response.as(Type.class)`.
- **Log4j2** — `RestAPIExecutor` logs each call (`GET /posts/1`).

### The one thing to know before you start

`RestAPIExecutor.get/post/put/delete` take the endpoint, an optional body, and a
**headers map** as separate arguments. The `ApiRequest` / `ApiResponse` models
exist as generic data holders but are **not** consumed by the executor — don't
expect to pass an `ApiRequest` into `post()`. Use a `Map<String,Object>` or a
POJO for the body.

---

## 2. How `restClient` gets its base URL

`BaseAPITest` wires everything in a `@BeforeClass`, reading the **active
application's** `apiBaseUrl`:

```java
public abstract class BaseAPITest extends BaseTest {
    protected RestAPIExecutor restClient;

    @BeforeClass(alwaysRun = true)
    public void initApi() {
        String apiBaseUrl = config.getApiBaseUrl();      // from applications/<app>/application.yml
        if (apiBaseUrl != null && !apiBaseUrl.isBlank()) {
            RestAssured.baseURI = apiBaseUrl;
        }
        restClient = new RestAPIExecutor(apiBaseUrl);
        log.info("API base URL: {}", apiBaseUrl);
    }
}
```

So the API target is **configuration, not code**. You select it with
`-Dapplication=...`, exactly like the browser target. Confirmed at runtime:

```
Configuration initialised: application='jsonplaceholder', environment='prod', ...
API base URL: https://jsonplaceholder.typicode.com
```

---

## 3. A complete worked example (JSONPlaceholder)

### Step 1 — Describe the API target as an application

No pages, just an environment with an `apiBaseUrl`. This is the whole file,
`src/main/resources/applications/jsonplaceholder/application.yml`:

```yaml
name: jsonplaceholder
environments:
  prod:
    baseUrl: "https://jsonplaceholder.typicode.com"
    apiBaseUrl: "https://jsonplaceholder.typicode.com"
pages: {}
testData:
  knownPostId: 1
  knownUserId: 1
```

### Step 2 — Write the test (extend `BaseAPITest`, use `restClient`)

From `src/test/java/com/automation/framework/tests/api/JsonPlaceholderApiTest.java`.
Both assertion styles are shown so you can pick per test.

```java
public class JsonPlaceholderApiTest extends BaseAPITest {

    private static final Map<String, String> NO_HEADERS = Map.of();

    @Test(groups = FrameworkConstants.GROUP_API)
    public void getPostById() {
        Response response = restClient.get("/posts/1", NO_HEADERS);

        // Style 1 — fluent REST-Assured + Hamcrest
        response.then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("title", notNullValue());
    }

    @Test(groups = FrameworkConstants.GROUP_API)
    public void getAllPosts() {
        Response response = restClient.get("/posts", NO_HEADERS);

        // Style 2 — extract, then assert with AssertJ
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("id")).hasSize(100);
    }

    @Test(groups = FrameworkConstants.GROUP_API)
    public void createPost() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", "framework demo");
        body.put("body", "posted by the automation framework");
        body.put("userId", 1);

        Response response = restClient.post("/posts", body, NO_HEADERS);

        response.then()
                .statusCode(201)
                .body("title", equalTo("framework demo"))
                .body("id", notNullValue());     // JSONPlaceholder assigns 101
    }
}
```

`updatePost` (PUT `/posts/1`) and `deletePost` (DELETE `/posts/1`) round out the
CRUD set in the same file.

### Step 3 — Run it

```bash
# whole api group, via the dedicated suite
mvn test -DsuiteXmlFile=testng-api.xml -Dapplication=jsonplaceholder

# a single method
mvn test -Dapplication=jsonplaceholder -Dtest=JsonPlaceholderApiTest#createPost
```

> **Why `-Dapplication=jsonplaceholder`?** Without it the active application is
> `gyansathi`, whose `apiBaseUrl` is `https://www.gyansathi.com/api`, and the
> `/posts/*` calls would 404. The application selects the API target.

### What the calls actually exchange

`GET /posts/1` →

```json
{
  "userId": 1,
  "id": 1,
  "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
  "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum..."
}
```

`POST /posts` with `{"title":"framework demo","body":"...","userId":1}` → **201** →

```json
{
  "title": "framework demo",
  "body": "posted by the automation framework",
  "userId": 1,
  "id": 101
}
```

(JSONPlaceholder is a mock: it validates and echoes writes but does not persist
them, so the created `id` is always 101 and a later GET won't find it. Good for
demonstrating mechanics, not stateful workflows.)

---

## 4. Sending headers (httpbin example)

The last argument to every method is a headers map — this is where auth tokens,
API keys, or a correlation id go. [httpbin.org](https://httpbin.org) echoes what
it received, which makes it easy to prove headers are sent:

```java
Map<String, String> headers = Map.of(
        "Authorization", "Bearer demo-token-123",
        "X-Request-Id", "abc-001");

Response response = new RestAPIExecutor("https://httpbin.org")
        .get("/headers", headers);

response.then()
        .statusCode(200)
        .body("headers.Authorization", equalTo("Bearer demo-token-123"));
```

For headers sent on **every** request (e.g. a shared token), set them once:

```java
restClient.withDefaultHeaders(Map.of("Authorization", "Bearer " + token));
```

Per-call headers are merged on top of the defaults.

---

## 5. Two ways to get a `RestAPIExecutor`

| | Config-driven (via `BaseAPITest`) | Direct (`new RestAPIExecutor(url)`) |
|--|--|--|
| Base URL | from `apiBaseUrl` of the active app | passed explicitly |
| Best for | your app-under-test's own API | a third-party/external API in the same test |
| Selected by | `-Dapplication=...` | the string in code |

Both are legitimate. The httpbin snippet above uses the direct form precisely
because httpbin is not the application under test.

---

## 6. Typed request/response bodies (optional)

For readability you can bind bodies to POJOs instead of maps. A model extending
`BaseAPIModel` also gets `toJson()`:

```java
public class Post extends BaseAPIModel {
    public Integer userId;
    public Integer id;
    public String  title;
    public String  body;
}

// request: pass the POJO straight to post(); Jackson serialises it
Response r = restClient.post("/posts", new Post(/* ... */), Map.of());

// response: deserialise the body
Post created = r.as(Post.class);
assertThat(created.id).isNotNull();
```

---

## 7. SOAP, briefly

For SOAP services, extend `BaseSoapAPITest` and hand `soapExecutor.send(...)` a
full envelope — the executor is a plain JDK `HttpClient` POST, so it works
against any SOAP endpoint without WSDL bindings:

```java
public class CountryInfoSoapTest extends BaseSoapAPITest {
    @Test(groups = FrameworkConstants.GROUP_API)
    public void capitalCity() {
        String envelope = """
            <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"
                           xmlns:web="http://www.oorsprong.org/websamples.countryinfo">
              <soap:Body><web:CapitalCity><web:sCountryISOCode>IN</web:sCountryISOCode></web:CapitalCity></soap:Body>
            </soap:Envelope>""";

        String response = soapExecutor.send(
                "https://www.oorsprong.org/websamples.countryinfo/CountryInfoService.wso",
                "",              // SOAPAction (empty for SOAP 1.2)
                envelope);

        assertThat(response).contains("New Delhi");
    }
}
```

`send` returns the raw XML; assert on it with string/XML matchers. (This snippet
illustrates the shape — unlike the REST example it was not run here, since public
SOAP endpoints come and go.)

---

## 8. Files in this example

| File | Purpose |
|------|---------|
| `applications/jsonplaceholder/application.yml` | The API target as an application |
| `tests/api/JsonPlaceholderApiTest.java` | The 5 REST tests (CRUD) |
| `testng-api.xml` | Suite that runs the `api` group |
| `core/base/BaseAPITest.java` | Wires `restClient` from config *(existing)* |
| `api/executors/RestAPIExecutor.java` | The HTTP wrapper *(existing)* |

## 9. Notes / gotchas

- **Pick the application**, or you hit the wrong host. The API base URL is
  `apiBaseUrl`, distinct from the UI `baseUrl`.
- **`ApiRequest`/`ApiResponse` are not plumbed into `RestAPIExecutor`.** They are
  generic holders; the executor takes `(endpoint, body, headers)`.
- **`GROUP_API` isn't in the default `testng.xml`.** Use `testng-api.xml` (or
  `-Dtest=...`) so API tests don't run in the UI smoke suite.
- **JSONPlaceholder doesn't persist writes.** Use it for request/response
  mechanics, not for create-then-read scenarios.
- **Framework logging is off unless enabled.** To see `RestAPIExecutor`'s
  `GET/POST` lines, add `-DargLine="-Dlog4j2.configurationFile=config/log4j2.xml"`
  (`log4j2.xml` isn't at the classpath root, so it isn't auto-loaded).
