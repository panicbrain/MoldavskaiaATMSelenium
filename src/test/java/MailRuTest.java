import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    }

    //@AfterClass(alwaysRun = true)
    //public void closeBrowser() {
    //    driver.quit();
    //  }

    @Test(description = "mail.ru page should open and contain appropriate title")
    public void loginTest() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(15,TimeUnit.SECONDS);
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
        UUID subjectToMail = UUID.randomUUID();
        driver.findElement(By.name("Subject")).sendKeys("" + subjectToMail);
        driver.switchTo().frame(driver.findElement(By.cssSelector("iframe")));
        driver.findElement(By.cssSelector("#tinymce")).sendKeys("Text");

        // save the mail as draft
        driver.switchTo().defaultContent();
        Thread.sleep(5000);
        driver.findElement(By.cssSelector("#b-toolbar__right [data-name='saveDraft']")).click();
        //open drafts folder
       Thread.sleep(5000);
        driver.findElement(By.cssSelector("[data-mnemo=\"drafts\"]")).click();
        assertEquals(driver.getTitle(), "Новое письмо - Почта Mail.Ru");
        // assert that draft presents in the Draft folder
     Thread.sleep(5000);
        List<WebElement> subjectsOfMailForTest = driver.findElements(By.cssSelector(".b-datalist__item__subj"));
        List<String> textOfSubjects = new ArrayList<String>();
        for (WebElement subject : subjectsOfMailForTest) {
            String bodyOfMail = subject.findElement(By.cssSelector(".b-datalist__item__subj__snippet")).getText();
            textOfSubjects.add(subject.getText().replace(bodyOfMail, "").trim());
        }
        assertTrue(textOfSubjects.contains("" + subjectToMail), "The draft of test email is absent in the folder");
        //Open draft
        String toLocator = "[data-subject='" + subjectToMail + "']";
        driver.findElement(By.cssSelector(toLocator)).click();
        assertEquals(driver.getTitle(), "Новое письмо - Почта Mail.Ru");
        // assert that all field contain the same information that before saving as draft
        assertEquals(driver.findElement(By.cssSelector("[data-text='ekaterinamoldavskaia18@gmail.com']"))
                .getText(), "ekaterinamoldavskaia18@gmail.com");
        JavascriptExecutor js = (JavascriptExecutor)driver;
        Object subToMail = js.executeScript("document.getElementsByName('Subject')[0].value");

        assertEquals(subToMail,"" + subjectToMail);
        driver.switchTo().frame(driver.findElement(By.cssSelector("iframe")));
        Thread.sleep(5000);
        assertTrue(driver.findElement(By.cssSelector("#tinymce")).getText().contains("Text"), "Mail text is absent");
       // driver.switchTo().defaultContent();
      //  driver.findElement(By.linkText("Отправить")).click();

    }
}
