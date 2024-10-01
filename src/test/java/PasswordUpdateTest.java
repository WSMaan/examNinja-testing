
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PasswordUpdateTest {
    @BeforeClass
    public void setup() {
        RestAssured.baseURI ="http://localhost:8081";
    }

    @Test
    public void testUpdatePasswordSuccess() {
        // Define request payload
        String requestPayload = "{\"email\": \"lilydavis@mail.com\",\n" +
                "    \"password\": \"Lily@Password1\"\n" +
                "}";
        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/api/users/change-password")
                .then()
                .extract().response();

        String message = response.jsonPath().getString("message");

        Assert.assertEquals(message, "Password changed successfully", "Message is incorrect");
    }

    @Test
    public void testUpdatePasswordEmailNotFound() {
        // Define request payload with a non-existent email
        String requestPayload = "{ \"email\": \"abcd@mail.com\", \"password\": \"root#sde1\" }";

        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/api/users/change-password")
                .then()
                .extract().response();
        // Additional checks based on the expected response format
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "User not found with the provided email.", "Message is incorrect");

    }

    @Test
    public void testUpdatePasswordInvalidPasswordFormat() {
        // Request payload with a password that is missing a special character
        String requestPayload = "{ \"email\": \"lilydavis@mail.com\", \"password\": \"LilyPassword1\" }";

        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/api/users/change-password")
                .then()
                .extract().response();
        // Assert status code
        Assert.assertEquals(response.getStatusCode(), 400, "Status code is not 400");

        // Extract the specific error message for password validation
        String errorMessage = response.jsonPath().getString("error.password");

        // Assert that the correct validation message is returned
        Assert.assertEquals(errorMessage, "Password must contain at least 1 special character.", "Message is incorrect");
    }

    @Test
    public void testUpdatePasswordnotvalidPasswordIsTooLongorShort() {
        // Correct Email format, password not valid(missing special character).
        String requestPayload = "{ \"email\": \"lilydavis@mail.com\", \"password\": \"Lily*1\" }";

        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/api/users/change-password")
                .then()
                .extract().response();

        String message = response.jsonPath().getString("error.password");
        Assert.assertEquals(message, "Password must be between 8 and 15 characters.", "Message is incorrect");
    }
    @Test
    public void testUpdatePasswordnotvalidPasswordFormat() {
        // Correct Email format, password not valid(missing special character).
        String requestPayload = "{ \"email\": \"lilydavis@mail.com\", \"password\": \"LilyPassword1\" }";

        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/api/users/change-password")
                .then()
                .extract().response();
        String message = response.jsonPath().getString("error.password");
        Assert.assertEquals(message, "Password must contain at least 1 special character.", "Message is incorrect");
    }

    @Test
    public void testUpdatePasswordInvalidEmailFormat() {
        // Define request payload with an invalid email format
        String requestPayload = "{ \"email\": \"lilydavismail.com\", \"password\": \"Lily@Password1\" }";

        // Send PUT request to update the password
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .when()
                .put("/api/users/change-password")
                .then()
                .extract().response();

        String errorMessage = response.jsonPath().getString("error.email");
        Assert.assertEquals(errorMessage, "Email should be valid", "Message is incorrect");
    }


}
