package com.tribu.qaselenium.testframework.testbase;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

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
		log.info("run setUp Method");
		/* driverFactory creates a new instance of webdriver "browser" */
		driverFactory.createDriver(browser);
		log.info("drive recien creado = " + driverFactory.getDriver().hashCode());
		driverFactory.getDriver().manage().window().maximize();
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		if (PropertiesFile.getProperties("teardown").equals("true")) {
			driverFactory.getDriver().quit();
			log.info("drive recien cerrado" + driverFactory.getDriver().hashCode());
		}
	}
}
