package com.saloni.parabank.tests;

import com.saloni.parabank.api.BankActionsApi;
import com.saloni.parabank.database.TransactionQueries;
import com.saloni.parabank.listeners.FailureScreenshotListener;
import com.saloni.parabank.listeners.HtmlReportListener;
import com.saloni.parabank.support.AdditionalCoverageBase;

import java.math.BigDecimal;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({
        FailureScreenshotListener.class,
        HtmlReportListener.class
})
public class ApiDepositDatabaseValidationTest
        extends AdditionalCoverageBase {

    @Test
    public void verifyApiDepositAcrossDatabaseAndUI() {

        BigDecimal amount = new BigDecimal("25.00");

        BigDecimal before = startingBalance(PRIMARY);
        BigDecimal otherBefore = startingBalance(SECONDARY);

        int previousId =
                TransactionQueries.getLatestTransactionId(PRIMARY);

        int otherPreviousId =
                TransactionQueries.getLatestTransactionId(SECONDARY);

        new BankActionsApi().deposit(PRIMARY, amount);

        balances(
                Map.of(
                        PRIMARY, before.add(amount),
                        SECONDARY, otherBefore
                )
        );

        transaction(PRIMARY, previousId, "0", amount);

        Assert.assertTrue(
                TransactionQueries.getTransactionsAfter(
                        SECONDARY, otherPreviousId
                ).isEmpty(),
                "Deposit created a transaction in the other account."
        );
    }
}