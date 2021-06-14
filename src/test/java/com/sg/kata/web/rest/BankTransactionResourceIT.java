package com.sg.kata.web.rest;

import static com.sg.kata.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sg.kata.IntegrationTest;
import com.sg.kata.domain.BankAccount;
import com.sg.kata.domain.BankTransaction;
import com.sg.kata.domain.enumeration.TransactionType;
import com.sg.kata.repository.BankTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BankTransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BankTransactionResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_VALUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VALUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final TransactionType DEFAULT_TYPE = TransactionType.CREDIT;
    private static final TransactionType UPDATED_TYPE = TransactionType.DEBIT;

    private static final String ENTITY_API_URL = "/api/bank-transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBankTransactionMockMvc;

    private BankTransaction bankTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankTransaction createEntity(EntityManager em) {
        BankTransaction bankTransaction = new BankTransaction()
            .label(DEFAULT_LABEL)
            .valueDate(DEFAULT_VALUE_DATE)
            .amount(DEFAULT_AMOUNT)
            .type(DEFAULT_TYPE);
        return bankTransaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankTransaction createUpdatedEntity(EntityManager em) {
        BankTransaction bankTransaction = new BankTransaction()
            .label(UPDATED_LABEL)
            .valueDate(UPDATED_VALUE_DATE)
            .amount(UPDATED_AMOUNT)
            .type(UPDATED_TYPE);
        return bankTransaction;
    }

    public static BankAccount persistBankAccount(EntityManager em) {
        BankAccount bankAccount = new BankAccount().ownerLogin("user").position(BigDecimal.valueOf(500));
        em.persist(bankAccount);
        return bankAccount;
    }

    @BeforeEach
    public void initTest() {
        bankTransaction = createEntity(em);
        persistBankAccount(em);
    }

    @Test
    @Transactional
    void createBankTransaction() throws Exception {
        int databaseSizeBeforeCreate = bankTransactionRepository.findAll().size();
        // Create the BankTransaction
        restBankTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isCreated());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        BankTransaction testBankTransaction = bankTransactionList.get(bankTransactionList.size() - 1);
        assertThat(testBankTransaction.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testBankTransaction.getValueDate()).isEqualTo(DEFAULT_VALUE_DATE);
        assertThat(testBankTransaction.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testBankTransaction.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void makeDeposit() throws Exception {
        int databaseSizeBeforeCreate = bankTransactionRepository.findAll().size();
        // Make a new deposit
        restBankTransactionMockMvc
            .perform(post("/api/deposit").with(csrf()).contentType(MediaType.APPLICATION_JSON).param("amount", "200"))
            .andExpect(status().isCreated());

        // Validate the deposit generated a new BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        BankTransaction testBankTransaction = bankTransactionList.get(bankTransactionList.size() - 1);
        assertThat(testBankTransaction.getLabel()).isEqualTo("Deposit");
        assertThat(testBankTransaction.getValueDate().atStartOfDay()).isEqualTo(LocalDate.now().atStartOfDay());
        assertThat(testBankTransaction.getAmount()).isEqualByComparingTo("200");
        assertThat(testBankTransaction.getType()).isEqualTo(TransactionType.CREDIT);
    }

    @Test
    @Transactional
    void makeWithdrawal() throws Exception {
        int databaseSizeBeforeCreate = bankTransactionRepository.findAll().size();
        // Make a new deposit
        restBankTransactionMockMvc
            .perform(post("/api/withdrawal").with(csrf()).contentType(MediaType.APPLICATION_JSON).param("amount", "150"))
            .andExpect(status().isCreated());

        // Validate the deposit generated a new BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeCreate + 1);
        BankTransaction testBankTransaction = bankTransactionList.get(bankTransactionList.size() - 1);
        assertThat(testBankTransaction.getLabel()).isEqualTo("Withdrawal");
        assertThat(testBankTransaction.getValueDate().atStartOfDay()).isEqualTo(LocalDate.now().atStartOfDay());
        assertThat(testBankTransaction.getAmount()).isEqualByComparingTo("-150");
        assertThat(testBankTransaction.getType()).isEqualTo(TransactionType.DEBIT);
    }

    @Test
    @Transactional
    void createBankTransactionWithExistingId() throws Exception {
        // Create the BankTransaction with an existing ID
        bankTransaction.setId(1L);

        int databaseSizeBeforeCreate = bankTransactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBankTransactionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBankTransactions() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        // Get all the bankTransactionList
        restBankTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bankTransaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].valueDate").value(hasItem(DEFAULT_VALUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getBankTransaction() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        // Get the bankTransaction
        restBankTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, bankTransaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bankTransaction.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.valueDate").value(DEFAULT_VALUE_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBankTransaction() throws Exception {
        // Get the bankTransaction
        restBankTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBankTransaction() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();

        // Update the bankTransaction
        BankTransaction updatedBankTransaction = bankTransactionRepository.findById(bankTransaction.getId()).get();
        // Disconnect from session so that the updates on updatedBankTransaction are not directly saved in db
        em.detach(updatedBankTransaction);
        updatedBankTransaction.label(UPDATED_LABEL).valueDate(UPDATED_VALUE_DATE).amount(UPDATED_AMOUNT).type(UPDATED_TYPE);

        restBankTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBankTransaction.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBankTransaction))
            )
            .andExpect(status().isOk());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
        BankTransaction testBankTransaction = bankTransactionList.get(bankTransactionList.size() - 1);
        assertThat(testBankTransaction.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testBankTransaction.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testBankTransaction.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testBankTransaction.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bankTransaction.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBankTransactionWithPatch() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();

        // Update the bankTransaction using partial update
        BankTransaction partialUpdatedBankTransaction = new BankTransaction();
        partialUpdatedBankTransaction.setId(bankTransaction.getId());

        partialUpdatedBankTransaction.valueDate(UPDATED_VALUE_DATE).type(UPDATED_TYPE);

        restBankTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBankTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBankTransaction))
            )
            .andExpect(status().isOk());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
        BankTransaction testBankTransaction = bankTransactionList.get(bankTransactionList.size() - 1);
        assertThat(testBankTransaction.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testBankTransaction.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testBankTransaction.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testBankTransaction.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateBankTransactionWithPatch() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();

        // Update the bankTransaction using partial update
        BankTransaction partialUpdatedBankTransaction = new BankTransaction();
        partialUpdatedBankTransaction.setId(bankTransaction.getId());

        partialUpdatedBankTransaction.label(UPDATED_LABEL).valueDate(UPDATED_VALUE_DATE).amount(UPDATED_AMOUNT).type(UPDATED_TYPE);

        restBankTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBankTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBankTransaction))
            )
            .andExpect(status().isOk());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
        BankTransaction testBankTransaction = bankTransactionList.get(bankTransactionList.size() - 1);
        assertThat(testBankTransaction.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testBankTransaction.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testBankTransaction.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testBankTransaction.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bankTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBankTransaction() throws Exception {
        int databaseSizeBeforeUpdate = bankTransactionRepository.findAll().size();
        bankTransaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bankTransaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BankTransaction in the database
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBankTransaction() throws Exception {
        // Initialize the database
        bankTransactionRepository.saveAndFlush(bankTransaction);

        int databaseSizeBeforeDelete = bankTransactionRepository.findAll().size();

        // Delete the bankTransaction
        restBankTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, bankTransaction.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BankTransaction> bankTransactionList = bankTransactionRepository.findAll();
        assertThat(bankTransactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
