package com.sg.kata.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A BankAccount.
 */
@Entity
@Table(name = "bank_account")
public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "owner_login")
    private String ownerLogin;

    @Column(name = "position", precision = 21, scale = 2)
    private BigDecimal position;

    @OneToMany(mappedBy = "account")
    @JsonIgnoreProperties(value = { "account" }, allowSetters = true)
    private Set<BankTransaction> bankTransactions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankAccount id(Long id) {
        this.id = id;
        return this;
    }

    public String getOwnerLogin() {
        return this.ownerLogin;
    }

    public BankAccount ownerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
        return this;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public BigDecimal getPosition() {
        return this.position;
    }

    public BankAccount position(BigDecimal position) {
        this.position = position;
        return this;
    }

    public void setPosition(BigDecimal position) {
        this.position = position;
    }

    public Set<BankTransaction> getBankTransactions() {
        return this.bankTransactions;
    }

    public BankAccount bankTransactions(Set<BankTransaction> bankTransactions) {
        this.setBankTransactions(bankTransactions);
        return this;
    }

    public BankAccount addBankTransaction(BankTransaction bankTransaction) {
        this.bankTransactions.add(bankTransaction);
        bankTransaction.setAccount(this);
        return this;
    }

    public BankAccount removeBankTransaction(BankTransaction bankTransaction) {
        this.bankTransactions.remove(bankTransaction);
        bankTransaction.setAccount(null);
        return this;
    }

    public void setBankTransactions(Set<BankTransaction> bankTransactions) {
        if (this.bankTransactions != null) {
            this.bankTransactions.forEach(i -> i.setAccount(null));
        }
        if (bankTransactions != null) {
            bankTransactions.forEach(i -> i.setAccount(this));
        }
        this.bankTransactions = bankTransactions;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BankAccount)) {
            return false;
        }
        return id != null && id.equals(((BankAccount) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BankAccount{" +
            "id=" + getId() +
            ", ownerLogin='" + getOwnerLogin() + "'" +
            ", position=" + getPosition() +
            "}";
    }
}
