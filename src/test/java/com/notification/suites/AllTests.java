package com.notification.suites;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
    "com.notification.domain",
    "com.notification.config",
    "com.notification.service",
    "com.notification.web",
    "com.notification.integration"
})
public class AllTests {
    // This is a placeholder for the JUnit test suite
} 