package com.sg.kata.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sg.kata.domain.enumeration.TransactionType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * A BankTransaction.
 */
@Entity
@Table(name = "bank_transaction")
public class BankTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "label")
    private String label;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "amount", precision = 21, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;

    @ManyToOne
    @JsonIgnoreProperties(value = { "bankTransactions" }, allowSetters = true)
    private BankAccount account;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankTransaction id(Long id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return this.label;
    }

    public BankTransaction label(String label) {
        this.label = label;
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDate getValueDate() {
        return this.valueDate;
    }

    public BankTransaction valueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public BankTransaction amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return this.type;
    }

    public BankTransaction type(TransactionType type) {
        this.type = type;
        return this;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BankAccount getAccount() {
        return this.account;
    }

    public BankTransaction account(BankAccount bankAccount) {
        this.setAccount(bankAccount);
        return this;
    }

    public void setAccount(BankAccount bankAccount) {
        this.account = bankAccount;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BankTransaction)) {
            return false;
        }
        return id != null && id.equals(((BankTransaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BankTransaction{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", valueDate='" + getValueDate() + "'" +
            ", amount=" + getAmount() +
            ", type='" + getType() + "'" +
            "}";
    }
}
