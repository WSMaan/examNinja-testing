import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;

public class UserFetchQuestions {

    private String authToken;  // Token will be fetched and stored here

    @BeforeAll
    public static void setup(){
        RestAssured.baseURI = "http://localhost";  // Replace with the actual base URI
        RestAssured.port = 8081;                   // Replace with the actual port if needed
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


    // Happy Path Test: Valid test_id and page_number
    @Test
    public void testGetQuestionsHappyPath() {
        // Step 1: Fetch the question response and extract it
        Response questionResponse = given()
                .header("Authorization", authToken)
                .contentType(ContentType.JSON)
                .when()
                .pathParam("test_id", 3)
                .queryParam("page", 7)
                .get("/api/tests/{test_id}/questions")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .log().ifError()
                .extract().response();

        // Step 2: Extract option values dynamically from the response
        String option1 = questionResponse.jsonPath().getString("questions[0].option1");
        String option2 = questionResponse.jsonPath().getString("questions[0].option2");
        String option3 = questionResponse.jsonPath().getString("questions[0].option3");
        String option4 = questionResponse.jsonPath().getString("questions[0].option4");

        // Step 3: Perform assertions
        questionResponse.then()
                .body("testName", notNullValue())
                .body("questionNumber", matchesRegex("\\d+ of \\d+"))
                .body("questions[0].questionId", notNullValue())
                .body("questions[0].question", notNullValue())
                .body("questions[0].option1", notNullValue())
                .body("questions[0].option2", notNullValue())
                .body("questions[0].option3", notNullValue())
                .body("questions[0].option4", notNullValue())
                .body("questions[0].correctAnswer", notNullValue())
                .body("questions[0].answerDescription", notNullValue())
                .body("questions[0].category", notNullValue())
                .body("questions[0].level", notNullValue())
                .body("questions[0].questionType", notNullValue())
                .body("questions[0].testId", equalTo(3))
                // Updated selectedOption validation with dynamic options
                .body("questions[0].selectedOption", anyOf(
                        nullValue(),
                        equalTo(option1),
                        equalTo(option2),
                        equalTo(option3),
                        equalTo(option4)
                ));

        int testId = questionResponse.jsonPath().getInt("questions[0].testId");
        int questionId = questionResponse.jsonPath().getInt("questions[0].questionId");

        String[] options = {
                questionResponse.jsonPath().getString("questions[0].option1"),
                questionResponse.jsonPath().getString("questions[0].option2"),
                questionResponse.jsonPath().getString("questions[0].option3"),
                questionResponse.jsonPath().getString("questions[0].option4")
        };

        String currentSelectedOption = questionResponse.jsonPath().getString("questions[0].selectedOption");

        if (currentSelectedOption == null) {
            String selectedOption = options[new Random().nextInt(options.length)];

            given()
                    .header("Authorization", authToken)
                    .contentType(ContentType.JSON)
                    .body("{ \"questionId\": " + questionId + ", \"testId\": " + testId + ", \"selectedOption\": \"" + selectedOption + "\" }")
                    .when()
                    .post("/api/tests/save")
                    .then()
                    .statusCode(200)
                    .body("status", equalTo("success"))
                    .body("message", equalTo("Answer saved successfully!"));

            given()
                    .header("Authorization", authToken)
                    .contentType(ContentType.JSON)
                    .pathParam("test_id", testId)
                    .queryParam("page", 7)
                    .when()
                    .get("/api/tests/{test_id}/questions")
                    .then()
                    .statusCode(200)
                    .body("questions[0].questionId", equalTo(questionId))
                    .body("questions[0].selectedOption", equalTo(selectedOption));

        } else {
            given()
                    .header("Authorization", authToken)
                    .contentType(ContentType.JSON)
                    .pathParam("test_id", testId)
                    .queryParam("page", 7)
                    .when()
                    .get("/api/tests/{test_id}/questions")
                    .then()
                    .statusCode(200)
                    .body("questions[0].selectedOption", equalTo(currentSelectedOption));
        }
    }

    // Unhappy Path Test: Invalid test_id
    @Test
    public void testGetQuestionsInvalidTestId(){
        given()
                .header("Authorization", authToken)  // Use the JWT token here too
                .contentType(ContentType.JSON)
                .pathParam("test_id", 10) // Invalid test_id
                .queryParam("page", 0)
                .when()
                .get("/api/tests/{test_id}/questions")
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", containsString("No questions found for test with Test Id:"));
    }

    // Unhappy Path Test: Invalid page number
    @Test
    public void testGetQuestionsInvalidPage(){
        given()
                .header("Authorization", authToken)  // Use the JWT token here too
                .contentType(ContentType.JSON)
                .pathParam("test_id", 3)
                .queryParam("page", 66) // Page number out of bounds
                .when()
                .get("/api/tests/{test_id}/questions")
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", containsString("Requested page is out of bounds. Maximum page number:"));
    }

}