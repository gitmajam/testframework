package com.tribu.qaselenium.testframework.testbase;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

public class TestsListenerManager extends TestUtilities implements ITestListener {

	@Override
	public void onTestSuccess(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		Reporter.log(" <img src='" + takeScreenshot() + "' height='250' width='300'/> ");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		Reporter.log(" <img src='" + takeScreenshot() + "' height='250' width='300'/> ");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		Reporter.setCurrentTestResult(result);
		Reporter.log(" <img src='" + takeScreenshot() + "' height='250' width='300'/> ");
	}

}
