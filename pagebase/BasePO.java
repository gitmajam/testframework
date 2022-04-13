package com.tribu.qaselenium.testframework.pagebase;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.StaleElementReferenceException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.tribu.qaselenium.testframework.testbase.DriverFactory;
import com.tribu.qaselenium.testframework.testbase.TestLoggerFactory;

public abstract class BasePO<T> {

	protected By locator; // locator work variable
	private WebElement webElement; // last selected element
	private SearchContext searchContext; // Page title for switch to this page
	protected String PTitle;
	protected WebElement baseElement;
	protected List<Predicate<WebElement>> predicatesElementList = new ArrayList<Predicate<WebElement>>();
	protected Logger log = TestLoggerFactory.getInstance().getLogger();
	protected Supplier<WebDriver> driverFunc = () -> DriverFactory.getInstance().getDriver();
	protected String resourcesPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator;

	/*
	 * this method sets the work webElemen, if receives a predicate as argument this
	 * one is put into filter stream for search a specific webElement, else the
	 * method executes find Elements without any filter , only verifies if the
	 * element is in the DOM.
	 */
	protected void setWebElement(By by, Predicate<WebElement>[] predicates) {
		if (by.toString().contains(" .//") && (searchContext == driverFunc.get() || searchContext == null)) {
			searchContext = webElement;
		} else if (!(by.toString().contains(" .//")) && searchContext != driverFunc.get()) {
			searchContext = driverFunc.get();
		}
		Collections.addAll(predicatesElementList, predicates);
		List<Predicate<WebElement>> predicateList = new ArrayList<Predicate<WebElement>>();
		predicateList.add(e -> e.isEnabled());
		predicateList.add(e -> e.isDisplayed());
		Collections.addAll(predicateList, predicates);
		this.locator = by;
		webElement = waitForElementSearch(predicateList);
	}

	public WebElement waitForElementSearch(List<Predicate<WebElement>> predicateList) {
		Sleeper sleeper = requireNonNull(Sleeper.SYSTEM_SLEEPER);
		Clock clock = requireNonNull(Clock.systemDefaultZone());
		Duration timeout = Duration.ofMillis(3000);
		Duration interval = Duration.ofMillis(500);
		Instant end = clock.instant().plus(timeout);
		WebElement element = null;
		while (true) {
			try {
				element = visibilityOfElementWithFilter.apply(predicateList);
				if (element != null) {
					return element;
				}
			} catch (StaleElementReferenceException e) {
				e.printStackTrace();
			}
			if (end.isBefore(clock.instant())) {
				log.info("timeout, webElemnt not found : " + this.locator);
				return element;
			}
			try {
				sleeper.sleep(interval);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}

	private Function<List<Predicate<WebElement>>, WebElement> visibilityOfElementWithFilter = (l) -> {
		List<WebElement> elementList = this.searchContext.findElements(this.locator);
		try {
			l.forEach(predicate -> elementList.removeIf(predicate.negate()));
		} catch (NoSuchElementException e) {
			log.info("NoSuchElementException");
			return null;
		} catch (StaleElementReferenceException e) {
			return null;
		}
		return elementList.stream().findFirst().orElse(null);
	};

	public T waitForNotVisibility() {
		String message;
		if (webElement != null) {
			message = waitForAbsentElement(this.predicatesElementList) ? "webElement is absent"
					: "webElement is not absent";
		} else {
			message = "webElement is absent";
		}
		log.info(message);
		return (T) this;
	}

	public Boolean waitForAbsentElement(List<Predicate<WebElement>> predicateList) {
		Sleeper sleeper = requireNonNull(Sleeper.SYSTEM_SLEEPER);
		Clock clock = requireNonNull(Clock.systemDefaultZone());
		Duration timeout = Duration.ofMillis(4000);
		Duration interval = Duration.ofMillis(500);
		Instant end = clock.instant().plus(timeout);
		while (true) {
			if (invisibilityOfElementWithFilter.apply(predicateList) == true) {
				this.webElement = null;
				return true;
			}
			if (end.isBefore(clock.instant())) {
				log.info("timeout, webElement is still present : " + this.locator);
				return false;
			}
			try {
				sleeper.sleep(interval);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}

	private Function<List<Predicate<WebElement>>, Boolean> invisibilityOfElementWithFilter = (l) -> {
		List<WebElement> elementList = driverFunc.get().findElements(this.locator);
		try {
			l.forEach(predicate -> elementList.removeIf(predicate.negate()));
			return elementList.size() == 0 ? true : false;
		} catch (NoSuchElementException e) {
			log.info("NoSuchElementException");
			// Returns true because the element is not present in DOM. The
			// try block checks if the element is present but is invisible.
			return true;
		} catch (StaleElementReferenceException err) {
			// Returns true because stale element reference implies that element
			// is no longer visible.
			return true;
		}
	};

	public By getLocator() {
		return this.locator;
	}

	public WebElement getWebElement() {
		return this.webElement;
	}

	// simple click method without return's supplier object
	public T click() {
		try {
			webElement.click();
		} catch (Exception e) {
			JavascriptExecutor executor = (JavascriptExecutor) driverFunc.get();
			executor.executeScript("arguments[0].click();", webElement);
			log.info("click por javascript : " + this.locator);
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

	// Clear given text into element with given locator
	public T clear() {
		webElement.clear();
		return (T) this;
	}

	// Type given text into element with given locator
	public T type(String text) {
		try {
			this.webElement.sendKeys(text);
		} catch (Exception e) {
			List<Predicate<WebElement>> predicateList = new ArrayList<Predicate<WebElement>>();
			this.webElement = waitForElementSearch(predicateList);
			try {
				this.webElement.sendKeys(text);
				log.info("Element found without isDisplayed filter");
			} catch (Exception error) {
				e.printStackTrace();
			}
		}
		return (T) this;
	}

	// type with delay before
	public T type(String text, Integer miliSeconds) {
		try {
			Thread.sleep(miliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		type(text);
		return (T) this;
	}

	public T hoverElement() {
		Actions actions = new Actions(driverFunc.get());
		actions.moveToElement(webElement).perform();
		return (T) this;
	}

	// slider element
	public T moveSlider(int times, String key) {
		switch (key) {
		case "LEFT":
			for (int i = 0; i < times; i++) {
				webElement.sendKeys(Keys.ARROW_LEFT);
			}
			break;
		case "RIGHT":
			for (int i = 0; i < times; i++) {
				webElement.sendKeys(Keys.ARROW_RIGHT);
			}
			break;
		case "STAY":
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
				ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(this.locator, currentText)));
		return (T) this;
	}

	public T swichToFrame() {
		if (webElement == null) {
			log.info("frame webelement not found :" + this.locator);
		}
		driverFunc.get().switchTo().frame(webElement);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

	public String getCurrentUrl() {
		return driverFunc.get().getCurrentUrl();
	}

	public abstract String getPageUrl();

	/* asserts */

	public T ifFoundOrElse(Runnable runFound, Runnable runElse) {
		if (webElement != null && runFound != null) {
			runFound.run();
		} else if (webElement == null && runElse != null) {
			runElse.run();
		}
		return (T) this;
	}

	public T exec(Runnable runnable) {
		runnable.run();
		return (T) this;
	}

	public T assess(BiConsumer<Boolean, String> consumer, String... resultText) {
		String text = resultText.length > 0 ? resultText[0] : null;
		String message = webElement != null ? "Element found " + text : "Element was not found " + text;
		consumer.accept(webElement != null, message + ", " + this.locator.toString());
		return (T) this;
	}

	public Boolean verifyImage() {
		Object result = ((JavascriptExecutor) driverFunc.get()).executeScript("return arguments[0].complete && "
				+ "typeof arguments[0].naturalWidth != \"undefined\" && " + "arguments[0].naturalWidth > 0",
				webElement);
		return result instanceof Boolean ? (Boolean) result : false;
	}
	
	public boolean isFileDownloaded(String downloadPath, String fileName) {
		boolean flag = false;
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    File dir = new File(downloadPath);
	    File[] dir_contents = dir.listFiles();
	    for (int i = 0; i < dir_contents.length; i++) {
	        if (dir_contents[i].getName().contains(fileName))
	            return flag=true;
	            }
	    return flag;
	}

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
