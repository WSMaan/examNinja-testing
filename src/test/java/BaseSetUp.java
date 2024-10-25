import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import lombok.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseSetUp {

    private static Logger log = Logger.getLogger(BaseSetUp.class.getName());
    private WebDriver webDriver = null;
    private static Properties objRepo = null;
    private static DriverManagerType browser = null;

    @BeforeClass
    @SneakyThrows
    public void setUpForAllTest() {
        // Load logging configuration from application.properties
        LogManager.getLogManager().readConfiguration(new FileInputStream("src/test/resources/application.properties"));

        log.info("Loading object repository and setting up browser.");
        objRepo = loadObjectRepository();
        browser = DriverManagerType.valueOf(objRepo.get("browser").toString());
        WebDriverManager.getInstance(browser).setup();
    }

    @BeforeMethod
    @SneakyThrows
    public void init() {
        log.info("Initializing WebDriver");
        if (webDriver == null) {
            Class<?> chromeClass = Class.forName(browser.browserClass());
            webDriver = (WebDriver) chromeClass.newInstance();
        }
    }

    @SneakyThrows
    private static Properties loadObjectRepository() {
        objRepo = new Properties();
        objRepo.load(new FileInputStream(new File("log.properties")));
        return objRepo;
    }

    public Properties getObjRepo() {
        return objRepo;
    }

    @AfterMethod
    public void tearDown() {
        if (webDriver != null) {
            webDriver.quit();  // Quit the WebDriver session after the test
            webDriver = null;  // Set to null to avoid reuse
        }
    }
}


