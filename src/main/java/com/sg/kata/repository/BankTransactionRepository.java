package com.sg.kata.repository;

import com.sg.kata.domain.BankTransaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BankTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {}
