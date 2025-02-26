package com.notification.suites;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
    "com.notification.service.delivery.sms",
    "com.notification.integration"
})
@IncludeClassNamePatterns({".*Sms.*Test"})
public class SmsTests {
    // This is a placeholder for the JUnit test suite
} 