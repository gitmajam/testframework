package com.tribu.qaselenium.testframework.testbase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.google.common.base.Predicate;

/**
 * Canned {@link ExpectedCondition}s which are generally useful within webdriver
 * tests.
 */
public class CheckedConditions {

	private final static Logger log = Logger.getLogger(CheckedConditions.class.getName());

	private CheckedConditions() {
		// Utility class
	}

	/**
	 * this Class let us to find a webelement from a WebDriver or a WebElement
	 * context
	 * 
	 * @param locator used to find the element
	 * @return the list of WebElements once they are located
	 */
	public static CheckedCondition<List<WebElement>> presenceOfAllElementsLocatedBy(final By locator) {
		return new CheckedCondition<List<WebElement>>() {
			@Override
			public List<WebElement> apply(SearchContext context) {
				List<WebElement> elements = context.findElements(locator);
				return elements.size() > 0 ? elements : null;
			}

			@Override
			public String toString() {
				return "presence of any elements located by " + locator;
			}
		};
	}

	/**
	 * @param locator used to find the element
	 * @return the list of WebElements once they are located
	 */
	public static CheckedCondition<List<WebElement>> visibilityOfAllElementLocatedBy(final By locator) {
		return new CheckedCondition<List<WebElement>>() {
			@Override
			public List<WebElement> apply(SearchContext context) {
				List<WebElement> elements = context.findElements(locator);
				elements = elements.stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
				return elements.size() > 0 ? elements : null;
			}

			@Override
			public String toString() {
				return "visibility of all elements located by " + locator;
			}
		};
	}

	/**
	 * @param locator used to find the element
	 * @return the WebElement once it is located and visible
	 */
	public static CheckedCondition<WebElement> visibilityOfElementLocated(final By locator) {
		return new CheckedCondition<WebElement>() {
			@Override
			public WebElement apply(SearchContext context) {
				try {
					return elementIfVisible(context.findElement(locator));
				} catch (StaleElementReferenceException e) {
					return null;
				}
			}

			@Override
			public String toString() {
				return "visibility of element located by " + locator;
			}
		};
	}

	/**
	 * @return the given element if it is visible and has non-zero size, otherwise
	 *         null.
	 */
	private static WebElement elementIfVisible(WebElement element) {
		return element.isDisplayed() ? element : null;
	}
}