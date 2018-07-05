import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class MailRuTest {

    private WebDriver driver;

    @BeforeClass
    public void beforeClass() {
        System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        driver.quit();
    }

    @Test(description = "mail.ru page should open and contain appropriate title")
    public void loginTest() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        // Open mail.ru page
        driver.navigate().to("https://mail.ru/");
        assertEquals(driver.getTitle(), "Mail.Ru: почта, поиск в интернете, новости, игры");

        // clear the email address field
        driver.findElement(By.name("login")).clear();

        //login to the mail box
        driver.findElement(By.name("login")).sendKeys("MoldavskaiaATM");
        driver.findElement(By.cssSelector(".input[type=password]")).sendKeys("ghbvtcm46");
        driver.findElement(By.cssSelector(".o-control[type=submit]")).click();

        // assert that the login was successful
        wait.until(ExpectedConditions.titleContains("Входящие - Почта Mail.Ru"));
        assertTrue(driver.getTitle().contains("Входящие - Почта Mail.Ru"));

        //Create a new mail
        WebElement newMailButton = driver.findElement(By.linkText("Написать письмо"));
        assertTrue(newMailButton.isDisplayed());
        newMailButton.click();
        wait.until(ExpectedConditions.titleIs("Новое письмо - Почта Mail.Ru"));
        driver.findElement(By.cssSelector(".js-input[data-original-name=To]"))
                .sendKeys("ekaterinamoldavskaia18@gmail.com");
        UUID subjectToMail = UUID.randomUUID();
        driver.findElement(By.name("Subject")).sendKeys(subjectToMail.toString());
        driver.switchTo().frame(driver.findElement(By.cssSelector("iframe")));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#tinymce"))).sendKeys("Text");

        // save the mail as draft
        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#b-toolbar__right [data-name='saveDraft']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-mnemo=\"saveStatus\"]")));

        //open drafts folder
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-mnemo=\"drafts\"]"))).click();
        assertEquals(driver.getTitle(), "Новое письмо - Почта Mail.Ru");

        // assert that draft presents in the Draft folder
        wait.until(ExpectedConditions.titleIs("Черновики - Почта Mail.Ru"));
        List<WebElement> subjectsOfMailForTest = driver.findElements(By.cssSelector(".b-datalist__item__subj"));
        List<String> textOfSubjects = new ArrayList<String>();
        for (WebElement subject : subjectsOfMailForTest) {
            String bodyOfMail = subject.findElement(By.cssSelector(".b-datalist__item__subj__snippet")).getText();
            textOfSubjects.add(subject.getText().replace(bodyOfMail, "").trim());
        }
        assertTrue(textOfSubjects.contains(subjectToMail.toString()), "The draft of test email is absent in the folder");

        //Open saved draft
        String toLocator = "[data-subject='" + subjectToMail + "']";
        driver.findElement(By.cssSelector(toLocator)).click();
        assertEquals(driver.getTitle(), "Новое письмо - Почта Mail.Ru");

        // assert that all field contain the same information that before saving as draft
        assertEquals(driver.findElement(By.cssSelector("[data-text='ekaterinamoldavskaia18@gmail.com']"))
                .getText(), "ekaterinamoldavskaia18@gmail.com");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object subToMail = js.executeScript("return document.getElementsByName('Subject')[0].value");
        assertEquals(subToMail, subjectToMail.toString());
        driver.switchTo().frame(driver.findElement(By.cssSelector("iframe")));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#tinymce")));
        assertTrue(driver.findElement(By.cssSelector("#tinymce")).getText().contains("Text"), "Mail text is absent");
        driver.switchTo().defaultContent();

        // send email
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@data-name='send']"))).click();

        // assert that the draft disappeared from draft folder
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message-sent__title")));
        driver.findElement(By.cssSelector("[data-mnemo='drafts']")).click();
        wait.until(ExpectedConditions.titleIs("Черновики - Почта Mail.Ru"));
        List<WebElement> subjectsOfMailForTestAfterSending = driver.findElements(
                By.cssSelector(".b-datalist__item__subj"));
        List<String> textOfSubjectsAfterSending = new ArrayList<String>();
        for (WebElement subjectAfterSending : subjectsOfMailForTestAfterSending) {
            String bodyOfMail2 = subjectAfterSending.findElement(By.cssSelector(".b-datalist__item__subj__snippet"))
                    .getText();
            textOfSubjectsAfterSending.add(subjectAfterSending.getText().replace(bodyOfMail2, "").trim());
        }
        assertFalse(textOfSubjectsAfterSending.contains(
                subjectToMail.toString()), "The draft of test email is absent in the folder");

        //assert the sent mail presents in Sent folder
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[href='/messages/sent/']"))).click();
        wait.until(ExpectedConditions.titleIs("Отправленные - Почта Mail.Ru"));
        List<WebElement> sentMails = driver.findElements(By.cssSelector(".b-datalist__item__subj"));
        List<String> sentSubjects = new ArrayList<String>();
        for (WebElement sentMail : sentMails) {
            String bodyOfMail = sentMail.findElement(By.cssSelector(".b-datalist__item__subj__snippet")).getText();
            sentSubjects.add(sentMail.getText().replace(bodyOfMail, "").trim());
        }
        assertTrue(sentSubjects.contains(subjectToMail.toString()), "The sent test email is absent in the folder");

        //Log off
        driver.findElement(By.cssSelector("#PH_logoutLink")).click();
        assertEquals(driver.getTitle(), "Mail.Ru: почта, поиск в интернете, новости, игры");
    }
}
