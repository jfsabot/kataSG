package com.sg.kata.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sg.kata.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BankTransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BankTransaction.class);
        BankTransaction bankTransaction1 = new BankTransaction();
        bankTransaction1.setId(1L);
        BankTransaction bankTransaction2 = new BankTransaction();
        bankTransaction2.setId(bankTransaction1.getId());
        assertThat(bankTransaction1).isEqualTo(bankTransaction2);
        bankTransaction2.setId(2L);
        assertThat(bankTransaction1).isNotEqualTo(bankTransaction2);
        bankTransaction1.setId(null);
        assertThat(bankTransaction1).isNotEqualTo(bankTransaction2);
    }
}
