package com.tribu.qaselenium.testframework.testbase;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {
	// singleton
	private volatile static DriverFactory driverFactory = null;
	private static int contador = 0; // temp

	// private constructor
	private DriverFactory() {
		contador = contador + 1;// temp
		System.out.println("*****driverFactory-counter***** = " + contador);// temp
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
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--no-sandbox");// Bypass OS security models
		// options.addArguments("--headless");
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

	public void createDriver(String browser, Logger log) {
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
		log.info(driver.get().hashCode() + " driverFactory crea driver");
	}

	/** this method returns the driver related with the current thread */
	public WebDriver getDriver() {
		return driver.get();
	}
}
