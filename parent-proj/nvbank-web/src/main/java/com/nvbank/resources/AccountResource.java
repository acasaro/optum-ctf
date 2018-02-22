package com.nvbank.resources;

import com.nvbank.dao.BankUserDao;
import com.nvbank.dao.AccountDao;
import com.nvbank.dao.ActiveSessionDao;
import com.nvbank.model.ActiveSession;
import com.nvbank.model.BankUser;
import com.nvbank.model.Account;
import com.nvbank.model.Txn;

import org.springframework.core.io.FileSystemResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;
import java.util.List;
import java.util.Collection;
import java.util.Base64;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.ws.rs.core.MediaType;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@RestController
@RequestMapping("/account")
public class AccountResource {

    private final AccountDao accountDao;
    private final BankUserDao bankUserDao;
    private final ActiveSessionDao activeSessionDao;
    private static final Logger logger = LoggerFactory.getLogger(AccountResource.class);

    public AccountResource(AccountDao accountDao, BankUserDao bankUserDao, ActiveSessionDao activeSessionDao) {
        this.bankUserDao = bankUserDao;
        this.accountDao = accountDao;
        this.activeSessionDao = activeSessionDao;
    }
    
    @RequestMapping(value = "/{id}/users", method = RequestMethod.GET)
    public ResponseEntity<?> accountUsers(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
        BankUser bankUser = bankUserDao.getUser(session.getUserId());
        if (bankUser == null) { logger.warn("user not found"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
        
        List<Account> allowedAccounts = accountDao.getAccountsByUserId(bankUser.getId());
        for (Account acc : allowedAccounts) {
        	if (acc.getId() == id) {
        		return new ResponseEntity<List<BankUser>>(accountDao.getAccountUsersByAccountId(id), HttpStatus.OK);
        	}
        }
        
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    
    @RequestMapping(value = "/{id}/txns", method = RequestMethod.GET)
    public ResponseEntity<?> accountTxns(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
        BankUser bankUser = bankUserDao.getUser(session.getUserId());
        if (bankUser == null) { logger.warn("user not found"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
        
        List<Account> allowedAccounts = accountDao.getAccountsByUserId(bankUser.getId());
        for (Account acc : allowedAccounts) {
        	if (acc.getId() == id) {
        		List<Txn> txnList = accountDao.getTxnsByAccountId(id);
        		String filename = UUID.randomUUID().toString();
        		
        		try
        		{
	        	    BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	        	    writer.write("Amount,Description");
	        	    
	        	    for (Txn txn : txnList) {
	        	    	writer.write(txn.getAmount() + "," + txn.getDescription());
	        	    }
	        	     
	        	    writer.close();
        		}
	        	catch (IOException ioe)
	        	{
	        		logger.warn("Error writing txn history to file!");
	        		logger.warn(ioe.toString());
	        		return new ResponseEntity(HttpStatus.FORBIDDEN);
	        	}
	       
        		return new ResponseEntity<String>("{\"filename\":\"" + filename + "\"}", HttpStatus.OK);
        	}
        }
        
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    
    @RequestMapping(value = "/txnexport", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM)
    public FileSystemResource accountUsers(String filename, HttpServletResponse response) {
    	response.setHeader("Content-Disposition", "attachment; filename=transaction_history.csv"); 
        return new FileSystemResource(filename); 
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAccount(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
        BankUser bankUser = bankUserDao.getUser(session.getUserId());
        if (bankUser == null) { logger.warn("user not found"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
        
        List<Account> allowedAccounts = accountDao.getAccountsByUserId(bankUser.getId());
        for (Account acc : allowedAccounts) {
        	if (acc.getId() == id) {
        		if (accountDao.deleteAccountById(id)) { return new ResponseEntity<String>("{}", HttpStatus.OK); }
        		else { return new ResponseEntity(HttpStatus.FORBIDDEN); }
        	}
        }
        
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
