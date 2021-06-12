package com.sg.kata.service;

import com.sg.kata.domain.BankAccount;
import com.sg.kata.domain.BankTransaction;
import com.sg.kata.domain.enumeration.TransactionType;
import com.sg.kata.repository.BankAccountRepository;
import com.sg.kata.repository.BankTransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankAccountService {

    private final BankTransactionRepository bankTransactionRepository;
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountService(BankTransactionRepository bankTransactionRepository, BankAccountRepository bankAccountRepository) {
        this.bankTransactionRepository = bankTransactionRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public BankTransaction makeDeposit(String accountOwner, BigDecimal amount) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findBankAccountByOwnerLogin(accountOwner);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException("Bank account with owner login '" + accountOwner + "' not found");
        }
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setAccount(bankAccount);
        bankTransaction.setAmount(amount.setScale(2, RoundingMode.HALF_EVEN));
        bankTransaction.setType(TransactionType.CREDIT);
        bankTransaction.setValueDate(LocalDate.now());
        BankTransaction savedTransaction = bankTransactionRepository.save(bankTransaction);
        bankAccount.setPosition(bankAccount.getPosition().add(amount).setScale(2, RoundingMode.HALF_EVEN));
        bankAccountRepository.save(bankAccount);
        return savedTransaction;
    }

    public BankTransaction makeWithdrawal(String accountOwner, BigDecimal amount) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findBankAccountByOwnerLogin(accountOwner);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException("Bank account with owner login '" + accountOwner + "' not found");
        }
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setAccount(bankAccount);
        bankTransaction.setAmount(amount.negate().setScale(2, RoundingMode.HALF_EVEN));
        bankTransaction.setType(TransactionType.DEBIT);
        bankTransaction.setValueDate(LocalDate.now());
        BankTransaction savedTransaction = bankTransactionRepository.save(bankTransaction);
        bankAccount.setPosition(bankAccount.getPosition().add(amount.negate()).setScale(2, RoundingMode.HALF_EVEN));
        bankAccountRepository.save(bankAccount);
        return savedTransaction;
    }

    public BankTransactionHistory retrieveHistory(String accountOwner) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findBankAccountByOwnerLogin(accountOwner);
        if (bankAccount == null) {
            throw new BankAccountNotFoundException("Bank account with owner login '" + accountOwner + "' not found");
        }
        List<BankTransaction> transactionList = bankTransactionRepository.findAllByAccountOrderByValueDateDesc(bankAccount);

        List<BankTransactionWithBalance> transactionsWithBalance = new ArrayList<>();
        BigDecimal lastBalance = bankAccount.getPosition();
        for (BankTransaction bankTransaction : transactionList) {
            transactionsWithBalance.add(new BankTransactionWithBalance(bankTransaction, lastBalance));
            lastBalance = lastBalance.add(bankTransaction.getAmount().negate());
        }
        return new BankTransactionHistory(transactionsWithBalance);
    }
}
