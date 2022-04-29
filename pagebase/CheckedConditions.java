package com.tribu.qaselenium.testframework.pagebase;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
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

	public static CheckedCondition<List<WebElement>> visibilityOfAllElementLocatedByFilter(final By locator,
			List<Predicate<WebElement>> predicateList) {
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
	 * @param locator   used to find the element
	 * @param predicate used to filter the search
	 * @return the WebElement once it is located and visible
	 */
	public static CheckedCondition<WebElement> visibilityOfElementLocatedByFilter(final By locator,
			final List<Predicate<WebElement>> predicateList) {
		return new CheckedCondition<WebElement>() {
			@Override
			public WebElement apply(SearchContext context) {
				try {
					List<WebElement> elementList = context.findElements(locator);
					predicateList.add(e -> e.isDisplayed());
					predicateList.forEach(predicate -> elementList.removeIf(predicate.negate()));
					return elementList.stream().findFirst().orElse(null);
				} catch (NoSuchElementException e) {
					log.info("NoSuchElementException");
					return null;
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
	 * @param locator   used to find the element
	 * @param predicate used to filter the search
	 * @return the WebElement once it is located and visible
	 */
	
	public static CheckedCondition<Boolean> invisibilityOfElementLocatedByFilter(final By locator,
			final List<Predicate<WebElement>> predicateList) {
		return new CheckedCondition<Boolean>() {
			@Override
			public Boolean apply(SearchContext context) {
				List<WebElement> elementList = context.findElements(locator);
				try {
					predicateList.forEach(predicate -> elementList.removeIf(predicate.negate()));
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