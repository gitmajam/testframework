package com.tribu.qaselenium.testframework.pagebase;

import org.openqa.selenium.SearchContext;

import com.google.common.base.Function;

public interface CheckedCondition<T> extends Function<SearchContext, T> {
}
