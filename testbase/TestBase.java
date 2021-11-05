package com.tribu.qaselenium.testframework.testbase;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.google.common.base.Supplier;

public class TestBase extends TestUtilities {

	protected TestLoggerFactory loggerFactory = null;
	protected DriverFactory driverFactory;
	
	@Parameters({ "browser" })
	@BeforeMethod(alwaysRun = true)
	public void setUp(Method method, @Optional("chrome") String browser, ITestContext context) {
		testSuiteName = context.getSuite().getName();
		testName = context.getCurrentXmlTest().getName();
		// testMethodName = method.getName();

		driverFactory = DriverFactory.getInstance();
		loggerFactory = TestLoggerFactory.getInstance();
		
		loggerFactory.createLogger(testName);
		log = loggerFactory.getLogger();
		
		/* driverFactory creates a new instance of webdriver "browser" */
		driverFactory.createDriver(browser);
		driverFactory.getDriver().manage().window().maximize();
		log.info("run setUp Method");
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		// Close browser
		driverFactory.getDriver().quit();
		log.info("[" + driverFactory.getDriver().hashCode() + "] se cierra driver");
	}
}
