package com.tribu.qaselenium.testframework.pagebase;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tribu.qaselenium.testframework.testbase.DriverFactory;

public class GeneralUtils {

	protected Logger log;

	// Find element using given locator
	protected static WebElement find(By locator, WebDriver driver, Logger log) {
		try {
			return driver.findElement(locator);
		} catch (WebDriverException e) {
			log.info("Fail: WebDriver couldn’t locate the element: " + locator);
			throw (e);
		}
	}

	// Wait for given number of seconds for element with given locator to be visible
	// on the page, Explicit wait.
	public static void waitForVisibilityOf(By locator, Integer timeOutInSeconds, WebDriver driver) {
		timeOutInSeconds = timeOutInSeconds != null ? timeOutInSeconds : 30;
		new WebDriverWait(driver, timeOutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	// Waiting for page is whole loaded
	public static void waitForPageToLoad(WebDriver driver, Logger log) {
		try {
			/*
			 * lambda function, verifies if the document.readyState is complete, has a
			 * timeout of 30 seconds
			 */
			new WebDriverWait(driver, 30).until(webDriver -> ((JavascriptExecutor) webDriver)
					.executeScript("return document.readyState").equals("complete"));
		} catch (Exception e) {
			log.info("WaitForPageLoad timeout");
			throw (e);
		}
	}
}
