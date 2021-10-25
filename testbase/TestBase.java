package com.tribu.qaselenium.testframework.testbase;

import java.lang.reflect.Method;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

public class TestBase extends TestUtilities {

	protected DriverFactory driverFactory = null;
	protected TestLoggerFactory loggerFactory = null;
	protected WebDriver driver;
	

	/** create a Browser driver factory it lets run several test in parallel */
	@BeforeTest(alwaysRun = true)
	public void createFactories() {

		loggerFactory = TestLoggerFactory.getInstance();
		driverFactory = DriverFactory.getInstance();
	}

	@Parameters({ "browser" })
	@BeforeMethod(alwaysRun = true)
	public void setUp(Method method, @Optional("chrome") String browser, ITestContext context) {

		testName = context.getCurrentXmlTest().getName();
		testSuiteName = context.getSuite().getName();
		testMethodName = method.getName();
		
		/* loggerFactory creates a new instance of logger */
		loggerFactory.crateLogger(testName);
		log = loggerFactory.getLogger();
		log.info("Se ejecuta setup() ");

		/* driverFactory creates a new instance of webdriver "browser" */
		driverFactory.createDriver(browser, log);
		driverFactory.getDriver().manage().window().maximize();

		// pass an attribute to context variable in order to the TestListener can
		// retrieve it
		context.setAttribute("driverFactory", driverFactory);
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		// Close browser
		driverFactory.getDriver().quit();
	}

}
