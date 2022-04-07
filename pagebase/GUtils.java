package com.tribu.qaselenium.testframework.pagebase;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Supplier;
import com.tribu.qaselenium.testframework.testbase.DriverFactory;
import com.tribu.qaselenium.testframework.testbase.TestLoggerFactory;

public class GUtils {

	protected static Logger log = TestLoggerFactory.getInstance().getLogger();
	protected static Supplier<WebDriver> driverFunc = () -> DriverFactory.getInstance().getDriver();

	// Find element using given locator
	protected static WebElement find(By locator, WebDriver driver) {
		try {
			return driver.findElement(locator);
		} catch (WebDriverException e) {
			log.info("Fail: WebDriver couldn’t locate the element: " + locator);
			throw (e);
		}
	}

	public static void waitForClickableOf(By locator) {
		try {
			new WebDriverWait(driverFunc.get(), 10).until(ExpectedConditions.elementToBeClickable(locator));
		} catch (Exception e) {
			log.info("error waiting for to be cickable of locator : " + locator);
		}
	}

	// Wait for given number of seconds for element with given locator to be visible
	// on the page, Explicit wait.
	public static void waitForVisibilityOf(By locator) {
		try {
			new WebDriverWait(driverFunc.get(), 10).until(ExpectedConditions.visibilityOfElementLocated(locator));
		} catch (Exception e) {
			log.info("error waiting for visibility of locator : " + locator);
		}
	}

	// Wait for given number of seconds for element with given locator to be visible
	// on the page, Explicit wait.
	public static void waitForPresenceOfminimunElements(By locator, Integer minElements) {
		try {
			new WebDriverWait(driverFunc.get(), 20)
					.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, minElements - 1));
		} catch (Exception e) {
			log.info("error waiting for numberOfElementsToBeMoreThan of locator : " + locator);
		}
	}

	// Wait for given number of seconds for element with given locator to be
	// invisible
	// on the page, Explicit wait.
	public static void waitForNotVisibilityOf(By locator) {
		try {
			Boolean a = new WebDriverWait(driverFunc.get(), 10)
					.until(ExpectedConditions.invisibilityOfElementLocated(locator));
		} catch (Exception e) {
			log.info("error waiting for non visibility of locator : " + locator);
		}
	}

	public static void waitAttributes(WebElement webElement, String... attributes) {
		String attribute = attributes.length > 0 ? attributes[0] : null;
		String value = attributes.length > 0 ? attributes[1] : null;
		if (attribute != null) {
			new WebDriverWait(driverFunc.get(), 10)
					.until(ExpectedConditions.attributeToBe(webElement, "aria-selected", "true"));
		}
	}

	// Waiting for page is whole loaded
	public static void waitForPageToLoad() {
		try {
			/*
			 * lambda function, verifies if the document.readyState is complete, has a
			 * timeout of 30 seconds
			 */
			new WebDriverWait(driverFunc.get(), 30).until(webDriver -> ((JavascriptExecutor) webDriver)
					.executeScript("return document.readyState").equals("complete"));
		} catch (Exception e) {
			log.info("WaitForPageLoad timeout");
			throw (e);
		}
	}

}
