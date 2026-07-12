package com.automation.framework.tests.api;

import com.automation.framework.constants.FrameworkConstants;
import com.automation.framework.core.base.BaseAPITest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * API tests against the public dummy API https://jsonplaceholder.typicode.com.
 *
 * <p>{@code restClient} (a {@link com.automation.framework.api.executors.RestAPIExecutor})
 * and its base URI are wired by {@link BaseAPITest} from the active application's
 * {@code apiBaseUrl}. Run this against the bundled demo application:
 *
 * <pre>
 *   mvn test -Dapplication=jsonplaceholder -Dtest=JsonPlaceholderApiTest
 * </pre>
 *
 * The methods below show the two assertion styles the framework supports: the
 * fluent REST-Assured {@code response.then()...} and plain extraction into
 * AssertJ assertions.
 */
public class JsonPlaceholderApiTest extends BaseAPITest {

    private static final Map<String, String> NO_HEADERS = Map.of();

    @Test(groups = FrameworkConstants.GROUP_API,
            description = "GET a known post returns 200 and the expected shape")
    public void getPostById() {
        Response response = restClient.get("/posts/1", NO_HEADERS);

        // Style 1: fluent REST-Assured validation with Hamcrest matchers.
        response.then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("title", notNullValue());
    }

    @Test(groups = FrameworkConstants.GROUP_API,
            description = "GET all posts returns a non-empty collection")
    public void getAllPosts() {
        Response response = restClient.get("/posts", NO_HEADERS);

        // Style 2: extract, then assert with AssertJ.
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("id")).hasSize(100);
    }

    @Test(groups = FrameworkConstants.GROUP_API,
            description = "POST creates a resource and echoes the body with a new id")
    public void createPost() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", "framework demo");
        body.put("body", "posted by the automation framework");
        body.put("userId", 1);

        Response response = restClient.post("/posts", body, NO_HEADERS);

        response.then()
                .statusCode(201)
                .body("title", equalTo("framework demo"))
                .body("userId", equalTo(1))
                .body("id", notNullValue());     // JSONPlaceholder assigns 101
    }

    @Test(groups = FrameworkConstants.GROUP_API,
            description = "PUT updates an existing resource")
    public void updatePost() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", 1);
        body.put("title", "updated title");
        body.put("body", "updated body");
        body.put("userId", 1);

        Response response = restClient.put("/posts/1", body, NO_HEADERS);

        response.then().statusCode(200).body("title", equalTo("updated title"));
    }

    @Test(groups = FrameworkConstants.GROUP_API,
            description = "DELETE removes a resource")
    public void deletePost() {
        Response response = restClient.delete("/posts/1", NO_HEADERS);
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
