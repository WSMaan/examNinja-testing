import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginApiTest {

    private String authToken;  // Token will be fetched and stored here

    @BeforeClass
    public static void setup() {
        // Base URI
        RestAssured.baseURI = "http://localhost:8081"; // Update with your actual base URL
    }
    // This method simulates a login and retrieves the JWT token before each test
    @BeforeEach
    public void fetchAuthToken(){
        // Simulate login or fetch the token from the token generation endpoint
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"foo@example.com\", \"password\": \"fooWoo@123\" }")  // Replace with actual credentials
                .when()
                .post("/api/users/login")  // Replace with the actual login or token generation endpoint
                .then()
                .statusCode(200)
                .extract().response();

        /*
            Bearer Token authentication is a common way to authenticate
            API requests where the token (JWT in this case) is sent in the Authorization header.
         */
        // Extract JWT token from the response
        authToken = "Bearer " + response.jsonPath().getString("token");  // Adjust the field name if necessary
    }


    @Test
    public void loginSuccessTest() {
        // Request payload
        String requestBody = "{ \"email\": \"foo@example.com\", \"password\": \"fooWoo@123\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)// Logs request details
                .when()
                .post("/api/users/login")
                .then()// Logs response details
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", equalTo("User Logged in Successfully!"));
    }


    @Test
    public void loginFailedDueToPasswordMismatchTest() {
        String requestBody = "{ \"email\": \"foo@example.com\", \"password\": \"fooWoo1234\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"))
                .body("error.password", equalTo("Password must contain at least 1 special character."));
    }

    @Test
    public void loginFailedDueToInvalidEmailFormatTest() {
        String requestBody = "{ \"email\": \"fooexample.com\", \"password\": \"fooWoo123\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"))
                .body("error.email", equalTo("Email should be valid"));
    }

    @Test
    public void loginFailedUserNotFoundTest() {
        String requestBody = "{ \"email\": \"foo1@example.com\", \"password\": \"fooWoo@123\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("User not found"));
    }

    @Test
    public void loginFailedEmailNullTest() {
        String requestBody = "{ \"email\": \"\", \"password\": \"fooWoo@123\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"))
                .body("error.email", equalTo("Email is mandatory"));
    }

    @Test
    public void loginFailedPasswordNullTest() {
        String requestBody = "{ \"email\": \"foo@example.com\", \"password\": \"*****\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"))
                .body("error.password", equalTo("Password must be between 8 and 15 characters."));
    }
}

