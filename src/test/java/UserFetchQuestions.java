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
