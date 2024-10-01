import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class LoginApiTest {

    @BeforeClass
    public static void setup() {
        // Base URI
        RestAssured.baseURI = "http://localhost:8081"; // Update with your actual base URL
    }

    @Test
    public void loginSuccessTest() {
        // Request payload
        String requestBody = "{ \"email\": \"lilydavis@mail.com\", \"password\": \"Lily@Password1\" }";

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
        String requestBody = "{ \"email\": \"lilydavis@mail.com\", \"password\": \"Lid1password\" }";

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
        String requestBody = "{ \"email\": \"lilydavismail.com\", \"password\": \"Lily@Password1\" }";

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
        String requestBody = "{ \"email\": \"lilavis@mail.com\", \"password\": \"Lily@Password1\" }";

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
        String requestBody = "{ \"email\": \"\", \"password\": \"root#sde1\" }";

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
        String requestBody = "{ \"email\": \"lilydavis@mail.com\", \"password\": \"Lid1#\" }";

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

