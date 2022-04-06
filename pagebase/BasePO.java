package com.tribu.qaselenium.testframework.pagebase;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.tribu.qaselenium.testframework.testbase.DriverFactory;
import com.tribu.qaselenium.testframework.testbase.TestLoggerFactory;

public abstract class BasePO<T> {

	// locator work variable
	protected By locator;
	// last selected element
	protected WebElement webElement;

	// Page title for switch to this page
	protected String PTitle;
	// this string variables are used in case you need to create a xpath reference
	// in runtime, example: Hello Frontline
	protected String xpathPart;
	protected String xpathVariable1;
	protected String xpathVariable2;
	// variable to work with asserts
	private Boolean status = null;
	// variable to work with lastWebElement as base element
	private Boolean baseElementStatus = false;
	protected WebElement BaseElement;

	protected Logger log = TestLoggerFactory.getInstance().getLogger();
	protected Supplier<WebDriver> driverFunc = () -> DriverFactory.getInstance().getDriver();

	protected String resourcesPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator;

	/*
	 * this method sets the work webElement variable in order to be use for all
	 * methods of this class if receives a predicate as argument this one is put
	 * into filter stream for search a specific webElement, else the method executes
	 * normal findElement method.
	 */
	@SuppressWarnings("unchecked")
	public T setWebElement(By by, Predicate<WebElement>... predicates) {
		this.locator = by;
		WebDriverWait waitPresence = new WebDriverWait(driverFunc.get(), 10);
		SearchContext searchContext = baseElementStatus ? BaseElement : driverFunc.get();
		List<WebElement> list = searchContext.findElements(locator);
		if (list.size() > 0) {
			if (predicates.length > 0) {
				Predicate<WebElement> predicate = predicates[0];
				waitPresence.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
				webElement = list.stream().filter(predicate).findFirst().get();
			} else {
				webElement = searchContext.findElement(locator);
			}
		}else {
			status = false;
			webElement = null;
		}
		return (T) this;
	}

	// simple click method without return's supplier object
	public T click() {
		GUtils.waitForVisibilityOf(webElement);
		GUtils.waitForClickableOf(webElement);
		try {
			webElement.click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driverFunc.get();
			executor.executeScript("arguments[0].click();", webElement);
			log.info("click por javascript : " + locator);
		}

		GUtils.waitForPageToLoad();
		return (T) this;
	}

	// click with delay before
	public T click(Integer miliSeconds) {
		click();
		try {
			Thread.sleep(miliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return (T) this;
	}

	// ths click method returns a supplier
	public <R> Supplier<R> click(Supplier<R> pageSupplier, Integer... delays) {
		Integer delay = delays.length > 0 ? delays[0] : null;
		if (delay != null) {
			click(delay);
		} else {
			click();
		}
		return pageSupplier;
	}

	// place (srcoll) de element at center of the viewport
	public T centerElement() {
		String centerElement = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
				+ "var elementTop = arguments[0].getBoundingClientRect().top;"
				+ "window.scrollBy(0, elementTop-(viewPortHeight/2));";
		((JavascriptExecutor) driverFunc.get()).executeScript(centerElement, webElement);
		return (T) this;
	}

	public T stayBaseElement() {
		BaseElement = webElement;
		this.baseElementStatus = true;
		return (T) this;
	}

	public T quitBaseElement() {
		BaseElement = null;
		this.baseElementStatus = false;
		return (T) this;
	}

	public T selectElement(Predicate<WebElement> predicateBase, Predicate<WebElement> predicateChild) {
		List<WebElement> list = driverFunc.get().findElements(locator);
		webElement = list.stream().filter(predicateBase).findFirst().get();
		return (T) this;
	}

	// Type given text into element with given locator
	public T type(String text) {
		GUtils.waitForVisibilityOf(webElement);
		webElement.sendKeys(text);
		return (T) this;
	}

	// type with delay before
	public T type(String text, Integer miliSeconds) {
		try {
			Thread.sleep(miliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("path : " + text);
		webElement.sendKeys(text);
		return (T) this;
	}

	public T hoverElement() {
		Actions actions = new Actions(driverFunc.get());
		GUtils.waitForVisibilityOf(webElement);
		actions.moveToElement(webElement).perform();
		return (T) this;
	}

	// slider element
	public T moveSlider(int times, String key) {
		switch (key) {
		case "LEFT":
			for (int i = 0; i < times; i++) {
				GUtils.waitForVisibilityOf(webElement);
				webElement.sendKeys(Keys.ARROW_LEFT);
			}
			break;
		case "RIGHT":
			for (int i = 0; i < times; i++) {
				GUtils.waitForVisibilityOf(webElement);
				webElement.sendKeys(Keys.ARROW_RIGHT);
			}
			break;
		case "STAY":
			GUtils.waitForVisibilityOf(webElement);
			webElement.sendKeys(Keys.ARROW_RIGHT);
			webElement.sendKeys(Keys.ARROW_LEFT);
			break;
		default:
			log.info("Key word is not valid");
			break;
		}
		return (T) this;
	}

	// slider element
	public T waitForTextChange() {
		String currentText = webElement.getText().trim();
		new WebDriverWait(driverFunc.get(), 20).until(
				ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(locator, currentText)));
		return (T) this;
	}

	// Clear given text into element with given locator
	public T clear() {
		GUtils.waitForVisibilityOf(webElement);
		webElement.clear();
		return (T) this;
	}

	public String getText() {
		return webElement.getText().trim();
	}

	public T swichToFrame() {
		driverFunc.get().switchTo().frame(webElement);
		return (T) this;
	}

	public T swichToActiveElement() {
		driverFunc.get().switchTo().activeElement();
		return (T) this;
	}

	public T acceptAlert() {
		try {
			WebDriverWait wait = new WebDriverWait(driverFunc.get(), 5);
			Alert alert = wait.until(ExpectedConditions.alertIsPresent());
			alert.accept();
		} catch (Exception e) {
			log.info(e);
		}
		return (T) this;
	}

	public T swichToMain() {
		driverFunc.get().switchTo().defaultContent();
		return (T) this;
	}

	public T waitForImage() {
		try {
			new WebDriverWait(driverFunc.get(), 15).until(
					webDriver -> ((JavascriptExecutor) webDriver).executeScript("return arguments[0].complete && "
							+ "typeof arguments[0].naturalWidth != \"undefined\" && " + "arguments[0].naturalWidth > 0",
							webElement));
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
			new WebDriverWait(driverFunc.get(), 15).until(
					webDriver -> ((JavascriptExecutor) webDriver).executeScript("return arguments[0].complete && "
							+ "typeof arguments[0].naturalWidth != \"undefined\" && " + "arguments[0].naturalWidth > 0",
							webElement));
		} catch (Exception e) {
			log.info("WaitForImage timeout");
			throw (e);
		}
		return (T) this;
	}

	public T waitForVisibility() {
		GUtils.waitForVisibilityOf(webElement);
		return (T) this;
	}

	public T waitForNotVisibility() {
		GUtils.waitForNotVisibilityOf(webElement);
		return (T) this;
	}

	public String getCurrentUrl() {
		return driverFunc.get().getCurrentUrl();
	}

	public abstract String getPageUrl();

	/* asserts */

	// looking for text inside other text using in asserts
	public Boolean contains(String text) {
		GUtils.waitForVisibilityOf(webElement);
		return webElement.getText().contains(text);
	}

	public Boolean isDisplayed() {
		return webElement.isDisplayed();
	}

	public T check(Predicate<WebElement> predicate) {
		status = predicate.apply(webElement);
		return (T) this;
	}

	public Boolean getStatus() {
		return this.status;
	}

	public T andThen(Runnable runnable) {
		if (status == true) {
			runnable.run();
		}
		return (T) this;
	}

	public T assess(Consumer<Boolean> consumer) {
		consumer.accept(status);
		return (T) this;
	}

	public Boolean verifyImage() {
		Object result = ((JavascriptExecutor) driverFunc.get()).executeScript("return arguments[0].complete && "
				+ "typeof arguments[0].naturalWidth != \"undefined\" && " + "arguments[0].naturalWidth > 0",
				webElement);

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
		String firstWindow = driverFunc.get().getWindowHandle();
		Set<String> allWindows = driverFunc.get().getWindowHandles();
		Iterator<String> windowsIterator = allWindows.iterator();

		while (windowsIterator.hasNext()) {
			String windowHandle = windowsIterator.next().toString();
			if (!windowHandle.equals(firstWindow)) {
				driverFunc.get().switchTo().window(windowHandle);
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
		return driverFunc.get().getTitle();
	}

	// Html5 video
	// it returns the total video duration time
	public double videoDuration() {
		double duration = 0;
		new WebDriverWait(driverFunc.get(), 10).until(webDriver -> ((JavascriptExecutor) webDriver)
				.executeScript("return arguments[0].duration", webElement));
		WebElement video = webElement;
		JavascriptExecutor js = (JavascriptExecutor) driverFunc.get();
		duration = (double) js.executeScript("return arguments[0].duration", video);
		return duration;
	}

	// it set the currentime in the video
	public T videoCurrentTime(double currentTime) {
		WebElement video = webElement;
		JavascriptExecutor js = (JavascriptExecutor) driverFunc.get();
		js.executeScript("return arguments[0].currentTime=" + currentTime, video);
		return (T) this;
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
