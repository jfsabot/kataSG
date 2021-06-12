package com.sg.kata.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.sg.kata.IntegrationTest;
import com.sg.kata.domain.BankAccount;
import com.sg.kata.domain.BankTransaction;
import com.sg.kata.domain.enumeration.TransactionType;
import com.sg.kata.repository.BankAccountRepository;
import com.sg.kata.repository.BankTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class BankAccountServiceIT {

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @BeforeEach
    public void setUp() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setOwnerLogin("johndoe");
        bankAccount.setPosition(new BigDecimal(500));
        bankAccount = bankAccountRepository.save(bankAccount);

        createHistoTransaction(bankAccount, "Salary", TransactionType.CREDIT, LocalDate.now().minusDays(5), new BigDecimal("2513.44"));
        createHistoTransaction(bankAccount, "Electricity", TransactionType.DEBIT, LocalDate.now().minusDays(2), new BigDecimal("-47.56"));
        createHistoTransaction(bankAccount, "TV", TransactionType.DEBIT, LocalDate.now().minusDays(4), new BigDecimal("-56"));
        createHistoTransaction(bankAccount, "Food", TransactionType.DEBIT, LocalDate.now().minusDays(4), new BigDecimal("-125.16"));
        createHistoTransaction(bankAccount, "Rent", TransactionType.DEBIT, LocalDate.now().minusDays(6), new BigDecimal("-763.23"));
    }

    private void createHistoTransaction(
        BankAccount bankAccount,
        String label,
        TransactionType type,
        LocalDate valueDate,
        BigDecimal amount
    ) {
        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setLabel(label);
        bankTransaction.setType(type);
        bankTransaction.setAccount(bankAccount);
        bankTransaction.setValueDate(valueDate);
        bankTransaction.setAmount(amount);
        bankTransactionRepository.save(bankTransaction);
    }

    @Test
    @Transactional
    public void test_as_bank_client_I_make_a_deposit_in_my_account() throws BankAccountNotFoundException {
        BankTransaction bankTransaction = bankAccountService.makeDeposit("johndoe", BigDecimal.valueOf(200));
        assertNotNull(bankTransaction);
        assertNotNull(bankTransaction.getId());
        assertEquals("200.00", bankTransaction.getAmount().toString());
        assertEquals(TransactionType.CREDIT, bankTransaction.getType());
        assertEquals("700.00", bankTransaction.getAccount().getPosition().toString());
    }

    @Test
    @Transactional
    public void test_as_bank_client_I_make_a_withdrawal_from_my_account() throws BankAccountNotFoundException {
        BankTransaction bankTransaction = bankAccountService.makeWithdrawal("johndoe", BigDecimal.valueOf(53.6));
        assertNotNull(bankTransaction);
        assertNotNull(bankTransaction.getId());
        assertEquals("-53.60", bankTransaction.getAmount().toString());
        assertEquals(TransactionType.DEBIT, bankTransaction.getType());
        assertEquals("446.40", bankTransaction.getAccount().getPosition().toString());
    }

    @Test
    @Transactional
    public void test_As_bank_client_I_want_to_see_the_history() throws BankAccountNotFoundException {
        BankTransactionHistory bankTransactionHistory = bankAccountService.retrieveHistory("johndoe");
        System.out.println(bankTransactionHistory.toString());
        assertEquals(
            "Date       | Description                                        | Debit           | Credit          | Balance        " +
            System.lineSeparator() +
            "---------------------------------------------------------------------------------------------------------------------" +
            System.lineSeparator() +
            "2021-06-11 | Electricity                                        |          -47.56 |                 |             500" +
            System.lineSeparator() +
            "2021-06-09 | TV                                                 |             -56 |                 |          547.56" +
            System.lineSeparator() +
            "2021-06-09 | Food                                               |         -125.16 |                 |          603.56" +
            System.lineSeparator() +
            "2021-06-08 | Salary                                             |                 |         2513.44 |          728.72" +
            System.lineSeparator() +
            "2021-06-07 | Rent                                               |         -763.23 |                 |        -1784.72",
            bankTransactionHistory.toString()
        );
    }
}
