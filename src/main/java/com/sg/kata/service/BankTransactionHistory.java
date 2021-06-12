package com.sg.kata.service;

import com.sg.kata.domain.enumeration.TransactionType;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class BankTransactionHistory {

    List<BankTransactionWithBalance> transactionsWithBalance;

    public BankTransactionHistory(List<BankTransactionWithBalance> transactionsWithBalance) {
        this.transactionsWithBalance = transactionsWithBalance;
    }

    @Override
    public String toString() {
        String header = StringUtils.joinWith(
            " | ",
            StringUtils.rightPad("Date", 10),
            StringUtils.rightPad("Description", 50),
            StringUtils.rightPad("Debit", 15),
            StringUtils.rightPad("Credit", 15),
            StringUtils.rightPad("Balance", 15)
        );
        String headerSeparator = StringUtils.repeat("-", 117);
        return (
            header +
            System.lineSeparator() +
            headerSeparator +
            System.lineSeparator() +
            StringUtils.join(transactionsWithBalance.toArray(), System.lineSeparator())
        );
    }
}
