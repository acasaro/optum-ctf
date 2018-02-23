package com.nvbank.dao;

import com.nvbank.model.Ticket;

import java.util.List;


public interface TicketDao {

    List<Ticket> listTickets();
    
    int createTicket(Ticket ticket);
    
    List<Ticket> getTicketsByUserId(int userId);
    
    Ticket getTicketById(int ticketId);
    
    boolean deleteTicket(int ticketId);
    
    boolean addAttachmentToTicketById(int ticketId, String filename);
    
    boolean addResponseToTicketById(int ticketId, String response);
    
    boolean closeTicket(int ticketId);
}

