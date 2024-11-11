import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class TestUserExamPage {

    private WebDriver driver;
    private final String username = "ab@gmail.com";
    private final String password = "Ab@12345";


    @BeforeClass
    public void setUp() {

        // Initialize ChromeDriver
        driver = new ChromeDriver();

        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Maximize the browser window
        driver.manage().window().maximize();

        driver.get("http://localhost:3000/");

        // Perform login
        login(username, password);

        // Navigate to the Certifications page
        driver.get("http://localhost:3000/Quest");
    }

    private void login(String username, String password) {
        // Enter username
        WebElement usernameField = driver.findElement(By.id(":r0:"));
        usernameField.sendKeys(username);

        // Enter password
        WebElement passwordField = driver.findElement(By.id("pwd"));
        passwordField.sendKeys(password);

        // Click login button
        WebElement loginButton = driver.findElement(By.xpath("//button[normalize-space()='Login']")); // Adjust selector as needed
        loginButton.click();

        // Verify successful login
        Assert.assertTrue(driver.findElement(By.xpath("//button[normalize-space()='Login']")).isDisplayed(), "Login failed!");
    }

    @Test
    public void testTitleAndHeaderDisplay() {
        String expectedTitle = "Vite + React";
        Assert.assertEquals(driver.getTitle(), expectedTitle, "Page title does not match!");

        WebElement header = driver.findElement(By.xpath("//button[normalize-space()='Certifications']"));
        Assert.assertEquals(header.getText(), "Certifications", "Header does match!");
    }

    @Test
    public void testInformationDisplay() {
        WebElement expiredLabel = driver.findElement(By.cssSelector("h6[class='MuiTypography-root MuiTypography-h6 css-cpax1e']"));
        String expectedText = "EXPIRED - JA+ I V8 For OCA-JP-I SE8 (1Z0-808)";
        Assert.assertEquals(expiredLabel.getText(), expectedText, "Expired test information does match!");
    }

    @Test
    public void verifyTestLinksClickable() {
        List<WebElement> testLinks = driver.findElements(By.xpath("//body[1]/div[1]/div[1]/div[2]/div[2]/table[1]/tbody[1]/tr[1]/td[2]"));
        for (WebElement link : testLinks) {
            link.click();
            Assert.assertTrue(driver.getCurrentUrl().contains("/test/{id}"), "Test page URL is correct");
            driver.navigate().back(); // Navigate back to certification page after verifying
        }
    }

    @AfterClass
    public void tearDown() {
        // Close the browser after all tests are done
        if (driver != null) {
            driver.quit();
        }
    }
}

