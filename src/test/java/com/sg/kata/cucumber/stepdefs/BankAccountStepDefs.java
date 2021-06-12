package com.sg.kata.cucumber.stepdefs;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.kata.domain.BankAccount;
import com.sg.kata.web.rest.BankAccountResource;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class BankAccountStepDefs extends StepDefs {

    private final BankAccountResource bankAccountResource;
    private final ObjectMapper mapper;

    private MockMvc restBankAccountMockMvc;

    @Autowired
    public BankAccountStepDefs(BankAccountResource bankAccountResource, ObjectMapper mapper) {
        this.bankAccountResource = bankAccountResource;
        this.mapper = mapper;
    }

    @Before
    public void setup() {
        this.restBankAccountMockMvc = MockMvcBuilders.standaloneSetup(bankAccountResource).build();
    }

    @When("I check account position as user {string}, I expect")
    public void iCheckAccountPositionAsUserIExpect(String ownerName, DataTable expectedTable) throws Exception {
        MvcResult result = restBankAccountMockMvc
            .perform(post("/api/bank-accounts-by-owner-name").param("owner", ownerName).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        String json = result.getResponse().getContentAsString();
        List<BankAccount> actualBankAccounts = mapper.readValue(json, new TypeReference<>() {});

        List<String> topCells = expectedTable.row(0);
        List<Map<String, String>> expected = expectedTable.asMaps(String.class, String.class);

        DataTable actualTable = createTable(
            actualBankAccounts,
            Arrays.asList("ownerLogin", "position"),
            BankAccount::getOwnerLogin,
            BankAccount::getPosition
        );
        expectedTable.unorderedDiff(actualTable);
    }

    static <T> DataTable createTable(List<T> values, List<String> headers, Function<T, Object>... extractors) {
        List<List<String>> rawTable = new ArrayList<>();
        rawTable.add(headers);
        values
            .stream()
            .map(record -> Stream.of(extractors).map(f -> f.apply(record)).map(String::valueOf).collect(Collectors.toList()))
            .forEach(rawTable::add);
        return DataTable.create(rawTable);
    }
}
