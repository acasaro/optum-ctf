package com.nvbank.dao;

import com.nvbank.model.Message;
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

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public final class DefaultMessageDao implements MessageDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultMessageDao.class);
	
    public DefaultMessageDao() {}
    
    @Override
    public List<Message> listMessages() {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	ResultSet rs2 = null;
    	PreparedStatement pstmt = null;
    	List<Message> msgList = new ArrayList<Message>();
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, fromUserId, toUserId, subject, body, beenread from messages");
	    	rs1 = pstmt.executeQuery();
	    	
	    	while (rs1.next())
	    	{	
	    		int fromUserId = rs1.getInt(2);
	    		int toUserId = rs1.getInt(3);
	    		
	    		Message msg = new Message();
	    		msg.setId(rs1.getInt(1));
	    		msg.setFromUserId(fromUserId);
	    		msg.setToUserId(toUserId);
	    		msg.setSubject(rs1.getString(4));
	    		msg.setBody(rs1.getString(5));
	    		msg.setRead(rs1.getBoolean(6));
	    		
	    		pstmt = connection.prepareStatement("select userName from bankusers where id = ?");
	    		pstmt.setInt(1, fromUserId);
	    		rs2 = pstmt.executeQuery();
	    		
	    		if (rs2.next()) { msg.setFromUserName(rs2.getString(1)); }
	    		
	    		pstmt = connection.prepareStatement("select userName from bankusers where id = ?");
	    		pstmt.setInt(1, toUserId);
	    		rs2 = pstmt.executeQuery();
	    		
	    		if (rs2.next()) { msg.setToUserName(rs2.getString(1)); }
	    		
	    		msgList.add(msg);
	    	}
    	}
    	catch (SQLException se) { logger.warn("Error occurred in getMessagesByUserId()!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { rs2.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return msgList;
    }
    
    @Override
    public boolean createMessage(Message msg) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	boolean success = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id from bankUsers where roleId = 1");
	    	resultSet = pstmt.executeQuery();
	    	
	    	while (resultSet.next()) {
	    		int toUserId = resultSet.getInt(1);
	    		
		    	pstmt = connection.prepareStatement("insert into messages (fromUserId, toUserId, subject, body, beenread) values (?, ?, ?, ?, ?)");
		    	
		    	pstmt.setInt(1, msg.getFromUserId());
		    	pstmt.setInt(2, toUserId);
		    	pstmt.setString(3, msg.getSubject());
		    	pstmt.setString(4, msg.getBody());
		    	pstmt.setInt(5, 0);
		    	pstmt.executeUpdate();
	    	}
	    	
	    	success = true;
    	}
    	catch (SQLException se) { logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return success;
    }

    @Override
    public List<Message> getMessagesByUserId(int userId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	ResultSet rs2 = null;
    	PreparedStatement pstmt = null;
    	List<Message> msgList = new ArrayList<Message>();;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, fromUserId, toUserId, subject, body, beenread from messages where fromUserId = ? or toUserId = ?");
	  
	    	pstmt.setInt(1, userId);
	    	pstmt.setInt(2, userId);
	    	rs1 = pstmt.executeQuery();
	    	
	    	while (rs1.next())
	    	{	
	    		int fromUserId = rs1.getInt(2);
	    		int toUserId = rs1.getInt(3);
	    		
	    		Message msg = new Message();
	    		msg.setId(rs1.getInt(1));
	    		msg.setFromUserId(fromUserId);
	    		msg.setToUserId(toUserId);
	    		msg.setSubject(rs1.getString(4));
	    		msg.setBody(rs1.getString(5));
	    		msg.setRead(rs1.getBoolean(6));

	    		pstmt = connection.prepareStatement("select userName from bankusers where id = ?");
	    		pstmt.setInt(1, fromUserId);
	    		rs2 = pstmt.executeQuery();
	    		
	    		if (rs2.next()) { msg.setFromUserName(rs2.getString(1)); }
	    		
	    		pstmt = connection.prepareStatement("select userName from bankusers where id = ?");
	    		pstmt.setInt(1, toUserId);
	    		rs2 = pstmt.executeQuery();
	    		
	    		if (rs2.next()) { msg.setToUserName(rs2.getString(1)); }
	    		
	    		msgList.add(msg);
	    	}
    	}
    	catch (SQLException se) { logger.warn("Error occurred in getMessagesByUserId()!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { rs2.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return msgList;
    }
    
    @Override
    public Message getMessageById(int msgId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	Message msg = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select fromUserId, toUserId, subject, body, beenread from messages where id = ?");
	  
	    	pstmt.setInt(1, msgId);
	    	resultSet = pstmt.executeQuery();
	    	
	    	if (resultSet.next())
	    	{	
	    		int fromUserId = resultSet.getInt(1);
	    		int toUserId = resultSet.getInt(2);
	    		
	    		msg = new Message();
	    		msg.setId(msgId);
	    		msg.setFromUserId(fromUserId);
	    		msg.setToUserId(toUserId);
	    		msg.setSubject(resultSet.getString(3));
	    		msg.setBody(resultSet.getString(4));
	    		msg.setRead(resultSet.getBoolean(5));
	    		
	    		pstmt = connection.prepareStatement("select userName from bankusers where id = ?");
	    		pstmt.setInt(1, fromUserId);
	    		resultSet = pstmt.executeQuery();
	    		
	    		if (resultSet.next()) { msg.setFromUserName(resultSet.getString(1)); }
	    		
	    		pstmt = connection.prepareStatement("select userName from bankusers where id = ?");
	    		pstmt.setInt(1, toUserId);
	    		resultSet = pstmt.executeQuery();
	    		
	    		if (resultSet.next()) { msg.setToUserName(resultSet.getString(1)); }
	    	}
    	}
    	catch (SQLException se) { logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return msg;
    }
    
    @Override
    public boolean deleteMessage(int msgId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	boolean success = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("delete from messages where id = ?");
	  
	    	pstmt.setInt(1, msgId);
	    	pstmt.executeUpdate();
	    	success = true;
    	}
    	catch (SQLException se) { logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return success;
    }
    
    @Override
    public boolean markMessageAsRead(int msgId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet resultSet = null;
    	PreparedStatement pstmt = null;
    	boolean success = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("update messages set beenread = 1 where id = ?");
	  
	    	pstmt.setInt(1, msgId);
	    	pstmt.executeUpdate();
	    	success = true;
    	}
    	catch (SQLException se) { logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { resultSet.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return success;
    }
}