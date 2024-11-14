import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class UserResetPasswordTest {

    private final String testEmail = "foo@example.com";
    private final String testPassword = "fooWoo@123";

    @BeforeAll
    @Description("Set up base URI and port for UserResetPassword API")
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @BeforeEach
    @Description("Register test user if not already registered")
    @Step("Attempt to register user with test credentials")
    public void registerUserIfNotExists() {
        Response registrationResponse = given()
                .contentType(ContentType.JSON)
                .body("{ \"firstName\": \"abc\", \"lastName\": \"def\", \"email\": \"" + testEmail + "\", \"password\": \"" + testPassword + "\" }")
                .when()
                .post("/api/users/register");
    }

    @Test
    @Description("Reset Password with Unregistered Email Test")
    @Step("Attempt to reset password with an unregistered email")
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

    @Test
    @Description("Reset Password with Registered Email Test")
    @Step("Reset password for a registered email")
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

    @Test
    @Description("Reset Password with Invalid Email Format Test")
    @Step("Attempt to reset password with an invalid email format")
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

    @Test
    @Description("Reset Password with Empty Email Field Test")
    @Step("Attempt to reset password with an empty email field")
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
                .body("error.email", equalTo("Email is mandatory"));
    }

    @Test
    @Description("Reset Password with Missing Email Field Test")
    @Step("Attempt to reset password without providing an email field")
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
