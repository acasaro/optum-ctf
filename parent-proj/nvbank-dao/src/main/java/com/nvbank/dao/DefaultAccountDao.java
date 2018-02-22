package com.nvbank.dao;

import com.nvbank.model.Account;
import com.nvbank.model.BankUser;
import com.nvbank.model.Txn;
import com.nvbank.util.DatabaseUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.sql.DataSource;

import java.io.StringWriter;
import java.io.PrintWriter;

public class DefaultAccountDao implements AccountDao {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultAccountDao.class);
	
    public DefaultAccountDao() {}
    
    @Override
	public List<Account> listAccounts() {
    	DataSource ds = DatabaseUtility.getDataSource();
    	List<Account> accList = new ArrayList<Account>();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, accountNumber, balance from accounts");
	    	resultSet = pstmt.executeQuery();
	    	
	    	while(resultSet.next())
	    	{	
		    	Account acc = new Account();
		    	acc.setId(resultSet.getInt(1));
		    	acc.setAccountNumber(resultSet.getInt(2));
		    	acc.setBalance(resultSet.getDouble(3));
		    	
		    	accList.add(acc);
	    	}
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return accList;
    }

    @Override
    public Account getAccountById(int accountId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	Account acc = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select accountNumber, balance from accounts where id = ?");
	  
	    	pstmt.setInt(1, accountId);
	    	resultSet = pstmt.executeQuery();
	    	
	    	if (resultSet.next())
	    	{	
	    		acc = new Account();
		    	acc.setId(accountId);
		    	acc.setAccountNumber(resultSet.getInt(1));
		    	acc.setBalance(resultSet.getDouble(2));
	    	}
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return acc;
    }
    
    @Override
    public Account getAccountByAccountNumber(int accountNum) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	Account acc = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, balance from accounts where accountNumber = ?");
	  
	    	pstmt.setInt(1, accountNum);
	    	resultSet = pstmt.executeQuery();
	    	
	    	if (resultSet.next())
	    	{	
	    		acc = new Account();
		    	acc.setId(resultSet.getInt(1));
		    	acc.setAccountNumber(accountNum);
		    	acc.setBalance(resultSet.getDouble(2));
	    	}
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return acc;
    }
    
    @Override
    public List<Account> getAccountsByUserId(int userId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	List<Account> accList = new LinkedList<Account>();

    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select accountId from accountmapping where userId = ?");
	  
	    	pstmt.setInt(1, userId);
	    	rs1 = pstmt.executeQuery();
	    	
	    	while (rs1.next())
	    	{	
	    		int accId = rs1.getInt(1);
	    		ResultSet rs2 = null;
	    		pstmt = connection.prepareStatement("select accountNumber, balance from accounts where id = ?");
	    		pstmt.setInt(1, accId);
	    		
	    		try
	    		{
		    		rs2 = pstmt.executeQuery();
		    		
		    		if (rs2.next())
		    		{
		    			Account acc = new Account();
				    	acc.setId(accId);
				    	acc.setAccountNumber(rs2.getInt(1));
				    	acc.setBalance(rs2.getDouble(2));
				    	accList.add(acc);
		    		}
	    		}
		    	catch (SQLException se) {
		    		StringWriter sw = new StringWriter();
		    		PrintWriter pw = new PrintWriter(sw);
		    		se.printStackTrace(pw);
		    		String sStackTrace = sw.toString(); 
		    		logger.warn(sStackTrace);
		    	}
		    	finally
		    	{
		    		try { rs2.close(); } catch (Exception e) {}
		    	}
	    	}
	    	
    	}
    	catch (SQLException se) {
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		se.printStackTrace(pw);
    		String sStackTrace = sw.toString(); 
    		logger.warn(sStackTrace);
    	}
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return accList;
    }
    
    @Override
    public List<BankUser> getAccountUsersByAccountId(int accId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	List<BankUser> userList = new LinkedList<BankUser>();
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select userId from accountmapping where accountId = ?");
	  
	    	pstmt.setInt(1, accId);
	    	rs1 = pstmt.executeQuery();
	    	
	    	while (rs1.next())
	    	{	
	    		int userId = rs1.getInt(1);
	    		ResultSet rs2 = null;
	    		pstmt = connection.prepareStatement("select firstName, lastName, userName, ssn from bankusers where id = ?");
	    		pstmt.setInt(1, userId);
	    		
	    		try
	    		{
		    		rs2 = pstmt.executeQuery();
		    		
		    		if (rs2.next())
		    		{
		    			BankUser bu = new BankUser();
				    	bu.setId(userId);
				    	bu.setFirstName(rs2.getString(1));
				    	bu.setLastName(rs2.getString(2));
				    	bu.setUserName(rs2.getString(3));
				    	bu.setSsn(rs2.getString(4));
				    	userList.add(bu);
		    		}
	    		}
		    	catch (SQLException se) {
		    		StringWriter sw = new StringWriter();
		    		PrintWriter pw = new PrintWriter(sw);
		    		se.printStackTrace(pw);
		    		String sStackTrace = sw.toString(); 
		    		logger.warn(sStackTrace);
		    	}
		    	finally
		    	{
		    		try { rs2.close(); } catch (Exception e) {}
		    	}
	    	}
    	}
    	catch (SQLException se) {
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		se.printStackTrace(pw);
    		String sStackTrace = sw.toString(); 
    		logger.warn(sStackTrace);
    	}
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return userList;
    }

    @Override
    public boolean createAccount(Account acc) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	boolean result = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("insert into accounts (accountNumber, balance) values (?, ?)");
	  
	    	pstmt.setInt(1, acc.getAccountNumber());
	    	pstmt.setDouble(2, acc.getBalance());
	    	pstmt.executeUpdate();
	    	
	    	result = true;
    	}
    	catch (SQLException se) { 
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		se.printStackTrace(pw);
    		String sStackTrace = sw.toString(); // stack trace as a string
    		System.out.println(sStackTrace);
    	}
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return result;
    }
    
    @Override
    public boolean associateUserWithAccount(Account acc, BankUser bu) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	boolean result = false;
    	
    	try
    	{
	    	connection = ds.getConnection();  	
	    	pstmt = connection.prepareStatement("insert into accountmapping (accountId, userId) values (?, ?)");
	  	  
	    	pstmt.setInt(1, acc.getId());
	    	pstmt.setDouble(2, bu.getId());
	    	pstmt.executeUpdate();
	    	
	    	result = true;
    	}
    	catch (SQLException se) { 
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		se.printStackTrace(pw);
    		String sStackTrace = sw.toString(); // stack trace as a string
    		System.out.println(sStackTrace);
    	}
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return result;
    }

    @Override
    public boolean deleteAccountById(int accountId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	boolean result = false;
    	
    	try
    	{
	    	connection = ds.getConnection();  	
	    	pstmt = connection.prepareStatement("delete from accountmapping where accountId = ?");
	    	pstmt.setInt(1, accountId);
	    	pstmt.executeUpdate();
	    	
	    	pstmt = connection.prepareStatement("delete from accounts where id = ?");
	    	pstmt.setInt(1, accountId);
	    	pstmt.executeUpdate();
	    	
	    	result = true;
    	}
    	catch (SQLException se) { 
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		se.printStackTrace(pw);
    		String sStackTrace = sw.toString(); // stack trace as a string
    		System.out.println(sStackTrace);
    	}
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return result;	
    }
    
    @Override
    public boolean deleteAccountsByUserId(int userId) {
    	return false;
    }
    
    @Override
    public List<Txn> getTxnsByAccountId(int accId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	List<Txn> txnList = new LinkedList<Txn>();
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, value, description from txn where accountId = ?");
	  
	    	pstmt.setInt(1, accId);
	    	rs1 = pstmt.executeQuery();
	    	
	    	while (rs1.next())
	    	{	
	    		Txn txn = new Txn();
	    		txn.setId(rs1.getInt(1));
	    		txn.setAmount(rs1.getDouble(2));
	    		txn.setDescription(rs1.getString(3));
	    		txn.setAccountId(accId);
	    		txnList.add(txn);
	    	}
    	}
    	catch (SQLException se) {
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		se.printStackTrace(pw);
    		String sStackTrace = sw.toString(); 
    		logger.warn(sStackTrace);
    	}
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return txnList;
    }
}
