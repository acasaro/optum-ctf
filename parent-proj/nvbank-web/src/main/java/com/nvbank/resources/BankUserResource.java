package com.nvbank.resources;

import com.nvbank.dao.BankUserDao;
import com.nvbank.dao.AccountDao;
import com.nvbank.dao.ActiveSessionDao;
import com.nvbank.model.BankUser;
import com.nvbank.model.Account;
import com.nvbank.model.ActiveSession;

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

import java.util.Random;
import java.util.Collection;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@RestController
@RequestMapping("/users")
public class BankUserResource {
	private static final Logger logger = LoggerFactory.getLogger(BankUserResource.class);

    private final BankUserDao bankUserDao;
    private final ActiveSessionDao activeSessionDao;
    private final AccountDao accountDao;
     

    public BankUserResource(BankUserDao bankUserDao, AccountDao accountDao, ActiveSessionDao activeSessionDao) {
        this.bankUserDao = bankUserDao;
        this.accountDao = accountDao;
        this.activeSessionDao = activeSessionDao;
    }
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity getBankUserBySession(@RequestHeader(value="Cookie", defaultValue="") String cookie) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
        BankUser bankUser = bankUserDao.getUser(session.getUserId());
        if (bankUser == null) { logger.warn("user not found"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
        
        return new ResponseEntity<BankUser>(bankUser, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity createBankUser(@RequestBody BankUser bankUser) {
        if (bankUserDao.addUser(bankUser)) {
        	BankUser bu = bankUserDao.getUserByUserName(bankUser.getUserName());
        	
        	Account account = new Account();
        	account.setAccountNumber(genAccNumber());
        	account.setBalance(0.0);
        	accountDao.createAccount(account);
        	
        	Account acc = accountDao.getAccountByAccountNumber(account.getAccountNumber());
        	accountDao.associateUserWithAccount(acc, bu);
        
        	return new ResponseEntity<String>("{}", HttpStatus.OK);
        }
        else {
        	logger.warn("User creation failed!");
        	return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getBankUserById(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) { //throws NotFoundException, ForbiddenException {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
        BankUser bankUser = bankUserDao.getUser(id);
        if (bankUser == null) { logger.warn("user not found"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
        
        //return bankUser;
        return new ResponseEntity<BankUser>(bankUser, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}/unlock", method = RequestMethod.GET)
    public ResponseEntity unlockBankUser(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) { //throws NotFoundException, ForbiddenException {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
        BankUser bankUser = bankUserDao.getUser(id);
        if (bankUser == null) { logger.warn("user not found"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
        
        if (bankUserDao.resetFailedAuth(bankUser)) { return new ResponseEntity<BankUser>(bankUser, HttpStatus.OK); }
        else { return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR); }
        
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity updateBankUser(@PathVariable int id, @RequestBody BankUser updatedBankUser, @RequestHeader(value="Cookie", defaultValue="") String cookie) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	if (bankUserDao.updateUserProfile(id, updatedBankUser)) { return new ResponseEntity("{}", HttpStatus.OK); }
    	else {
    		logger.warn("User update failed!");
    		return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteBankUser(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	BankUser bankUser = bankUserDao.getUser(session.getUserId());
    	if (bankUser.getRoleId() == 1 && session.getUserId() != id) { return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	if (bankUserDao.deleteUser(id)) { return new ResponseEntity("{}", HttpStatus.OK); }
    	else {
        	logger.warn("User deletion failed!");
        	return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    private int genAccNumber() {
    	Random rnd = new Random();
    	return 100000000 + rnd.nextInt(900000000);
    }

}
