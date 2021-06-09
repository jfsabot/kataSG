package com.sg.kata.cucumber;

import com.sg.kata.KataSgApp;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = KataSgApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
