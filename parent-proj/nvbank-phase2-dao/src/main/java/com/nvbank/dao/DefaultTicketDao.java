package com.nvbank.dao;

import com.nvbank.model.Ticket;
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

public final class DefaultTicketDao implements TicketDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultTicketDao.class);
	
    public DefaultTicketDao() {}
    
    @Override
    public List<Ticket> listTickets() {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	List<Ticket> ticketList = new ArrayList<Ticket>();;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, body, response, resolved, filename, fromUserId from tickets");
	    	rs1 = pstmt.executeQuery();
	    	
	    	while (rs1.next())
	    	{	
	    		Ticket ticket = new Ticket();
	    		ticket.setId(rs1.getInt(1));
	    		ticket.setBody(rs1.getString(2));
	    		ticket.setResponse(rs1.getString(3));
	    		ticket.setResolved(rs1.getBoolean(4));
	    		ticket.setFilename(rs1.getString(5));
	    		ticket.setFromUserId(rs1.getInt(6));
	    		
	    		ticketList.add(ticket);
	    	}
    	}
    	catch (SQLException se) { logger.warn("Error occurred in listTickets()!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return ticketList;
    }
    
    @Override
    public int createTicket(Ticket ticket) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	int ticketId = 0;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("insert into tickets (fromUserId, body, response, resolved) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
	  
	    	pstmt.setInt(1, ticket.getFromUserId());
	    	pstmt.setString(2, ticket.getBody());
	    	pstmt.setString(3, "");
	    	pstmt.setInt(4,  0);
	    	pstmt.executeUpdate();
	    	
	    	rs1 = pstmt.getGeneratedKeys();
	    	
	    	if (rs1.next()) {
                ticketId = rs1.getInt(1);
            }
    	}
    	catch (SQLException se) { logger.warn("Error occurred in createTicket()!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return ticketId;
    }
    
    @Override
    public List<Ticket> getTicketsByUserId(int userId){
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	List<Ticket> ticketList = new ArrayList<Ticket>();;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, body, response, resolved, filename from tickets where fromUserId = ?");
	  
	    	pstmt.setInt(1, userId);
	    	rs1 = pstmt.executeQuery();
	    	
	    	while (rs1.next())
	    	{	
	    		Ticket ticket = new Ticket();
	    		ticket.setId(rs1.getInt(1));
	    		ticket.setBody(rs1.getString(2));
	    		ticket.setResponse(rs1.getString(3));
	    		ticket.setResolved(rs1.getBoolean(4));
	    		ticket.setFilename(rs1.getString(5));
	    		ticket.setFromUserId(userId);
	    		
	    		ticketList.add(ticket);
	    	}
    	}
    	catch (SQLException se) { logger.warn("Error occurred in getTicketsByUserId()!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return ticketList;
    }
    
    @Override
    public boolean addAttachmentToTicketById(int ticketId, String filename) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	boolean success = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("update tickets set filename = ? where id = ?");
	 
	    	pstmt.setString(1, filename);
	    	pstmt.setInt(2, ticketId);
	    	pstmt.executeUpdate();
	    	
	    	success = true;
    	}
    	catch (SQLException se) { logger.warn("Error occurred in createTicket()!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return success;
    }
    
    @Override
    public boolean addResponseToTicketById(int ticketId, String response) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	boolean success = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("update tickets set response = ? where id = ?");
	 
	    	pstmt.setString(1, response);
	    	pstmt.setInt(2, ticketId);
	    	pstmt.executeUpdate();
	    	
	    	success = true;
    	}
    	catch (SQLException se) { logger.warn("Error occurred in addResponseToTicketById()!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return success;
    }
    
    @Override
    public Ticket getTicketById(int ticketId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	Ticket ticket = null;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("select id, body, response, resolved, filename, fromUserId from tickets where id = ?");
	 
	    	pstmt.setInt(1, ticketId);
	    	rs1 = pstmt.executeQuery();
	    	
	    	if (rs1.next())
	    	{	
	    		ticket = new Ticket();
	    		ticket.setId(rs1.getInt(1));
	    		ticket.setBody(rs1.getString(2));
	    		ticket.setResponse(rs1.getString(3));
	    		ticket.setResolved(rs1.getBoolean(4));
	    		ticket.setFilename(rs1.getString(5));
	    		ticket.setFromUserId(rs1.getInt(6));
	    	}
    	}
    	catch (SQLException se) { logger.warn("Error occurred in getTicketById()!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return ticket;
    }
    
    @Override
    public boolean deleteTicket(int ticketId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	boolean success = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("delete from tickets where id = ?");
	 
	    	pstmt.setInt(1, ticketId);
	    	pstmt.executeUpdate();
	    	
	    	success = true;
    	}
    	catch (SQLException se) { logger.warn("Error occurred in createTicket()!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return success;
    }

    @Override
    public boolean closeTicket(int ticketId) {
    	DataSource ds = DatabaseUtility.getDataSource();
    	Connection connection = null;
    	ResultSet rs1 = null;
    	PreparedStatement pstmt = null;
    	boolean success = false;
    	
    	try
    	{
	    	connection = ds.getConnection();
	    	pstmt = connection.prepareStatement("update tickets set resolved = 1 where id = " + ticketId);
	    	pstmt.executeUpdate();
	    	
	    	success = true;
    	}
    	catch (SQLException se) { logger.warn("Error occurred in createTicket()!"); logger.warn(se.toString()); }
    	finally
    	{
    		try { connection.close(); } catch (Exception e) {}
    		try { rs1.close(); } catch (Exception e) {}
    		try { pstmt.close(); } catch (Exception e) {}
    	}
    	
    	return success;
    }
}
