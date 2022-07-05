package com.tribu.qaselenium.testframework.testbase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import com.google.common.base.Supplier;
import com.tribu.qaselenium.testframework.pagebase.BasePO;
import com.tribu.qaselenium.testframework.utilities.DataProviders;
import com.tribu.qaselenium.testframework.utilities.DataReaders;
import com.tribu.qaselenium.testframework.utilities.WaitUtils;

public class TestUtilities {

	protected String testSuiteName;
	protected String testName;
	protected String testMethodName;
	protected Logger log;
	protected String dataProviderFilePath;
	protected String language = PropertiesFile.getProperties("language");
	protected Map<String, String> dictionary = language != null ? DataReaders.csvDictionaryReader(language) : null;
	protected Supplier<WebDriver> driverFunc = () -> DriverFactory.getInstance().getDriver();
	protected Supplier<SoftAssert> softAssertSupplier = () -> SoftAssertFactory.getInstance().getSoftAssert();

	public String getDataProviderFilePath() {
		return dataProviderFilePath;
	}

	// Static Sleep
	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public <T extends BasePO<T>> Supplier<T> openUrl(Supplier<T> pageSupplier) {
		log.info("opening url : " + pageSupplier.get().getPageUrl());
		driverFunc.get().get(pageSupplier.get().getPageUrl());
		WaitUtils.waitForPageToLoad();
		return pageSupplier;
	}

	// open an url with a delay after
	public <T extends BasePO<T>> Supplier<T> openUrl(Supplier<T> pageSupplier, long delay) {
		log.info("opening url : " + pageSupplier.get().getPageUrl());
		driverFunc.get().get(pageSupplier.get().getPageUrl());
		WaitUtils.waitForPageToLoad();
		sleep(delay);
		return pageSupplier;
	}

	/** Take screenshot file png. return path */
	protected String takeScreenshot(String fileName) {
		File scrFile = ((TakesScreenshot) driverFunc.get()).getScreenshotAs(OutputType.FILE);
		String path = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "screenshots"
				+ File.separator + getTodaysDate() + File.separator + testSuiteName + File.separator + testName
				+ File.separator + testMethodName + File.separator + getSystemTime() + " " + fileName + ".png";
		try {
			FileUtils.copyFile(scrFile, new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	/** Take screenshot base64, return file encoded */
	protected String takeScreenshot() {
		String scrFileEncoded = "";
		try {
			scrFileEncoded = ((TakesScreenshot) driverFunc.get()).getScreenshotAs(OutputType.BASE64);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "data:image/png;base64," + scrFileEncoded;
	}

	/** Todays date in yyyyMMdd format */
	protected String getTodaysDate() {
		return "-" + new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	/** Todays date plus days */
	protected String getDatePlus(int days) {
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, days);
		dt = c.getTime();
		return new SimpleDateFormat("MM/dd/yyyy").format(dt);
	}

	protected String getCurrentYear() {
		return "-" + new SimpleDateFormat("yyyy").format(new Date());
	}

	/** Current time in HHmmssSSS */
	protected String getSystemTime() {
		return "-" + new SimpleDateFormat("HHmmssSSS").format(new Date());
	}

	// provide credentials from the credentials csv file at the default path
	public Map<String, String> readCredentials(String profile) {
		String credentialsPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "providerFiles" + File.separator + "credentials.csv";
		Iterator<Map<String, String>> dataSet;
		dataSet = DataProviders.csvReader(credentialsPath);
		Map<String, String> dataMap = null;
		while (dataSet.hasNext()) {
			dataMap = dataSet.next();
			if (dataMap.get("profile").equals(profile)) {
				if (dataMap.get("environment").equals(PropertiesFile.getProperties("env"))) {
					return dataMap;
				}
			}
		}
		return null;
	}

}
