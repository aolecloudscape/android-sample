package MobileCloudComputing;

import java.io.IOException;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.lang.Process;

public class startappium {
	
	@BeforeSuite
	public void appium() throws IOException, InterruptedException {
		//Process p = Runtime.getRuntime().exec("adb install /exercise/test_case_select.apk");
		//Process r = Runtime.getRuntime().exec("/bin/sh -c appium");

  //    p.waitFor();
 //     r.waitFor();
		//cd Thread.sleep(7000L);
	}
	
	@AfterSuite
	public void killappium() throws IOException {
		
	//	Runtime.getRuntime().exec("/bin/sh -c killall node");
		
	}
	
}
