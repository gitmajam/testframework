package com.tribu.qaselenium.testframework.testbase;

import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {
	// singleton
	private volatile static DriverFactory driverFactory = null;
	private static int contador = 0; // test

	// private constructor
	private DriverFactory() {
		// check point created instances 
		contador = contador + 1;
		System.out.println("*****driverFactory-counter***** = " + contador);
	}

	public static DriverFactory getInstance() {
		if (driverFactory == null) {
			synchronized (DriverFactory.class) {
				if (driverFactory == null) {
					driverFactory = new DriverFactory();
				}
			}
		}
		return driverFactory;
	}
	
	private static ChromeOptions getChromeOptions() {
		ChromeOptions options = new ChromeOptions();
		// this capability enter accept button by default in chrome pop-ups
		options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
		if (PropertiesFile.getProperties("headless").equals("true")) {
			options.addArguments("--headless");
			options.addArguments("--window-size=1200,1100");
			options.addArguments("disable-gpu");
		}
		return options;
	}

	private static FirefoxOptions getFirefoxOptions() {
		FirefoxOptions options = new FirefoxOptions();
		options.addArguments("--headless");
		return options;
	}

	/**
	 * static variable that relates an specific webdriver instance with a thread, it
	 * is like a dictionary.
	 */

	private static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();

	public void createDriver(String browser) {
		browser.toLowerCase();

		switch (browser) {
		case "chrome":
			WebDriverManager.chromedriver().setup();
			driver.set(new ChromeDriver(getChromeOptions()));
			break;

		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			driver.set(new FirefoxDriver(getFirefoxOptions()));
			break;

		default:
			System.out.println("Do not know how to start: " + browser + ", starting chrome.");
			WebDriverManager.chromedriver().setup();
			driver.set(new ChromeDriver(getChromeOptions()));
			break;
		}
	}

	/** this method returns the driver related with the current thread */
	public WebDriver getDriver() {

		return driver.get();
	}
}
