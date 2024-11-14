import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginApiTest {

    private String authToken;

    @BeforeClass
    @Description("Setup base URI for RestAssured")
    public void setup() {
        RestAssured.baseURI = "http://localhost:8084";
    }

    @BeforeMethod
    @Step("Fetch AUTH TOKEN")
    public void fetchAuthToken() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"foo@example.com\", \"password\": \"fooWoo@123\" }")
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(200)
                .extract().response();

        authToken = "Bearer " + response.jsonPath().getString("token");
    }

    @Test
    @Description("Valid Login Success Test")
    @Step("Perform a successful login test")
    public void loginSuccessTest() {
        String requestBody = "{ \"email\": \"foo@example.com\", \"password\": \"fooWoo@123\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", equalTo("User Logged in Successfully!"));
    }

    @Test
    @Description("Login Failed Due to Password Mismatch Test")
    @Step("Attempt login with incorrect password")
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
    @Description("Login Failed Due to Invalid Email Format Test")
    @Step("Attempt login with invalid email format")
    public void loginFailedDueToInvalidEmailFormatTest() {
        String requestBody = "{ \"email\": \"foo@example.com\", \"password\": \"fooWoo123\" }";

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
    @Description("Login Failed Due to User Not Found Test")
    @Step("Attempt login with non-existent user")
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
    @Description("Login Failed Due to Email Null Test")
    @Step("Attempt login with null email")
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
    @Description("Login Failed Due to Password Null Test")
    @Step("Attempt login with null password")
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
