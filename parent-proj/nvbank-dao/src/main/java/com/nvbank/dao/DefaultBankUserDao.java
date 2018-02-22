/* 
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nvbank.dao;

import com.nvbank.model.BankUser;
import com.nvbank.util.DatabaseUtility;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.StringWriter;
import java.io.PrintWriter;

import javax.sql.DataSource;

public final class DefaultBankUserDao implements BankUserDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultBankUserDao.class);
    
    public DefaultBankUserDao() {}

    @Override
    public List<BankUser> listUsers() {
    	DataSource ds = DatabaseUtility.getDataSource();
    	List<BankUser> userList = new ArrayList<BankUser>();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, firstName, lastName, userName, ssn, failedauth from bankusers");
	    	resultSet = pstmt.executeQuery();
	    	
	    	while(resultSet.next())
	    	{	
		    	BankUser bu = new BankUser();
		    	bu.setId(resultSet.getInt(1));
		    	bu.setFirstName(resultSet.getString(2));
		    	bu.setLastName(resultSet.getString(3));
		    	bu.setUserName(resultSet.getString(4));
		    	bu.setSsn(resultSet.getString(5));
		    	bu.setIsLocked(true ? resultSet.getInt(6) >= 5 : false);
		    	
		    	userList.add(bu);
	    	}
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return userList;
    }

    @Override
    public BankUser getUser(int id) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	BankUser u = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select firstName, lastName, userName, ssn, roleId, failedauth from bankusers where id = ?");
	  
	    	pstmt.setInt(1, id);
	    	resultSet = pstmt.executeQuery();
	    	
	    	if (resultSet.next())
	    	{	
		    	u = new BankUser();
		    	u.setFirstName(resultSet.getString(1));
		    	u.setLastName(resultSet.getString(2));
		    	u.setUserName(resultSet.getString(3));
		    	u.setSsn(resultSet.getString(4));
		    	u.setRoleId(resultSet.getInt(5));
		    	u.setId(id);
		    	u.setIsLocked(true ? resultSet.getInt(6) >= 5 : false);
	    	}
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return u;
    }
    
    @Override
    public BankUser getUserByUserName(String username) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	BankUser u = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, firstName, lastName, userName, ssn, password, failedauth from bankusers where userName = ?");
	  
	    	pstmt.setString(1, username);
	    	resultSet = pstmt.executeQuery();
	    	
	    	if (resultSet.next())
	    	{	
		    	u = new BankUser();
		    	u.setId(resultSet.getInt(1));
		    	u.setFirstName(resultSet.getString(2));
		    	u.setLastName(resultSet.getString(3));
		    	u.setUserName(resultSet.getString(4));
		    	u.setSsn(resultSet.getString(5));
		    	u.setPassword(resultSet.getString(6));
		    	u.setIsLocked(true ? resultSet.getInt(7) >= 5 : false);
	    	}
    	}
    	catch (SQLException se) { logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return u;
    }

    @Override
    public boolean addUser(BankUser user) {
    	boolean result = false;
    	DataSource ds = DatabaseUtility.getDataSource();;
    	Connection connection = null;
    	PreparedStatement pstmt = null;
    	
    	int roleId = user.getRoleId();
    	if (roleId < 1) { roleId = 1; }
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("insert into bankusers (firstName, lastName, userName, password, ssn, roleId) values (?, ?, ?, ?, ?, ?)");
	  
	    	pstmt.setString(1, user.getFirstName());
	    	pstmt.setString(2, user.getLastName());
	    	pstmt.setString(3, user.getUserName());
	    	pstmt.setString(4, user.getPassword());
	    	pstmt.setString(5, user.getSsn());
	    	pstmt.setInt(6,roleId);
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
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return result;
    }

    //TODO: include email address on update
    @Override
    public boolean updateUserProfile(int id, BankUser user) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	boolean isSuccess = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("update bankusers set firstName = ?, lastName = ?, ssn = ? where id = ?");
	  
	    	pstmt.setString(1, user.getFirstName());
	    	pstmt.setString(2, user.getLastName());
	    	pstmt.setString(3, user.getSsn());
	    	pstmt.setInt(4, id);
	    	pstmt.executeUpdate();
	    	
	    	isSuccess = true;
    	}
    	catch (SQLException se) { logger.warn("SQLException occurred!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return isSuccess;
    }

    @Override
    public boolean deleteUser(int id) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	boolean isSuccess = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("delete from activesessions where userId = ?");
		  	pstmt.setInt(1, id);
	    	pstmt.executeUpdate();
	    	
	    	pstmt = connection.prepareStatement("delete from accountmapping where userId = ?");
	    	pstmt.setInt(1, id);
	    	pstmt.executeUpdate();
	    	
	    	pstmt = connection.prepareStatement("delete from messages where toUserId = ? or fromUserId = ?");
	    	pstmt.setInt(1,  id);
	    	pstmt.executeUpdate();
	    	
	    	pstmt = connection.prepareStatement("delete from ticket where toUserId = ? or fromUserId = ?");
	    	pstmt.setInt(1,  id);
	    	pstmt.executeUpdate();
	    	
	    	pstmt = connection.prepareStatement("delete from bankusers where fromUserId = ?");
	    	pstmt.setInt(1, id);
	    	pstmt.executeUpdate();
	    	
	    	isSuccess = true;
    	}
    	catch (SQLException se) { logger.warn("SQLException occurred!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return isSuccess;
    }
    
    private String createAccountNumber() {
    	String result = null;
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	String accountNum = generateRandomAccountNum();
    	
    	try
    	{
    		connection = ds.getConnection();
    		pstmt = connection.prepareStatement("select * from accounts where accountNumber = ?");
  		  
    		while (result == null)
    		{
		    	pstmt.setString(1, accountNum.toString());
		    	resultSet = pstmt.executeQuery();
		    	
		    	if (resultSet.next())
		    	{
		    		logger.warn("account number already exists, generating a new one");
		    		accountNum = generateRandomAccountNum();
		    	}
		    	else
		    	{
		    		result = accountNum;
		    	}
    		}
	    	
    	}
    	catch (SQLException se) { System.out.println(se); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return result;
    }
    
    public boolean incFailedAuth(BankUser user)
    {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	int lcount = getLockoutCount(user) + 1;
    	boolean success = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("update bankusers set failedauth = ? where username = ?");
	  
	    	pstmt.setInt(1, lcount);
	    	pstmt.setString(2,  user.getUserName());
	    	pstmt.executeUpdate();
	    	success = true;
    	}
    	catch (SQLException se) { logger.warn("SQLException occurred in incFailedAuth!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return success;
    }
    
    public boolean resetFailedAuth(BankUser user)
    {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	boolean success = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("update bankusers set failedauth = 0 where id = ?");
	  
	    	pstmt.setInt(1, user.getId());
	    	pstmt.executeUpdate();
	    	success = true;
    	}
    	catch (SQLException se) { logger.warn("SQLException occurred in resetFailedAuth!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return success;
    }
    
    public int getLockoutCount(BankUser user)
    {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	int lcount = 0;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select failedauth from bankusers where username = ?");
	  
	    	pstmt.setString(1, user.getUserName());
	    	resultSet = pstmt.executeQuery();
	    	resultSet.next();
	    	
	    	lcount = resultSet.getInt(1);
    	}
    	catch (SQLException se) { logger.warn("SQLException occurred in getLockoutCount!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return lcount;
    }
    
    private String generateRandomAccountNum()
    {
    	RandomStringGenerator generator = new RandomStringGenerator.Builder()
        	.withinRange('0', '9').build();
    	return generator.generate(15);
    }
}
