import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MailRuTest {

    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
    }

    //@AfterClass(alwaysRun = true)
    //public void closeBrowser() {
    //     driver.quit();
    //  }

    @Test(description = "mail.ru page should open and contain appropriate title")
    public void loginTest() throws InterruptedException {
        // Open mail.ru page
        driver.navigate().to("https://mail.ru/");
        assertEquals(driver.getTitle(), "Mail.Ru: почта, поиск в интернете, новости, игры");
        // clear the email address field
        driver.findElement(By.name("login")).clear();
        //login to the mail box
        driver.findElement(By.name("login")).sendKeys("MoldavskaiaATM");
        driver.findElement(By.cssSelector(".input[type=password]")).sendKeys("ghbvtcm46");
        driver.findElement(By.cssSelector(".o-control[type=submit]")).click();
        Thread.sleep(5000);
        // assert that the login was successful
        assertTrue(driver.getTitle().contains("Входящие - Почта Mail.Ru"));
        //Create a new mail
        WebElement newMailButton = driver.findElement(By.linkText("Написать письмо"));
        assertTrue(newMailButton.isDisplayed());
        newMailButton.click();
        Thread.sleep(5000);
        assertEquals(driver.getTitle(), "Новое письмо - Почта Mail.Ru");
        driver.findElement(By.cssSelector(".js-input[data-original-name=To]"))
                .sendKeys("ekaterinamoldavskaia18@gmail.com");
        driver.findElement(By.name("Subject")).sendKeys("This is email for test");
       // driver.switchTo().frame(driver.findElement(By.cssSelector("iframe")));
        //driver.findElement(By.cssSelector("#tinymce")).sendKeys("Text");

    }
}
