package com.nvbank.dao;


import com.nvbank.model.ActiveSession;
import com.nvbank.model.BankUser;
import com.nvbank.util.DatabaseUtility;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public final class DefaultActiveSessionDao implements ActiveSessionDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultActiveSessionDao.class);
	
    public DefaultActiveSessionDao() {}

    @Override
    public Collection<ActiveSession> listSessions() {
    	return null;
    }

    @Override
    public ActiveSession getSessionBySessionString(String session) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	ActiveSession s = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, sessionValue, userId from activesessions where sessionValue = ?");
	  
	    	pstmt.setString(1, session);
	    	resultSet = pstmt.executeQuery();
	    	
	    	if (resultSet.next())
	    	{	
	    		s = new ActiveSession();
		    	s.setId(resultSet.getInt(1));
		    	s.setSessionValue(resultSet.getString(2));
		    	s.setUserId(resultSet.getInt(3));
	    	}
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return s;
    }
    
    @Override
    public ActiveSession getSessionByUserId(int userId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	ActiveSession s = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, sessionValue, userId from activesessions where userID = ?");
	  
	    	pstmt.setInt(1, userId);
	    	resultSet = pstmt.executeQuery();
	    	
	    	if (resultSet.next())
	    	{	
	    		s = new ActiveSession();
		    	s.setId(resultSet.getInt(1));
		    	s.setSessionValue(resultSet.getString(2));
		    	s.setUserId(resultSet.getInt(3));
	    	}
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return s;
    }
    
    @Override
    public BankUser getUserBySessionString(String session) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	int userId = -1;
    	BankUser bu = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select userId from activesessions where sessionValue = ?");
	  
	    	pstmt.setString(1, session);
	    	resultSet = pstmt.executeQuery();
	    	
	    	if (resultSet.next()) {	userId = resultSet.getInt(1); }
	    	else { return null; }
	    	
	    	pstmt = connection.prepareStatement("select userName, firstName, lastName, ssn, password, roleId from bankusers where id = ?");
	    	pstmt.setInt(1, userId);
	    	resultSet = pstmt.executeQuery();
	    	
	    	if (resultSet.next()) {
	    		bu = new BankUser();
	    		bu.setId(userId);
	    		bu.setUserName(resultSet.getString(1));
		    	bu.setFirstName(resultSet.getString(2));
		    	bu.setLastName(resultSet.getString(3));
		    	bu.setSsn(resultSet.getString(4));
		    	bu.setPassword(resultSet.getString(5));
		    	bu.setRoleId(resultSet.getInt(6));
	    	}
	    	else { return null; }
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return bu;
    }

    @Override
    public boolean createSession(ActiveSession session) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	PreparedStatement pstmt = null;
    	boolean result = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("insert into activesessions (sessionValue, userId) values (?, ?)");
	  
	    	pstmt.setString(1, session.getSessionValue());
	    	pstmt.setInt(2, session.getUserId());
	    	pstmt.executeUpdate();
	    	
	    	result = true;
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return result;
    }

    @Override
    public void deleteSessionById(int id) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	PreparedStatement pstmt = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("delete from activesessions where id = ?");
	  
	    	pstmt.setInt(1, id);
	    	pstmt.executeUpdate();
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    }
    
    @Override
    public void deleteSessionByUser(BankUser user) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	PreparedStatement pstmt = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("delete from activesessions where userId = ?");
	  
	    	pstmt.setInt(1, user.getId());
	    	pstmt.executeUpdate();
    	}
    	catch (SQLException se) { }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    }
}