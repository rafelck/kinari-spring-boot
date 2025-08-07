package com.kinari.kinari.dto.account;

import java.math.BigInteger;

public class AccountRequest {
    private String name;
    private BigInteger balance;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }
}
