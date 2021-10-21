package com.tribu.seleniumframework.pages;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.tribu.seleniumframework.utilities.GeneralUtils;


public class BasePageObject {
	
	protected Logger log;
	
	public BasePageObject(Logger log) {
		this.log = log;
		log.info(" Constructor BasePageObject");
	}
		 
	/** Open page with given URL **/
	protected void openUrl(WebDriver driver, String url) {
		log.info(driver.hashCode() + " Welcome abre url");
		driver.get(url);
		GeneralUtils.waitForPageToLoad(driver, log);
	}

	// Click on element with given locator when its visible
	protected void click(WebDriver driver,By locator) {
		GeneralUtils.waitForVisibilityOf(locator, 5, driver);
		driver.findElement(locator).click();
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
}
