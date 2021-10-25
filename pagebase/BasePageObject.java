package com.tribu.qaselenium.testframework.pagebase;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.TestNG;
import org.testng.annotations.Parameters;

import com.tribu.qaselenium.testframework.testbase.DriverFactory;
import com.tribu.qaselenium.testframework.testbase.TestLoggerFactory;

public abstract class BasePageObject {

	protected Logger log = TestLoggerFactory.getInstance().getLogger();

	// Click on element with given locator when its visible
	protected void click(WebDriver driver,By locator) {
		GeneralUtils.waitForVisibilityOf(locator, 5, driver);
		driver.findElement(locator).click();
		GeneralUtils.waitForPageToLoad(driver, log);
	}

	// Type given text into element with given locator
	protected void type(WebDriver driver,String text, By locator) {
		GeneralUtils.waitForVisibilityOf(locator, 5, driver);
		driver.findElement(locator).sendKeys(text);
	}

	// Get URL of current page from browser
	public String getCurrentUrl(WebDriver driver) {
		return driver.getCurrentUrl();
	}
	
	public abstract String getPageUrl();
}
