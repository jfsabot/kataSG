package com.sg.kata.service;

public class BankAccountNotFoundException extends Throwable {

    public BankAccountNotFoundException(String errorMsg) {
        super(errorMsg);
    }
}
