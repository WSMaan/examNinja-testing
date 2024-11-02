import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserChangePasswordTest {

    private final String testEmail = "abc.def@example.com"; // Registered email
    private final String testPassword = "fooloo@123";           // Current password
    private final String validNewPassword = "asvbf@123b";      // New password for change
    private final String invalidEmail = "aalice1Green@mail.com"; // Not matching email in DB
    private final String incorrectOldPassword = "12dfg@";      // Incorrect old password
    private final String emptyOldPassword = "";                 // Old password empty
    private final String emptyNewPassword = "";                 // New password empty
    private final String invalidNewPassword = "abcasdsdsdd";   // New password not matching validation

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

    // Test Case 1: Change Password Success
    @Test
    public void testChangePasswordSuccess() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"" + testEmail + "\", \"oldPassword\": \"" + testPassword + "\", \"newPassword\": \"" + validNewPassword + "\" }")
                .when()
                .put("/api/users/change-password")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", equalTo("Password changed successfully"));
    }

    // Test Case 2: Not Matching Email in DB
    @Test
    public void testChangePasswordUserNotFound() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"" + invalidEmail + "\", \"oldPassword\": \"" + testPassword + "\", \"newPassword\": \"" + validNewPassword + "\" }")
                .when()
                .put("/api/users/change-password")
                .then()
                .statusCode(404)
                .body("message", equalTo("User not found with the provided email."));
    }

    // Test Case 3: Incorrect Old Password
    @Test
    public void testChangePasswordIncorrectOldPassword() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"" + testEmail + "\", \"oldPassword\": \"" + incorrectOldPassword + "\", \"newPassword\": \"" + validNewPassword + "\" }")
                .when()
                .put("/api/users/change-password")
                .then()
                .statusCode(400)
                .body("message", equalTo("Old password is incorrect"));
    }

    // Test Case 4: Old Password Empty
    @Test
    public void testChangePasswordOldPasswordEmpty() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"" + testEmail + "\", \"oldPassword\": \"" + emptyOldPassword + "\", \"newPassword\": \"" + validNewPassword + "\" }")
                .when()
                .put("/api/users/change-password")
                .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"))
                .body("error.oldPassword", equalTo("Old password is required"));
    }


}
