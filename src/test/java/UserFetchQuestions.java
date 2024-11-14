import io.qameta.allure.Description;
import io.qameta.allure.Step;
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
    @Description("Set up base URI and port for UserFetchQuestions API")
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @BeforeEach
    @Description("Fetch authentication token before each test")
    @Step("Simulate login to retrieve JWT token for authorization")
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
    @Description("Fetch questions for valid test ID and page number (Happy Path)")
    @Step("Fetch questions with valid test ID and verify response contents")
    public void testGetQuestionsHappyPath() {
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

        verifyQuestionResponse(questionResponse);
        int testId = questionResponse.jsonPath().getInt("questions[0].testId");
        int questionId = questionResponse.jsonPath().getInt("questions[0].questionId");

        if (questionResponse.jsonPath().getString("questions[0].selectedOption") == null) {
            saveAnswer(testId, questionId, questionResponse);
        } else {
            verifySelectedOption(testId, questionId, questionResponse);
        }
    }

    @Step("Verify response content of fetched questions")
    private void verifyQuestionResponse(Response questionResponse) {
        String option1 = questionResponse.jsonPath().getString("questions[0].option1");
        String option2 = questionResponse.jsonPath().getString("questions[0].option2");
        String option3 = questionResponse.jsonPath().getString("questions[0].option3");
        String option4 = questionResponse.jsonPath().getString("questions[0].option4");

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
                .body("questions[0].selectedOption", anyOf(nullValue(), equalTo(option1), equalTo(option2), equalTo(option3), equalTo(option4)));
    }

    @Step("Save answer for question if no option is selected")
    private void saveAnswer(int testId, int questionId, Response questionResponse) {
        String[] options = {
                questionResponse.jsonPath().getString("questions[0].option1"),
                questionResponse.jsonPath().getString("questions[0].option2"),
                questionResponse.jsonPath().getString("questions[0].option3"),
                questionResponse.jsonPath().getString("questions[0].option4")
        };

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
    }

    @Step("Verify previously selected option for question")
    private void verifySelectedOption(int testId, int questionId, Response questionResponse) {
        String currentSelectedOption = questionResponse.jsonPath().getString("questions[0].selectedOption");

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

    @Test
    @Description("Fetch questions with an invalid test ID (Unhappy Path)")
    @Step("Attempt to fetch questions with an invalid test ID and verify error message")
    public void testGetQuestionsInvalidTestId() {
        given()
                .header("Authorization", authToken)
                .contentType(ContentType.JSON)
                .pathParam("test_id", 10)
                .queryParam("page", 0)
                .when()
                .get("/api/tests/{test_id}/questions")
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", containsString("No questions found for test with Test Id:"));
    }

    @Test
    @Description("Fetch questions with an invalid page number (Unhappy Path)")
    @Step("Attempt to fetch questions with an invalid page number and verify error message")
    public void testGetQuestionsInvalidPage() {
        given()
                .header("Authorization", authToken)
                .contentType(ContentType.JSON)
                .pathParam("test_id", 3)
                .queryParam("page", 66)
                .when()
                .get("/api/tests/{test_id}/questions")
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", containsString("Requested page is out of bounds. Maximum page number:"));
    }
}
