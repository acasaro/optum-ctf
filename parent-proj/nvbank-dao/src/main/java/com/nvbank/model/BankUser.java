package com.nvbank.model;

public class BankUser {

    private int id;
    private String firstName;
    private String lastName;
    private String userName;
    private String ssn;
    private String password;
    private int roleId;
    private int failedAuth;
    private boolean isLocked;

    public BankUser() {
        // empty to allow for bean access
    }

    public BankUser(int id, String firstName, String lastName, String userName, String ssn, String password, int roleId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.ssn = ssn;
        this.password = password;
        this.roleId = roleId;
        this.failedAuth = 0;
        this.isLocked = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
    	return firstName;
    }
    
    public void setFirstName(String firstName) {
    	this.firstName = firstName;
    }
    
    public String getLastName() {
    	return lastName;
    }
    
    public void setLastName(String lastName) {
    	this.lastName = lastName;
    }
    
    public String getUserName() {
    	return userName;
    }
    
    public void setUserName(String userName) {
    	this.userName = userName;
    }
    
    public String getSsn() {
    	return ssn;
    }
    
    public void setSsn(String ssn) {
    	this.ssn = ssn;
    }
    
    public String getPassword() {
    	return password;
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }
    
    public int getRoleId() {
    	return roleId;
    }
    
    public void setRoleId(int roleId) {
    	this.roleId = roleId;
    }
    
    public int getFailedAuth() {
    	return failedAuth;
    }
    
    public void setFailedAuth(int c) {
    	this.failedAuth = c;
    }
    
    public boolean getIsLocked() {
    	return isLocked;
    }
    
    public void setIsLocked(boolean b) {
    	this.isLocked = b;
    }
}
