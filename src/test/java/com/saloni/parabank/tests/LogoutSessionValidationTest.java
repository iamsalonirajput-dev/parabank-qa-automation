package com.saloni.parabank.tests;

import com.saloni.parabank.api.ApiValidation;
import com.saloni.parabank.database.AccountQueries;
import com.saloni.parabank.database.TransactionQueries;
import com.saloni.parabank.listeners.FailureScreenshotListener;
import com.saloni.parabank.listeners.HtmlReportListener;
import com.saloni.parabank.pages.AccountsOverviewPage;
import com.saloni.parabank.pages.LoginPage;
import com.saloni.parabank.support.AdditionalCoverageBase;
import com.saloni.parabank.utils.ConfigReader;

import java.math.BigDecimal;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({
        FailureScreenshotListener.class,
        HtmlReportListener.class
})
public class LogoutSessionValidationTest
        extends AdditionalCoverageBase {

    @Test
    public void verifyLogoutBlocksAccountOverviewAndAllowsRelogin() {

        BigDecimal before = startingBalance(PRIMARY);
        BigDecimal otherBefore = startingBalance(SECONDARY);

        int previous =
                TransactionQueries.getLatestTransactionId(PRIMARY);

        int otherPrevious =
                TransactionQueries.getLatestTransactionId(SECONDARY);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.linkText("Log Out")
                )
        ).click();

        assertLoggedOut();

        driver.get(
                ConfigReader.get("baseUrl")
                        .replaceAll("/+$", "")
                        + "/overview.htm"
        );

        assertLoggedOut();

        driver.navigate().refresh();
        assertLoggedOut();

        AccountsOverviewPage overview =
                new LoginPage(driver).login(
                        ConfigReader.get("username"),
                        ConfigReader.get("password")
                );

        Assert.assertTrue(
                overview.isLoaded(),
                "Relogin failed."
        );

        money(
                overview.getBalance(PRIMARY),
                before,
                "Primary UI after logout/relogin"
        );

        money(
                overview.getBalance(SECONDARY),
                otherBefore,
                "Secondary UI after logout/relogin"
        );

        money(
                AccountQueries.getAccountBalance(PRIMARY),
                before,
                "Primary DB after logout"
        );

        money(
                AccountQueries.getAccountBalance(SECONDARY),
                otherBefore,
                "Secondary DB after logout"
        );

        ApiValidation.assertBalance(PRIMARY, before);
        ApiValidation.assertBalance(SECONDARY, otherBefore);

        Assert.assertTrue(
                TransactionQueries.getTransactionsAfter(
                        PRIMARY, previous
                ).isEmpty(),
                "Logout created transactions."
        );

        Assert.assertTrue(
                TransactionQueries.getTransactionsAfter(
                        SECONDARY, otherPrevious
                ).isEmpty(),
                "Logout created transactions."
        );

        Reporter.log(
                "Validated logout, direct overview access, "
                        + "refresh and relogin; funds unchanged.",
                true
        );
    }

    private void assertLoggedOut() {

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("username")
                )
        );

        Assert.assertTrue(
                driver.findElements(
                        By.linkText("Log Out")
                ).stream().noneMatch(WebElement::isDisplayed),
                "Authenticated navigation remains visible after logout."
        );

        Assert.assertTrue(
                driver.findElements(
                        By.xpath(
                                "//h1[normalize-space()='Accounts Overview']"
                        )
                ).stream().noneMatch(WebElement::isDisplayed),
                "Account overview is accessible after logout."
        );

        Assert.assertTrue(
                driver.findElements(
                        By.id("accountTable")
                ).stream().noneMatch(WebElement::isDisplayed),
                "Account table is visible after logout."
        );
    }
}