import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.extension.Extension;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PasswordUpdateTest {

    private WireMockServer wireMockServer;

    @BeforeClass
    public void setup() {
        // Start WireMock server
        wireMockServer = new WireMockServer(
                options()
                        .port(8080)

        );
        wireMockServer.start();

        // Configure WireMock to return specific responses for different scenarios
        configureFor("localhost", 8080);

        // Unhappy path stubs
        // Case 1: Email does not exist
        stubFor(put(urlEqualTo("/update-password"))
                .withRequestBody(equalToJson("{\"email\":\"nonexistent@example.com\",\"password\":\"newPassword123\"}", true, true))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)
                        .withBody("{ \"status\": \"error\", \"message\": \"Email not found\" }")));

        // Case 2: Invalid password format
        stubFor(put(urlEqualTo("/update-password"))
                .withRequestBody(equalToJson("{\"email\":\"user@example.com\",\"password\":\"invalidPassword\"}", true, true))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("{ \"status\": \"error\", \"message\": \"Invalid password format\" }")));

        // Case 3: Internal server error
        stubFor(put(urlEqualTo("/update-password"))
                .withRequestBody(equalToJson("{\"email\":\"user1@example.com\",\"password\":\"newPassword1234\"}", true, true))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(500)
                        .withBody("{ \"status\": \"error\", \"message\": \"Internal server error\" }")));

        // Case 4: Invalid email format
        stubFor(put(urlEqualTo("/update-password"))
                .withRequestBody(equalToJson("{\"email\":\"invalid-email\",\"password\":\"newPassword123\"}", true, true))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("{ \"status\": \"error\", \"message\": \"Invalid email format\" }")));
// Happy path
        stubFor(put(urlEqualTo("/update-password"))
                .withRequestBody(equalToJson("{\"email\":\"user@example.com\",\"password\":\"newPassword123\"}", true, true))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{ \"status\": \"success\", \"message\": \"Password updated successfully\" }")));


        // Set the Rest Assured base URI to the WireMock server
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void testUpdatePasswordSuccess() {
        // Define request payload
        String requestPayload = "{\n" +
                "    \"email\": \"user@example.com\",\n" +
                "    \"password\": \"newPassword123\"\n" +
                "}";
        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/update-password")
                .then()
                .extract().response();

        // Assert status code
        Assert.assertEquals(response.getStatusCode(), 200, "Status code is not 200");

        // Extract and validate the response JSON
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("status"), "Response does not contain status");
        Assert.assertTrue(responseBody.contains("message"), "Response does not contain message");

        // Additional checks based on the expected response format
        String status = response.jsonPath().getString("status");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(status, "success", "Status is not success");
        Assert.assertEquals(message, "Password updated successfully", "Message is incorrect");
    }

    @Test
    public void testUpdatePasswordEmailNotFound() {
        // Define request payload with a non-existent email
        String requestPayload = "{ \"email\": \"nonexistent@example.com\", \"password\": \"newPassword123\" }";

        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/update-password")
                .then()
                .extract().response();

        // Assert status code
        Assert.assertEquals(response.getStatusCode(), 404, "Status code is not 404");

        // Extract and validate the response JSON
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("status"), "Response does not contain status");
        Assert.assertTrue(responseBody.contains("message"), "Response does not contain message");

        // Additional checks based on the expected response format
        String status = response.jsonPath().getString("status");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(status, "error", "Status is not error");
        Assert.assertEquals(message, "Email not found", "Message is incorrect");
    }

    @Test
    public void testUpdatePasswordInvalidPasswordFormat() {
        // Define request payload with an invalid password format
        String requestPayload = "{ \"email\": \"user@example.com\", \"password\": \"invalidPassword\" }";

        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/update-password")
                .then()
                .extract().response();
        // Assert status code
        Assert.assertEquals(response.getStatusCode(), 400, "Status code is not 400");

        // Extract and validate the response JSON
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("status"), "Response does not contain status");
        Assert.assertTrue(responseBody.contains("message"), "Response does not contain message");

        // Additional checks based on the expected response format
        String status = response.jsonPath().getString("status");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(status, "error", "Status is not error");
        Assert.assertEquals(message, "Invalid password format", "Message is incorrect");
    }

    @Test
    public void testUpdatePasswordInternalServerError() {
        // Define request payload
        String requestPayload = "{ \"email\": \"user1@example.com\", \"password\": \"newPassword1234\" }";

        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/update-password")
                .then()
                .extract().response();
        // Assert status code
        Assert.assertEquals(response.getStatusCode(), 500, "Status code is not 500");

        // Extract and validate the response JSON
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("status"), "Response does not contain status");
        Assert.assertTrue(responseBody.contains("message"), "Response does not contain message");

        // Additional checks based on the expected response format
        String status = response.jsonPath().getString("status");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(status, "error", "Status is not error");
        Assert.assertEquals(message, "Internal server error", "Message is incorrect");
    }

    @Test
    public void testUpdatePasswordInvalidEmailFormat() {
        // Define request payload with an invalid email format
        String requestPayload = "{ \"email\": \"invalid-email\", \"password\": \"newPassword123\" }";

        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/update-password")
                .then()
                .extract().response();

        // Assert status code
        Assert.assertEquals(response.getStatusCode(), 400, "Status code is not 400");

        // Extract and validate the response JSON
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("status"), "Response does not contain status");
        Assert.assertTrue(responseBody.contains("message"), "Response does not contain message");

        // Additional checks based on the expected response format
        String status = response.jsonPath().getString("status");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(status, "error", "Status is not error");
        Assert.assertEquals(message, "Invalid email format", "Message is incorrect");
    }

    @AfterClass
    public void tearDown() {
        wireMockServer.stop();
    }
}
