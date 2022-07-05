package com.tribu.qaselenium.testframework.utilities;

import org.openqa.selenium.SearchContext;

import com.google.common.base.Function;

public interface CheckedCondition<T> extends Function<SearchContext, T> {
}
