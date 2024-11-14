import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AdminLoginTest {

    @BeforeClass
    @Description("Setup base URI for AdminLogin API")
    public static void setup() {
        RestAssured.baseURI = "http://localhost:9000"; // Update with your actual base URL
    }

    @Test
    @Description("Admin Login Success Test")
    @Step("Perform a successful admin login test")
    public void testLoginSuccess() {
        String requestBody = "{ \"username\": \"admin\", \"password\": \"admin\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/admin/login")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", equalTo("Admin Logged in Successfully!"));
    }

    @Test
    @Description("Admin Login Failed Due to Username Not Found Test")
    @Step("Attempt admin login with non-existent username")
    public void testUsernameNotFound() {
        String requestBody = "{ \"username\": \"admin123\", \"password\": \"admin\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/admin/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("User not found"));
    }

    @Test
    @Description("Admin Login Failed Due to Invalid Password Test")
    @Step("Attempt admin login with incorrect password")
    public void testInvalidPassword() {
        String requestBody = "{ \"username\": \"admin\", \"password\": \"admin123\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/admin/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Incorrect password"));
    }

    @Test
    @Description("Admin Login Failed Due to Empty Username Test")
    @Step("Attempt admin login with empty username")
    public void testUserMandatory() {
        String requestBody = "{ \"username\": \"\", \"password\": \"admin123\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/admin/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"));
    }

    @Test
    @Description("Admin Login Failed Due to Empty Password Test")
    @Step("Attempt admin login with empty password")
    public void testPasswordMandatory() {
        String requestBody = "{ \"username\": \"admin\", \"password\": \"\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/admin/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"));
    }

    @Test
    @Description("Admin Login Failed Due to Internal Server Error Test")
    @Step("Attempt admin login causing an internal server error")
    public void testLoginInternalServerError() {
        String requestBody = "{ \"username\": \"admin\", \"password\": \"admin\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/admin/logi")
                .then()
                .statusCode(500)
                .body("message", equalTo("No static resource api/admin/logi."));
    }

    @Test
    @Description("Admin Login Failed Due to Unauthorized User Test")
    @Step("Attempt admin login with unauthorized credentials")
    public void testUnauthorizedUser() {
        String requestBody = "{ \"username\": \"admin12\", \"password\": \"admin12\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/admin/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("Invalid credentials or not authorized as admin"));
    }
}
