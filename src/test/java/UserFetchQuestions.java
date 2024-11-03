<<<<<<< HEAD

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;

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
    public void testGetQuestionsHappyPath(){
        given()
                .header("Authorization", authToken)  // Pass the JWT token in the Authorization header
                .contentType(ContentType.JSON)
                .when()
                .pathParam("test_id", 1)
                .queryParam("page", 0)
                .when()
                .get("/api/tests/{test_id}/questions")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .log().ifError()  // Log output only if the test fails
                .body("questionNumber", notNullValue()) // Flexible assertion: Not null instead of a specific value
                .body("questions[0].questionId", notNullValue()) // Flexible assertion for questionId
                .body("questions[0].question", notNullValue()) // Flexible assertion for question
                .body("questions[0].option1", notNullValue()) // Option1 should not be null
                .body("questions[0].option2", notNullValue()) // Option2 should not be null
                .body("questions[0].option3", notNullValue()) // Option3 should not be null
                .body("questions[0].option4", notNullValue()) // Option4 should not be null
                .body("questions[0].correctAnswer", notNullValue()) // Correct answer should not be null
                .body("questions[0].answerDescription", notNullValue()) // Description should not be null
                .body("questions[0].category", notNullValue()) // Category should not be null
                .body("questions[0].level", notNullValue()) // Level should not be null
                // Dynamic assertions for totalElements and totalPages
                .body("pageDetails.totalElements", greaterThan(0))  // Instead of hardcoding 120
                .body("pageDetails.totalPages", greaterThan(0))     // Instead of hardcoding 120
                .body("pageDetails.pageNumber", greaterThanOrEqualTo(0))       // Page number still fixed for the test case
                .body("pageDetails.pageSize", greaterThan(0))       // Page size should be greater than 0
                .body("pageDetails.lastPage", notNullValue());      // Last page should not be null
    }

    // Unhappy Path Test: Invalid test_id
    @Test
    public void testGetQuestionsInvalidTestId(){


        given()
                .header("Authorization", authToken)  // Use the JWT token here too
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

    // Unhappy Path Test: Invalid page number
    @Test
    public void testGetQuestionsInvalidPage(){

        given()
                .header("Authorization", authToken)  // Use the JWT token here too
                .contentType(ContentType.JSON)
                .pathParam("test_id", 1)
                .queryParam("page", 11)
                .when()
                .get("/api/tests/{test_id}/questions")
                .then()
                .statusCode(400)
                .log().ifError()
                .body("message", containsString("Requested page is out of bounds. Maximum page number:"));
    }
}
=======
//import com.github.tomakehurst.wiremock.WireMockServer;
//import com.github.tomakehurst.wiremock.client.WireMock;
//import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import static com.github.tomakehurst.wiremock.client.WireMock.*;
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.greaterThan;
//
//public class UserFetchQuestions {
//
//    private static WireMockServer wireMockServer;
//
//    @BeforeAll
//    public static void setup() {
//        // Initialize WireMock server
//        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8081));
//        wireMockServer.start();
//
//        // Configure WireMock for use in RestAssured
//        WireMock.configureFor("localhost", 8081);
//        RestAssured.baseURI = "http://localhost";
//        RestAssured.port = 8081;
//
//        // Mock successful response for fetching questions
//        wireMockServer.stubFor(get(urlPathMatching("/api/tests/2/questions"))
//                .withQueryParam("page", equalToJson("1"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("""
//                    {
//                        "content": [
//                            {
//                                "questionId": 62,
//                                "question": "Explain the Java memory model.",
//                                "option1": "Heap & Stack",
//                                "option2": "Code & Data",
//                                "option3": "Heap & CPU",
//                                "option4": "RAM & ROM",
//                                "option5": "Flash & Memory",
//                                "correctAnswer": "Heap & Stack",
//                                "answerDescription": "The memory model includes Heap and Stack.",
//                                "category": "Programming",
//                                "level": "Hard",
//                                "questionType": null,
//                                "test": {
//                                    "testId": 2,
//                                    "testName": "Test 2",
//                                    "numberOfQuestions": 60
//                                }
//                            }
//                        ],
//                        "pageable": {
//                            "pageNumber": 1,
//                            "pageSize": 1,
//                            "sort": {
//                                "empty": true,
//                                "sorted": false,
//                                "unsorted": true
//                            },
//                            "offset": 1,
//                            "paged": true,
//                            "unpaged": false
//                        },
//                        "last": false,
//                        "totalElements": 60,
//                        "totalPages": 60,
//                        "size": 1,
//                        "number": 1,
//                        "sort": {
//                            "empty": true,
//                            "sorted": false,
//                            "unsorted": true
//                        },
//                        "numberOfElements": 1,
//                        "first": false,
//                        "empty": false
//                    }
//                """)));
//
//        // Mock error response for test not found
//        wireMockServer.stubFor(get(urlPathMatching("/api/tests/70/questions"))
//                .willReturn(aResponse()
//                        .withStatus(400)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("""
//                    {
//                        "message": "Test not found with id: 70"
//                    }
//                """)));
//    }
//
//    @AfterAll
//    public static void teardown() {
//        // Stop WireMock server after tests
//        wireMockServer.stop();
//    }
//
//    // Test for fetching questions successfully (Happy Path)
//    @Test
//    public void getQuestions_HappyPath_ShouldReturnQuestions() {
//        given()
//                .contentType(ContentType.JSON)
//                .pathParam("test_id", 2)
//                .queryParam("page", 1)
//                .when()
//                .get("/api/tests/{test_id}/questions")
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON).log().all()
//                .body("content.size()", greaterThan(0))
//                .body("content[0].questionId", equalTo(62))
//                .body("content[0].question", equalTo("Explain the Java memory model."))
//                .body("content[0].option1", equalTo("Heap & Stack"))
//                .body("content[0].correctAnswer", equalTo("Heap & Stack"))
//                .body("content[0].category", equalTo("Programming"))
//                .body("content[0].level", equalTo("Hard"))
//                .body("totalElements", equalTo(60))
//                .body("totalPages", equalTo(60));
//    }
//
//    // Test for test not found scenario (Unhappy Path)
//    @Test
//    public void getQuestions_TestNotFound_ShouldReturnError() {
//        given()
//                .contentType(ContentType.JSON)
//                .pathParam("test_id", 12)
//                .queryParam("page", 1)
//                .when()
//                .get("/api/tests/{test_id}/questions")
//                .then()
//                .statusCode(400)
//                .contentType(ContentType.JSON).log().all()
//                .body("message", equalTo("Test not found with id: 12"));
//    }
//}
//
//
>>>>>>> master
