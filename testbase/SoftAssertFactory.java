package com.tribu.qaselenium.testframework.testbase;

import org.testng.asserts.SoftAssert;

public class SoftAssertFactory {
	// singleton
	private volatile static SoftAssertFactory softAssertFactory = null;
	private static int contador = 0; // temp

	// private constructor
	private SoftAssertFactory() {
		contador = contador + 1;// temp
	}

	public static SoftAssertFactory getInstance() {

		if (softAssertFactory == null) {
			synchronized (SoftAssertFactory.class) {
				if (softAssertFactory == null) {
					softAssertFactory = new SoftAssertFactory();
				}
			}
		}
		return softAssertFactory;
	}

	private static ThreadLocal<SoftAssert> softAssert = new ThreadLocal<SoftAssert>();

	public void createSoftAssert() {
		softAssert.set(new SoftAssert());
	}

	public SoftAssert getSoftAssert() {
		return softAssert.get();
	}
}
