package com.tribu.qaselenium.testframework.utilities;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.tribu.qaselenium.testframework.testbase.DriverFactory;
import com.tribu.qaselenium.testframework.testbase.TestLoggerFactory;

public class WaitUtils {

	protected static Logger log = TestLoggerFactory.getInstance().getLogger();
	protected static Supplier<WebDriver> driverFunc = () -> DriverFactory.getInstance().getDriver();

	public static WebElement waitForVisibilityByfilter(By locator, SearchContext searchContext,
			List<Predicate<WebElement>> predicateList, Long... delays) {
		Long timeOut = delays.length > 0 ? delays[0] : 4000;
		WebElement element = null;
		try {
			Wait<SearchContext> wait = new FluentWait<SearchContext>(searchContext)
					.withTimeout(Duration.ofMillis(timeOut))
					.pollingEvery(Duration.ofMillis(500L))
					.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

			element = wait.until(CheckedConditions.visibilityOfElementLocatedByFilter(locator, predicateList));
		} catch (Exception e) {
			log.info("timeout waiting for visibility of searchContext : " + searchContext + " locator : " + locator);
		}
		return element;
	}
	
	public static WebElement waitForEnableStatusByfilter(By locator, SearchContext searchContext,
			List<Predicate<WebElement>> predicateList, Long... delays) {
		Long timeOut = delays.length > 0 ? delays[0] : 4000;
		WebElement element = null;
		try {
			Wait<SearchContext> wait = new FluentWait<SearchContext>(searchContext)
					.withTimeout(Duration.ofMillis(timeOut))
					.pollingEvery(Duration.ofMillis(500L))
					.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

			element = wait.until(CheckedConditions.enableStatusOfElementLocatedByFilter(locator, predicateList));
		} catch (Exception e) {
			log.info("timeout waiting for enable status of searchContext : " + searchContext + " locator : " + locator);
		}
		return element;
	}
	
	//return a value diferent to null or timeout exception
	public static WebElement waitForLoad(By locator, SearchContext searchContext,
			List<Predicate<WebElement>> predicateList, Integer... delays) {
		Integer timeOut = delays.length > 0 ? delays[0] : 3000;
		WebElement element = null;
		try {
			Wait<SearchContext> wait = new FluentWait<SearchContext>(searchContext)
					.withTimeout(Duration.ofSeconds(timeOut))
					.pollingEvery(Duration.ofSeconds(5L))
					.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

			element = wait.until(CheckedConditions.visibilityOfElementLocatedByFilter(locator, predicateList));
		} catch (Exception e) {
			log.info("timeout waiting for load : " + searchContext + " locator : " + locator);
		}
		return element;
	}
	
	public static void waitLoadImage(WebElement element, SearchContext searchContext, Long... delays) {
		Long timeOut = delays.length > 0 ? delays[0] : 5L;
		try {
			Wait<SearchContext> wait = new FluentWait<SearchContext>(searchContext)
					.withTimeout(Duration.ofSeconds(timeOut))
					.pollingEvery(Duration.ofMillis(500L))
					.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

			wait.until(CheckedConditions.imageLoadedByFilter(element));
		} catch (Exception e) {
			log.info("WaitForImage timeout");
		}
	}

	public static Boolean waitForInvisibilityByfilter(By locator, SearchContext searchContext,
			List<Predicate<WebElement>> predicateList) {
		Boolean isNotPresent = false;
		try {
			Wait<SearchContext> wait = new FluentWait<SearchContext>(searchContext)
					.withTimeout(Duration.ofMillis(10000L)).pollingEvery(Duration.ofMillis(500L))
					.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

			isNotPresent = wait.until(CheckedConditions.invisibilityOfElementLocatedByFilter(locator, predicateList));
		} catch (Exception e) {
			log.info("timeout waiting for not presence of locator : " + locator);
			isNotPresent = false;
		}
		return isNotPresent;
	}
	
	public static Boolean waitForNotPresenceByfilter(By locator, SearchContext searchContext,
			List<Predicate<WebElement>> predicateList) {
		Boolean isInvisible = false;
		try {
			Wait<SearchContext> wait = new FluentWait<SearchContext>(searchContext)
					.withTimeout(Duration.ofMillis(10000L)).pollingEvery(Duration.ofMillis(500L))
					.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

			isInvisible = wait.until(CheckedConditions.notPresenceOfElementLocatedByFilter(locator, predicateList));
		} catch (Exception e) {
			log.info("timeout waiting for invisibility of locator : " + locator);
			isInvisible = false;
		}
		return isInvisible;
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
