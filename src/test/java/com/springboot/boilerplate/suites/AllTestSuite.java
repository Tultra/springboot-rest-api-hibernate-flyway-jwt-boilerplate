package com.springboot.boilerplate.suites;

import org.junit.platform.suite.api.ExcludePackages;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.springboot")
@ExcludePackages("com.springboot.suites")
public class AllTestSuite {
}
