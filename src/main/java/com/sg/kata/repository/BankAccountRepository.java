package com.sg.kata.repository;

import com.sg.kata.domain.BankAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BankAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    /*
    Assume there is one account per user
     */
    BankAccount findBankAccountByOwnerLogin(String ownerLogin);
}
