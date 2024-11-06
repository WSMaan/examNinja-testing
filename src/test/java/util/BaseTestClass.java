package util;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import java.util.ResourceBundle;

import static io.restassured.RestAssured.given;

public class BaseTestClass {

    public String authToken;  // Token will be fetched and stored here
    protected static String testEmail;
    protected static String testPassword;

    // Static variables for endpoints
    protected static String loginEndpoint;
    protected static String questionsEndpoint;
    protected static String saveAnswerEndpoint;

    @BeforeAll
    public static void setup() {
        // Load the properties file
        ResourceBundle rb = ResourceBundle.getBundle("application");

        RestAssured.baseURI = rb.getString("base.uri");  // Base URI from properties
        RestAssured.port = Integer.parseInt(rb.getString("base.port"));  // Port from properties

        // Load user credentials from properties
        testEmail = rb.getString("test.email");
        testPassword = rb.getString("test.password");

        // Load endpoints from properties
        loginEndpoint = rb.getString("login.endpoint");
        questionsEndpoint = rb.getString("questions.endpoint");
        saveAnswerEndpoint = rb.getString("save.answer.endpoint");
    }

    // This method simulates login and retrieves the JWT token before each test
    @BeforeEach
    public void fetchAuthToken() {
        String requestBody = "{ \"email\": \"" + testEmail + "\", \"password\": \"" + testPassword + "\" }";

        // Simulate login or fetch the token from the token generation endpoint
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)  // Replace with actual credentials
                .when()
                .post(loginEndpoint)  // Replace with the actual login or token generation endpoint
                .then()
                .statusCode(200)
                .extract().response();

        // Extract JWT token from the response
        authToken = "Bearer " + response.jsonPath().getString("token");  // Adjust the field name if necessary
    }
}
