package com.tribu.qaselenium.testframework.testbase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestLoggerFactory {
	// singleton
	private volatile static TestLoggerFactory loggerFactory = null;
	private static int contador = 0; // temp

	// private constructor
	private TestLoggerFactory() {
		contador = contador + 1;// temp
		System.out.println("*****loggerFactory-counter***** = " + contador);// temp
	}

	public static TestLoggerFactory getInstance() {

		if (loggerFactory == null) {
			synchronized (TestLoggerFactory.class) {
				if (loggerFactory == null) {
					loggerFactory = new TestLoggerFactory();
				}
			}
		}
		return loggerFactory;
	}

	private static ThreadLocal<Logger> log = new ThreadLocal<Logger>();

	public void createLogger(String testName) {
		log.set(LogManager.getLogger(testName));
	}

	public Logger getLogger() {
		return log.get();
	}
}
