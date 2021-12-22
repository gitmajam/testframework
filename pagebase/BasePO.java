package com.tribu.qaselenium.testframework.pagebase;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Supplier;
import com.tribu.qaselenium.testframework.testbase.DriverFactory;
import com.tribu.qaselenium.testframework.testbase.TestLoggerFactory;

public abstract class BasePO<T> {

	protected By locator;
	//Page title for switch to this page
	protected String PTitle;
	// this string variables are used in case you need to create a xpath reference
	// in runtime, example: Hello Frontline
	protected String xpathPart;
	protected String xpathVariable1;
	protected String xpathVariable2;

	protected Logger log = TestLoggerFactory.getInstance().getLogger();
	protected Supplier<WebDriver> driver = () -> DriverFactory.getInstance().getDriver();

	public <R> Supplier<R> click(Supplier<R> pageSupplier) {
		GUtils.waitForVisibilityOf(locator, 5, driver.get());
		try {
			find(locator).click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driver.get();
			executor.executeScript("arguments[0].click();", find(locator));
			log.info("click por javascript");
		}
		GUtils.waitForPageToLoad(driver.get());
		return pageSupplier;
	}

	// simple click method without return's object
	public T click() {
		GUtils.waitForVisibilityOf(locator, 10, driver.get());
		GUtils.waitForClickableOf(locator, 10, driver.get());
		try {
			find(locator).click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driver.get();
			executor.executeScript("arguments[0].click();", find(locator));
			log.info("click por javascript");
		}
		GUtils.waitForPageToLoad(driver.get());
		return (T) this;
	}

	// Type given text into element with given locator
	public T type(String text) {
		GUtils.waitForVisibilityOf(locator, 5, driver.get());
		find(locator).sendKeys(text);
		return (T) this;
	}

	public T hoverElement() {
		Actions actions = new Actions(driver.get());
		GUtils.waitForVisibilityOf(locator, 5, driver.get());
		actions.moveToElement(find(locator)).perform();
		return (T) this;
	}

	// Clear given text into element with given locator
	public T clear() {
		GUtils.waitForVisibilityOf(locator, 5, driver.get());
		find(locator).clear();
		return (T) this;
	}

	public String getText() {
		return find(locator).getText().trim();
	}

	public T swichToFrame() {
		driver.get().switchTo().frame(find(locator));
		return (T) this;
	}

	public T swichToMain() {
		driver.get().switchTo().defaultContent();
		return (T) this;
	}

	private WebElement find(By locator) {
		try {
			return driver.get().findElement(locator);
		} catch (WebDriverException e) {
			throw (e);
		}
	}

	public T waitForImage() {
		try {
			new WebDriverWait(driver.get(), 15).until(
					webDriver -> ((JavascriptExecutor) webDriver).executeScript("return arguments[0].complete && "
							+ "typeof arguments[0].naturalWidth != \"undefined\" && " + "arguments[0].naturalWidth > 0",
							find(locator)));
		} catch (Exception e) {
			log.info("WaitForImage timeout");
			throw (e);
		}
		return (T) this;
	}

	// to use with external locator (locator builded) by the tests suits, used in
	// asserts of images.
	public T waitForImage(String imgName) {
		this.locator = By.xpath(this.xpathPart + imgName + this.getTodaysDate() + "')]");
		try {
			new WebDriverWait(driver.get(), 15).until(
					webDriver -> ((JavascriptExecutor) webDriver).executeScript("return arguments[0].complete && "
							+ "typeof arguments[0].naturalWidth != \"undefined\" && " + "arguments[0].naturalWidth > 0",
							find(locator)));
		} catch (Exception e) {
			log.info("WaitForImage timeout");
			throw (e);
		}
		return (T) this;
	}

	public T waitForVisivility() {
		GUtils.waitForVisibilityOf(locator, 90, driver.get());
		return (T) this;
	}
	
	public T waitForNotVisivility() {
		GUtils.waitForNotVisibilityOf(locator, 30, driver.get());
		return (T) this;
	}

	public String getCurrentUrl() {
		return driver.get().getCurrentUrl();
	}

	public abstract String getPageUrl();

	/* asserts */

	// looking for text inside other text using in asserts
	public Boolean contains(String text) {
		GUtils.waitForVisibilityOf(locator, 5, driver.get());
		return find(locator).getText().contains(text);
	}

	public Boolean isDisplayed() {
		return find(locator).isDisplayed();
	}

	// to verify if element is displayed, if not, it returns an Exception to be
	// handle by the caller
	public Boolean existElement() throws Exception {
		return driver.get().findElement(locator).isDisplayed();
	}

	public Boolean verifyImage() {
		Object result = ((JavascriptExecutor) driver.get()).executeScript("return arguments[0].complete && "
				+ "typeof arguments[0].naturalWidth != \"undefined\" && " + "arguments[0].naturalWidth > 0",
				find(locator));

		Boolean loaded = false;
		if (result instanceof Boolean) {
			loaded = (Boolean) result;
		}
		return loaded;
	}

	// method to retrieve a field from this (T) object using it's name trough a
	// string
	public T selectField(String s) {
		Field field = null;
		By campo = null;
		try {
			field = this.getClass().getDeclaredField(s);
			field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // or StudentRecord.class.getField()
		try {
			campo = (By) field.get(this);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.locator = campo;
		return (T) this;
	};

	/* switch to new window page, ex: opening hello-beer in simplifica hub */

	public T switchToWindowWithTitle() {
		String firstWindow = driver.get().getWindowHandle();
		Set<String> allWindows = driver.get().getWindowHandles();
		Iterator<String> windowsIterator = allWindows.iterator();

		while (windowsIterator.hasNext()) {
			String windowHandle = windowsIterator.next().toString();
			if (!windowHandle.equals(firstWindow)) {
				driver.get().switchTo().window(windowHandle);
				if (getCurrentPageTitle().contains(PTitle)) {
					log.info("CurretPageTitle = " + getCurrentPageTitle());
					log.info("ExpectedPageTitle = " + PTitle);
					break;
				}
			}
		}
		return (T) this;
	}

	/** Get title of current page */
	public String getCurrentPageTitle() {
		return driver.get().getTitle();
	}

	/* Utils */

	/** Todays date in yyyyMMdd format */
	protected String getTodaysDate() {
		return "-" + new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	/** Current time in HHmmssSSS */
	protected String getSystemTime() {
		return "-" + new SimpleDateFormat("HHmmssSSS").format(new Date());
	}
}
