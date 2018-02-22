package com.nvbank.model;

public class Account {

    private int id;
    private int accountNumber;
    private double balance;
    public Account() {
        // empty to allow for bean access
    }

    public Account(int id, int accountNumber, double balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getAccountNumber() {
    	return accountNumber;
    }
    
    public void setAccountNumber(int accountNumber) {
    	this.accountNumber = accountNumber;
    }
    
    public double getBalance() {
    	return balance;
    }
    
    public void setBalance(double balance) {
    	this.balance = balance;
    }
}

