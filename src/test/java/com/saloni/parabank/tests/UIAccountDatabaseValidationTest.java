package com.saloni.parabank.tests;

import com.saloni.parabank.base.DriverFactory;
import com.saloni.parabank.database.AccountQueries;
import com.saloni.parabank.pages.AccountsOverviewPage;
import com.saloni.parabank.pages.LoginPage;
import com.saloni.parabank.utils.ConfigReader;

import org.openqa.selenium.WebDriver;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import com.saloni.parabank.listeners.FailureScreenshotListener;
import com.saloni.parabank.listeners.HtmlReportListener;
import org.testng.annotations.Listeners;

@Listeners({
    FailureScreenshotListener.class,
    HtmlReportListener.class
})
public class UIAccountDatabaseValidationTest {

    private WebDriver driver;

    @BeforeMethod
    public void setup() {

        driver =
                DriverFactory.initializeDriver();

        driver.get(
                ConfigReader.get("baseUrl")
        );
    }

    @Test
    public void verifyUIBalanceMatchesDatabase() {

        int accountId = 13566;

        LoginPage loginPage =
                new LoginPage(driver);

        AccountsOverviewPage accountsPage =
                loginPage.login(
                        ConfigReader.get("username"),
                        ConfigReader.get("password")
                );

        Assert.assertTrue(
                accountsPage.isLoaded(),
                "Accounts Overview page did not load."
        );

        BigDecimal uiBalance =
                accountsPage.getBalance(accountId);

        BigDecimal databaseBalance =
                AccountQueries
                        .getAccountBalance(accountId);

        System.out.println(
                "UI Balance: " + uiBalance
        );

        System.out.println(
                "Database Balance: "
                        + databaseBalance
        );

        Assert.assertEquals(
                uiBalance.compareTo(databaseBalance),
                0,
                "UI and database balances do not match."
        );
    }

    @AfterMethod
    public void tearDown() {

        DriverFactory.quitDriver();
    }
}