package com.tribu.qaselenium.testframework.pagebase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Optional;
import org.testng.asserts.SoftAssert;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.tribu.qaselenium.pages.socios.SMHomeP;
import com.tribu.qaselenium.testframework.testbase.DriverFactory;
import com.tribu.qaselenium.testframework.testbase.SoftAssertFactory;
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
	protected Supplier<SoftAssert> softAssertSupplier = () -> SoftAssertFactory.getInstance().getSoftAssert();
	protected String resourcesPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
			+ File.separator + "resources" + File.separator;

	/*
	 * this method sets the work webElemen, if receives a predicate as argument this
	 * one is put into filter stream for search a specific webElement, else the
	 * method executes findElements without any filter , only verifies if the
	 * element is in the DOM.
	 */
	protected void setWebElement(By by, Predicate<WebElement>... predicates) {
		predicatesElementList.clear();
		Collections.addAll(predicatesElementList, predicates);
		if (!(webElement == null && by.toString().contains(" ./"))) {
			if (by.toString().contains(" ./") && baseElement == null) {
				baseElement = webElement;
				searchContext = webElement;
			} else if (by.toString().contains(" ./") && baseElement != null) {
				searchContext = baseElement;
			} else {
				baseElement = null;
				searchContext = driverFunc.get();
			}
			this.locator = by;
			webElement = GUtils.waitForVisibilityByfilter(locator, searchContext, predicatesElementList);
		}
	}

	protected T setWebElement(WebElement element) {
		this.webElement = element;
		return (T) this;
	}

	public T waitForNotVisibility() {
		Boolean isInvisible = GUtils.waitForInvisibilityByfilter(locator, searchContext, predicatesElementList);
		if (isInvisible == true) {
			this.webElement = null;
		} else if (isInvisible == false) {
			log.info("webElement is still visible");
		}
		return (T) this;
	}

	public T waitForNotPresence() {
		Boolean isNotPresent = GUtils.waitForNotPresenceByfilter(locator, searchContext, predicatesElementList);
		if (isNotPresent == true) {
			this.webElement = null;
		} else if (isNotPresent == false) {
			log.info("webElement is still present");
		}

		return (T) this;
	}

	// wait for an upload file or other time-loading feature
	public T waitForLoad() {
		this.webElement = GUtils.waitForLoad(locator, searchContext, predicatesElementList, 15L);
		return (T) this;
	}

	// wait for an upload file or other time-loading feature
	public T waitLoadImg() {
		GUtils.waitLoadImage(this.webElement, searchContext, 5L);
		return (T) this;
	}

	public By getLocator() {
		return this.locator;
	}

	public WebElement getBaseElement() {
		return baseElement;
	}

	public T setBaseElement() {
		baseElement = this.webElement;
		return (T) this;
	}

	public WebElement getWebElement() {
		return this.webElement;
	}

	public T getParent() {
		setWebElement(By.xpath("./.."));
		baseElement = webElement;
		return (T) this;
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

	// click with delay after
	public T click(Integer miliSeconds) {
		click();
		try {
			Thread.sleep(miliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return (T) this;
	}

	// this click method returns a supplier
	public <R> Supplier<R> click(Supplier<R> pageSupplier, Integer... delays) {
		Integer delay = delays.length > 0 ? delays[0] : null;
		if (delay != null) {
			click(delay);
		} else {
			click();
		}
		return pageSupplier;
	}

	public T refresh() {
		driverFunc.get().navigate().refresh();
		GUtils.waitForPageToLoad();
		return (T) this;
	}

	// place (srcoll) de element at center of the viewport, with delat after center
	// in miliseconds
	public T centerElement(Integer... delays) {
		Integer delay = delays.length > 0 ? delays[0] : 100;
		String centerElement = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
				+ "var elementTop = arguments[0].getBoundingClientRect().top;"
				+ "window.scrollBy(0, elementTop-(viewPortHeight/2));";
		((JavascriptExecutor) driverFunc.get()).executeScript(centerElement, webElement);
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
			this.webElement = GUtils.waitForEnableStatusByfilter(locator, searchContext, predicateList);
			try {
				this.webElement.sendKeys(text);
				log.info("Element found only with isEnable filter");
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

	public T assertExist(String... failText) {
		String text = failText.length > 0 ? failText[0] : "";
		String message = webElement != null ? "Element found " + text : "Element was not found " + text;
		softAssertSupplier.get().assertTrue(webElement != null, message + ", " + this.locator.toString());
		return (T) this;
	}

	public T assertNotExist(String... failText) {
		String text = failText.length > 0 ? failText[0] : "";
		String message = webElement != null ? "Element found " + text : "Element was not found " + text;
		softAssertSupplier.get().assertFalse(webElement != null, message + ", " + this.locator.toString());
		return (T) this;
	}

	public T assertDownload(String fileName, String... downloadPath) {
		String path = downloadPath.length > 0 ? downloadPath[0] : System.getProperty("user.dir");
		softAssertSupplier.get().assertTrue(this.isFileDownloaded(path, fileName),
				"file " + fileName + " was not downloaded");
		return (T) this;
	}

	public T assertImgDisplayed(String... failText) {
		String text = failText.length > 0 ? failText[0] : "";
		softAssertSupplier.get()
				.assertTrue((Boolean) ((JavascriptExecutor) this.driverFunc.get()).executeScript(
						"return arguments[0].complete && " + "typeof arguments[0].naturalWidth != 'undefined' && "
								+ "arguments[0].naturalWidth > 0",
						this.webElement), text);
		return (T) this;
	}

	@Deprecated
	public T assess(BiConsumer<Boolean, String> consumer, String... resultText) {
		String text = resultText.length > 0 ? resultText[0] : "";
		String message = webElement != null ? "Element found " + text : "Element was not found " + text;
		consumer.accept(webElement != null, message + ", " + this.locator.toString());
		return (T) this;
	}

	public T assertAll(String... resultText) {
		String text = resultText.length > 0 ? resultText[0] : "";
		softAssertSupplier.get().assertAll(text);
		return (T) this;
	}

	private boolean isFileDownloaded(String downloadPath, String fileName) {
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
				return flag = true;
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

	/*
	 * this method retrieve the table headers by joining the headers that there are
	 * in the table
	 */
	public List<String> readTableHeaders() {
		List<WebElement> headerList = this.webElement.findElements(By.xpath(".//thead"));
		Iterator<WebElement> headerListIt = headerList.stream().filter(e -> e.isDisplayed())
				.collect(Collectors.toList()).iterator();
		List<String> joinedHeaders = new ArrayList<String>();
		while (headerListIt.hasNext()) {
			Iterator<WebElement> columnHeaderList = headerListIt.next().findElements(By.xpath(".//tr/*")).iterator();
			List<String> columHeaders = new ArrayList<String>();
			while (columnHeaderList.hasNext()) {
				WebElement element = columnHeaderList.next();
				Integer colspan = Integer
						.parseInt(element.getAttribute("colspan") == null ? "1" : element.getAttribute("colspan"));
				for (int i = 0; i < colspan; ++i) {
					columHeaders.add(element.getText().trim());
				}
			}
			if (joinedHeaders.isEmpty()) {
				joinedHeaders.addAll(columHeaders);
			} else {
				for (int i = 0; i < joinedHeaders.size(); ++i) {
					joinedHeaders.set(i, joinedHeaders.get(i).concat(" ").concat(columHeaders.get(i)));
				}
			}
		}
		return joinedHeaders;
	}

	/*
	 * this method retrieve the table headers and body storing it in a list of maps
	 */
	public List<Map<String, String>> readTable() {
		List<String> headerList = readTableHeaders();
		List<WebElement> rowList = this.webElement.findElements(By.xpath(".//tbody/tr"));
		Iterator<WebElement> rowListIt = rowList.stream().filter(e -> e.isDisplayed()).collect(Collectors.toList())
				.iterator();
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		while (rowListIt.hasNext()) {
			Iterator<WebElement> rowDataListIt = rowListIt.next().findElements(By.xpath(".//*")).iterator();
			Map<String, String> dataMap = new HashMap<String, String>();
			List<String> dataRowList = new ArrayList<String>();
			while (rowDataListIt.hasNext()) {
				dataRowList.add(rowDataListIt.next().getText().trim());
			}
			for (int i = 0; i < headerList.size(); ++i) {
				dataMap.put(headerList.get(i), dataRowList.get(i));
			}
			dataList.add(dataMap);
		}
		return dataList;
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
