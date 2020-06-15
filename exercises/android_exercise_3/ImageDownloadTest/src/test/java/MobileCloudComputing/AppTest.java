package MobileCloudComputing;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterSuite;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import java.lang.System;
import java.util.List;

public class AppTest 
{		

    AndroidDriver driver;

    private final String EditTextId = "txtUrl";

    private final String ButtonId = "btnLoadImg";

    private final String JsonUrl = System.getenv("JSON_PHOTOS_LOCATION");//"https://raw.githubusercontent.com/djbb7/stupidjson/master/photos.json";

    private final String screenshotDir = System.getenv("SCREENSHOT_DIR");
    
    @BeforeClass
    public void InstallAPK() throws IOException {
      File f = new File("/");
		File fs = new File(f, "/submission/user/application.apk");
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability("deviceName", "Android");
		cap.setCapability("newCommandTimeout", 100); // if appium receives no commands in this time, app is terminated, in s
		cap.setCapability("appWaitDuration", 60000 ); // maximum time to wait for app to initialize, in ms
		cap.setCapability("deviceReadyTimeout", 60); // timeout to wait for device to be ready, in s
		cap.setCapability("androidDeviceReadyTimeout", 60); // timeout to wait for device to be ready after booting, in s
		cap.setCapability("androidInstallTimeout", 120000); // timeout to wait for app to install, in ms
      cap.setCapability("autoWebviewTimeout", 10000); // time to wait for Webview context to become active, in ms
		cap.setCapability(MobileCapabilityType.DEVICE_NAME,"emulator-5554");
		cap.setCapability(MobileCapabilityType.APP, fs.getAbsolutePath());
      //cap.setCapability("unicodeKeyboard", true);
      //cap.setCapability("resetKeyboard", true);
		driver = new AndroidDriver(new URL(System.getenv("APPIUM_SERVER")+"/wd/hub"),cap);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test(description="Passes a JSON with a list of images and verifies that they are rendered in a ListView (Image and Author).")
    public void ImageExerciseTest() throws IOException, InterruptedException {
      String currId = null;
      int elem = 0;
      try { 
         // pass JSON URL
         currId = EditTextId;
         takeScreenshot(screenshotDir+"/step.png");
		   driver.findElement(By.id(EditTextId)).sendKeys(JsonUrl);

         // press 'load' button
         currId = ButtonId;
		   TouchAction t = new TouchAction(driver);
         takeScreenshot(screenshotDir+"/step.png");
		   t.tap(driver.findElement(By.id(ButtonId))).perform();

         // allow some time to download images
		   Thread.sleep(20000L);
         
		   //         driver.hideKeyboard();
         takeScreenshot(screenshotDir+"/step.png");
         currId = null;
         
		   Assert.assertEquals(driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().className(\"android.widget.ListView\")).scrollIntoView(new UiSelector().className(\"android.widget.TextView\").text(\"Toby Keller\").instance(0))").getText(), "Toby Keller");

         elem = 1;
		   Assert.assertEquals(driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().className(\"android.widget.ListView\")).scrollIntoView(new UiSelector().className(\"android.widget.TextView\").text(\"Louis Vest\").instance(0))").getText(), "Louis Vest");
         
         elem = 2;      
		   Assert.assertEquals(driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().className(\"android.widget.ListView\")).scrollIntoView(new UiSelector().className(\"android.widget.TextView\").text(\"Mike Behnken\").instance(0))").getText(), "Mike Behnken");

         elem = 3;
		   Assert.assertEquals(driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().className(\"android.widget.ListView\")).scrollIntoView(new UiSelector().className(\"android.widget.TextView\").text(\"Luigi Alesi\").instance(0))").getText(), "Luigi Alesi");

         elem = 4;
		   Assert.assertEquals(driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().className(\"android.widget.ListView\")).scrollIntoView(new UiSelector().className(\"android.widget.TextView\").text(\"Eleder Jimenez Hermoso\").instance(0))").getText(), "Eleder Jimenez Hermoso");

       }  catch (NoSuchElementException e) {
            if (currId == null) {
               Assert.fail("Could not find the "+elem+"th photo author in view. Is it visible on screen?");
            } else {
               Assert.fail("Could not find element with id '"+currId+"'. Please check the assignment instruction and use the provided View ids.");
            }
       } catch (Exception e) {
         Assert.fail(e.toString());
       }
   }

   private void takeScreenshot(String filePath) throws IOException, InterruptedException {
      Process p = Runtime.getRuntime().exec("adb shell screencap -p /sdcard/screen.png");         
      p.waitFor();

      p = Runtime.getRuntime().exec("adb pull /sdcard/screen.png "+filePath);
      p.waitFor();
   }

   @AfterSuite
	public void closeDriver() {      
      driver.quit();
   }
}
