package com.tribu.seleniumframework.testbase;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

public class TestsListenerManager extends TestUtilities implements ITestListener {
	WebDriver driver = null;
	BrowserDriverFactory factory = null;
	@Override
	public void onTestSuccess(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		ITestContext context = result.getTestContext();
		factory = (BrowserDriverFactory) context.getAttribute("driverFactory");
		Reporter.log(" <img src='" + takeScreenshot(factory.getDriver()) + "' height='250' width='300'/> ");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		ITestContext context = result.getTestContext();
		factory = (BrowserDriverFactory) context.getAttribute("driverFactory");
		Reporter.log(" <img src='" + takeScreenshot(factory.getDriver()) + "' height='250' width='300'/> ");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		ITestContext context = result.getTestContext();
		factory = (BrowserDriverFactory) context.getAttribute("driverFactory");
		Reporter.log(" <img src='" + takeScreenshot(factory.getDriver()) + "' height='250' width='300'/> ");
	}

}
