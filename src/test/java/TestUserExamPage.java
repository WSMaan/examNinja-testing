//import io.qameta.allure.Description;
//import io.qameta.allure.Step;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import java.time.Duration;
//import java.util.List;
//
//public class TestUserExamPage {
//
//    private WebDriver driver;
//    private final String username = "ab@gmail.com";
//    private final String password = "Ab@12345";
//
//    @BeforeClass
//    @Description("Set up the ChromeDriver, perform login, and navigate to the Certifications page")
//    public void setUp() {
//        driver = new ChromeDriver();
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//        driver.manage().window().maximize();
//        driver.get("http://localhost:3000/");
//        login(username, password);
//        driver.get("http://localhost:3000/Quest");
//    }
//
//    @Step("Perform login with username and password")
//    private void login(String username, String password) {
//        WebElement usernameField = driver.findElement(By.id(":r0:"));
//        usernameField.sendKeys(username);
//
//        WebElement passwordField = driver.findElement(By.id("pwd"));
//        passwordField.sendKeys(password);
//
//        WebElement loginButton = driver.findElement(By.xpath("//button[normalize-space()='Login']"));
//        loginButton.click();
//
//        Assert.assertTrue(driver.findElement(By.xpath("//button[normalize-space()='Login']")).isDisplayed(), "Login failed!");
//    }
//
//    @Test
//    @Description("Verify the title and header on the Certifications page")
//    @Step("Check if the page title and header match expected values")
//    public void testTitleAndHeaderDisplay() {
//        String expectedTitle = "Vite + React";
//        Assert.assertEquals(driver.getTitle(), expectedTitle, "Page title does not match!");
//
//        WebElement header = driver.findElement(By.xpath("//button[normalize-space()='Certifications']"));
//        Assert.assertEquals(header.getText(), "Certifications", "Header text does not match!");
//    }
//
//    @Test
//    @Description("Verify that expired test information is displayed correctly")
//    @Step("Check if the expired test label text matches expected information")
//    public void testInformationDisplay() {
//        WebElement expiredLabel = driver.findElement(By.cssSelector("h6[class='MuiTypography-root MuiTypography-h6 css-cpax1e']"));
//        String expectedText = "EXPIRED - JA+ I V8 For OCA-JP-I SE8 (1Z0-808)";
//        Assert.assertEquals(expiredLabel.getText(), expectedText, "Expired test information does not match!");
//    }
//
//    @Test
//    @Description("Verify that each test link is clickable and navigates to the correct URL")
//    @Step("Click each test link and verify the URL")
//    public void verifyTestLinksClickable() {
//        List<WebElement> testLinks = driver.findElements(By.xpath("//body[1]/div[1]/div[1]/div[2]/div[2]/table[1]/tbody[1]/tr[1]/td[2]"));
//        for (WebElement link : testLinks) {
//            link.click();
//            Assert.assertTrue(driver.getCurrentUrl().contains("/test/{id}"), "Test page URL is incorrect");
//            driver.navigate().back();
//        }
//    }
//
//    @AfterClass
//    @Description("Close the browser after tests are completed")
//    public void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//}
