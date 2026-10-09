package com.saloni.parabank.diagnostics;

import com.saloni.parabank.base.DriverFactory;
import com.saloni.parabank.listeners.FailureScreenshotListener;
import com.saloni.parabank.utils.ConfigReader;

import com.saloni.parabank.listeners.HtmlReportListener;
import org.testng.annotations.Listeners;

import org.openqa.selenium.WebDriver;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
@Listeners({
    FailureScreenshotListener.class,
    HtmlReportListener.class
})
public class ScreenshotListenerSmokeTest {

    @BeforeMethod
    public void setup() {

        WebDriver driver =
                DriverFactory.initializeDriver();

        driver.get(
                ConfigReader.get("baseUrl")
        );
    }

    @Test
    public void intentionallyFailToVerifyScreenshot() {

        Assert.fail(
                "Intentional diagnostic failure "
                        + "to verify screenshot capture."
        );
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        DriverFactory.quitDriver();
    }
}