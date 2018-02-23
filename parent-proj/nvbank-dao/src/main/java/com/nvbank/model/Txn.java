package com.nvbank.model;

public class Txn {

    private int id;
    private int accountId;
    private double amount;
    private String description;
    
    public Txn() {
        // empty to allow for bean access
    }

    public Txn(int id, int accountId, double amount, String description) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getAccountId() {
    	return accountId;
    }
    
    public void setAccountId(int accountId) {
    	this.accountId = accountId;
    }
    
    public double getAmount() {
    	return amount;
    }
    
    public void setAmount(double balance) {
    	this.amount = amount;
    }
    
    public String getDescription() {
    	return description;
    }
    
    public void setDescription(String description) {
    	this.description = description;
    }
}

