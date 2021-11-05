package com.tribu.qaselenium.testframework.testbase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Supplier;
import com.tribu.qaselenium.testframework.pagebase.BasePO;
import com.tribu.qaselenium.testframework.pagebase.GUtils;

public class TestUtilities {

	
	protected String testSuiteName;
	protected String testName;
	protected String testMethodName;
	protected Logger log;
	
	protected Supplier<WebDriver> driver = () -> DriverFactory.getInstance().getDriver();
	

	// Static Sleep
	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public <T extends BasePO> Supplier<T> openUrl(Supplier<T> pageSupplier) {
		driver.get().get(pageSupplier.get().getPageUrl());
		GUtils.waitForPageToLoad(driver.get());
		return pageSupplier;
	}

	/** Take screenshot file png. return path */
	protected String takeScreenshot(String fileName) {
		File scrFile = ((TakesScreenshot) driver.get()).getScreenshotAs(OutputType.FILE);
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
			scrFileEncoded = ((TakesScreenshot) driver.get()).getScreenshotAs(OutputType.BASE64);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "data:image/png;base64," + scrFileEncoded;
	}

	/** Todays date in yyyyMMdd format */
	protected String getTodaysDate() {
		return "-" + new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	/** Current time in HHmmssSSS */
	protected String getSystemTime() {
		return "-" + new SimpleDateFormat("HHmmssSSS").format(new Date());
	}

}
