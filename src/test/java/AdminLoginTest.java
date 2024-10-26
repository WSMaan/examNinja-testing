import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AdminLoginTest {

    @BeforeClass
    public static void setup() {
        // Base URI
        RestAssured.baseURI = "http://localhost:9000"; // Update with your actual base URL
    }
    @Test
    public void testLoginSuccess() {
        // Request payload
        String requestBody = "{ \"username\": \"adminUserName\", \"password\": \"adminPassword\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)// Logs request details
                .when()
                .post("/api/admin/login")
                .then()// Logs response details
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", equalTo("Admin Logged in Successfully!"));
    }
    @Test
    public void testUsernameNotFound() {
        // Request payload
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
    public void testInvalidPassword() {
        // Request payload
        String requestBody = "{ \"username\": \"adminUserName\", \"password\": \"admin123\" }";

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
    public void testUserMandatory() {
        // Request payload
        String requestBody = "{ \"username\": \"\", \"password\": \"adminPassword\" }";

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
    public void testPasswordMandatory() {
        // Request payload
        String requestBody = "{ \"username\": \"adminUserName\", \"password\": \"\" }";

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
    public void testLoginInternalServerError() {
        // Request payload
        String requestBody = "{ \"username\": \"adminUserName\", \"password\": \"adminPassword\" }";

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
    public void testUnauthorizedUser() {
        // Request payload
        String requestBody = "{ \"username\": \"admin12\", \"password\": \"admin12\" }";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/admin/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("User not found"));
    }
}
