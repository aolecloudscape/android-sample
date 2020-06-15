package MobileCloudComputing;

import org.testng.IClass;
import org.testng.ITestResult;
import org.testng.ITestContext;
import org.testng.TestListenerAdapter;
import java.lang.Throwable;
import java.lang.Exception;

public class AplusListener extends TestListenerAdapter {
		@Override
		public void onTestStart(ITestResult tr) {
         log("=====================================================================");
			log("Running test: "+tr.getName());

         log("Description: "+tr.getMethod().getDescription());
		}
		
		@Override
		public void onTestSuccess(ITestResult tr) {
		   log("Result: Passed");
		}
		
		@Override
		public void onTestSkipped(ITestResult tr) {
		   // TODO Auto-generated method stub
		   log("Result: Skipped");
		}


		@Override
		public void onTestFailure(ITestResult tr) {
         Throwable throwable = tr.getThrowable();
         String message = throwable.getMessage();
		   log("Result: Failed");
         log("Cause: "+message);
		}
   
      @Override
      public void onFinish(ITestContext testContext) {
         log("=====================================================================");      
      }

		private void log(String methodname) {
			System.out.println(methodname);
		}
		
		private void log(IClass testClass) {
			System.out.println(testClass);
		}
}
