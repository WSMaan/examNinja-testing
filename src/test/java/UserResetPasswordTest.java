import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserResetPasswordTest {

    private final String testEmail = "foo@example.com";
    private final String testPassword = "fooWoo@123";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";  // Replace with the actual base URI
        RestAssured.port = 8081;                   // Replace with the actual port if needed
    }

    @BeforeEach
    public void registerUserIfNotExists() {
        // Attempt to register the user
        Response registrationResponse = given()
                .contentType(ContentType.JSON)
                .body("{ \"firstName\": \"abc\", \"lastName\": \"def\", \"email\": \"" + testEmail + "\", \"password\": \"" + testPassword + "\" }")
                .when()
                .post("/api/users/register");

    }

    // Test Case 1: Reset Password with Unregistered Email
    @Test
    public void testResetPasswordWithUnregisteredEmail() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"unregistered@example.com\" }")
                .when()
                .post("/api/users/reset-password")
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", equalTo("This Email is not registered"));
    }

    // Test Case 2: Reset Password with Registered Email
    @Test
    public void testResetPasswordWithRegisteredEmail() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"" + testEmail + "\" }")
                .when()
                .post("/api/users/reset-password")
                .then()
                .statusCode(200)
                .log().ifError()
                .body("status", equalTo("success"))
                .body("message", containsString("Password reset successfully"))
                .body("message", containsString("New Password is:"));
    }

    // Test Case 3: Reset Password with Invalid Email Format
    @Test
    public void testResetPasswordWithInvalidEmailFormat() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"invalidemailformat\" }")
                .when()
                .post("/api/users/reset-password")
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", equalTo("Validation failed"))
                .body("error.email", equalTo("Invalid email format"));
    }

    // Test Case 4: Reset Password with Empty Email Field
    @Test
    public void testResetPasswordWithEmptyEmailField() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"\" }")
                .when()
                .post("/api/users/reset-password")
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", equalTo("Validation failed"))
                .body("error.email", equalTo("Email field can't be empty"));
    }

    // Test Case 5: Reset Password with Missing Email Field
    @Test
    public void testResetPasswordWithMissingEmailField() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/api/users/reset-password")
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", equalTo("Validation failed"))
                .body("error.email", equalTo("Email is mandatory"));
    }
}
