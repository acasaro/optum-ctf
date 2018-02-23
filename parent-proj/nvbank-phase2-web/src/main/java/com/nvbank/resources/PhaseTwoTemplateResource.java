package com.nvbank.resources;

import com.nvbank.dao.ActiveSessionDao;
import com.nvbank.dao.MessageDao;
import com.nvbank.dao.BankUserDao;
import com.nvbank.dao.TicketDao;
import com.nvbank.model.ActiveSession;
import com.nvbank.model.BankUser;
import com.nvbank.model.Message;
import com.nvbank.model.Ticket;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Controller
@RequestMapping("/phase-2")
public class PhaseTwoTemplateResource {
	
	private static final Logger logger = LoggerFactory.getLogger(PhaseTwoTemplateResource.class); 
	private final ActiveSessionDao activeSessionDao;
	private final MessageDao messageDao;
	private final BankUserDao bankUserDao;
	private final TicketDao ticketDao;
    

    public PhaseTwoTemplateResource(ActiveSessionDao activeSessionDao, MessageDao messageDao, BankUserDao bankUserDao, TicketDao ticketDao) {
        this.activeSessionDao = activeSessionDao;
        this.messageDao = messageDao;
        this.bankUserDao = bankUserDao;
        this.ticketDao = ticketDao;
    }
	
	@RequestMapping(value = "/messages", method = RequestMethod.GET)
	public String messagesOverview(@RequestHeader(value="Cookie", defaultValue="") String cookie, Model model) {
		String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return "login"; }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return "login"; }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(sessionValue);
    	
    	if (bu.getRoleId() == 1) {
	    	List<Message> message_list= messageDao.getMessagesByUserId(bu.getId());
	    	model.addAttribute("messageList", message_list);
	    	
	    	return "messages-overview";
    	}
    	else if (bu.getRoleId() == 2) { return "login"; }
    	else {
    		List<Message> message_list= messageDao.listMessages();
	    	model.addAttribute("messageList", message_list);
	    	
	    	return "messages-admin";
    	}
	}
	
	@RequestMapping(value = "/tickets", method = RequestMethod.GET)
	public String ticketsOverview(@RequestHeader(value="Cookie", defaultValue="") String cookie, Model model) {
		String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return "login"; }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return "login"; }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(sessionValue);
    	
    	if (bu.getRoleId() > 1) {
	    	List<Ticket> ticket_list= ticketDao.listTickets();
	    	model.addAttribute("ticketList", ticket_list);
	    	
	    	return "tickets-csr";
    	}
    	else { 
    		List<Ticket> ticket_list= ticketDao.getTicketsByUserId(bu.getId());
	    	model.addAttribute("ticketList", ticket_list);
	    	
    		return "tickets"; 
    	}
	}
}