package com.sg.kata.service;

import com.sg.kata.domain.BankTransaction;
import com.sg.kata.domain.enumeration.TransactionType;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

public class BankTransactionWithBalance {

    private BankTransaction bankTransaction;
    private BigDecimal balance;

    public BankTransactionWithBalance(BankTransaction bankTransaction, BigDecimal balance) {
        this.bankTransaction = bankTransaction;
        this.balance = balance;
    }

    public BankTransaction getBankTransaction() {
        return bankTransaction;
    }

    public void setBankTransaction(BankTransaction bankTransaction) {
        this.bankTransaction = bankTransaction;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        String blank20x = StringUtils.repeat(' ', 15);
        String amount = StringUtils.leftPad(bankTransaction.getAmount().toString(), 15);
        return StringUtils.joinWith(
            " | ",
            bankTransaction.getValueDate().format(DateTimeFormatter.ISO_DATE),
            StringUtils.rightPad(bankTransaction.getLabel(), 50),
            TransactionType.DEBIT.equals(bankTransaction.getType()) ? amount : blank20x,
            TransactionType.CREDIT.equals(bankTransaction.getType()) ? amount : blank20x,
            StringUtils.leftPad(balance.toString(), 15)
        );
    }
}
