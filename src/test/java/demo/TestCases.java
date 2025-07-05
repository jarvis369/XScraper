package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;
    Wrappers wrapper;

    /*
     * TODO: Write your tests here with testng @Test annotation.
     * Follow `testCase01` `testCase02`... format or what is provided in
     * instructions
     */

    /*
     * Do not change the provided methods unless necessary, they will help in
     * automation and assessment
     */
    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        wrapper = new Wrappers(driver);
    }

    @Test
    public void testcase01() throws IOException, InterruptedException {
        System.out.println("📌 Starting testcase01 - Hockey Teams Scraper");
        testCase01_scrapeHockeyTeamsWithLowWinPercent();
        System.out.println("✅ testcase01 executed successfully.\n");
    }

    @Test
    public void testcase02() throws IOException, InterruptedException {
        System.out.println("📌 Starting testcase02 - Oscar Winners Scraper");
        testCase02_scrapeOscarWinners();
        System.out.println("✅ testcase02 executed successfully.\n");
    }

    public void testCase01_scrapeHockeyTeamsWithLowWinPercent() throws IOException, InterruptedException {
        System.out.println("Start test case : testcase01");
        driver.get("https://www.scrapethissite.com/pages/forms/");

        List<HashMap<String, Object>> resultList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            List<WebElement> rows = driver.findElements(By.cssSelector("table.table tbody tr"));

            for (WebElement row : rows) {
                String teamName = row.findElement(By.xpath("//tbody//tr[2]/td[1]")).getText().trim();
                String year = row
                        .findElement(By.xpath("//body[1]/div[1]/section[1]/div[1]/table[1]/tbody[1]/tr[2]/td[2]"))
                        .getText().trim();
                String winPercentStr = row.findElement(By.xpath("//tbody//tr[2]/td[6]")).getText().trim();
                double winPercent = Double.parseDouble(winPercentStr);

                if (winPercent < 0.40) {
                    HashMap<String, Object> teamMap = new HashMap<>();
                    teamMap.put("Epoch Time of Scrape", Instant.now().getEpochSecond());
                    teamMap.put("Team Name", teamName);
                    teamMap.put("Year", year);
                    teamMap.put("Win %", winPercent);
                    resultList.add(teamMap);
                }
            }

            if (i < 3) { // click "Next" on the first 3 pages
                WebElement nextBtn = driver.findElement(By.xpath("//span[@aria-hidden=\"true\"]"));
                nextBtn.click();
                Thread.sleep(2000); // allow page load
            }
        }

        // Save result to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("hockey-team-data.json"), resultList);

        System.out.println("Scraping complete. Output written to hockey-team-data.json");
        System.out.println("End test case : testcase01");
    }

    public void testCase02_scrapeOscarWinners() throws IOException, InterruptedException {
        System.out.println("Start test case : testcase02");
        driver.get("https://www.scrapethissite.com/pages/");

        // Click on "Oscar Winning Films"
        driver.findElement(By.xpath("//a[normalize-space()='Oscar Winning Films: AJAX and Javascript']"));
        Thread.sleep(2000); // Optional: Let the page load

        List<HashMap<String, Object>> resultList = new ArrayList<>();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long epochTime = System.currentTimeMillis();

        // Get all year tabs
        List<WebElement> yearTabs = driver.findElements(By.xpath("//div[@class='col-md-12 text-center']//a"));

        for (WebElement yearTab : yearTabs) {
            String year = yearTab.getText().trim();
            yearTab.click();
            Thread.sleep(1500); // Let the table load

            // Select top 5 rows from the table
            List<WebElement> rows = driver.findElements(By.id("table-body"));
            for (int i = 0; i < Math.min(5, rows.size()); i++) {
                WebElement row = rows.get(i);
                List<WebElement> cols = row.findElements(By.tagName("td"));

                String title = cols.get(0).getText();
                int nominations = Integer.parseInt(cols.get(1).getText());
                int awards = Integer.parseInt(cols.get(2).getText());
                boolean isWinner = !cols.get(3).getText().isEmpty(); // Best Picture 🏆 symbol

                HashMap<String, Object> map = new HashMap<>();
                map.put("Epoch Time of Scrape", epochTime);
                map.put("Year", year);
                map.put("Title", title);
                map.put("Nomination", nominations);
                map.put("Awards", awards);
                map.put("isWinner", isWinner);

                resultList.add(map);
            }
        }

        // Save to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File("oscar-winner-data.json"), resultList);

        // TestNG assertion to ensure file is created
        File file = new File("oscar-winner-data.json");
        Assert.assertTrue(file.exists() && file.length() > 0, "File not created or is empty.");

        System.out.println("Data scraping complete. Output saved to oscar-winner-data.json");
        System.out.println("End test case : testcase02");
    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();

    }
}