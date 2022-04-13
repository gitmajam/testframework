package com.tribu.qaselenium.testframework.testbase;

import org.openqa.selenium.SearchContext;

import com.google.common.base.Function;

public interface CheckedCondition<T> extends Function<SearchContext, T> {
}
