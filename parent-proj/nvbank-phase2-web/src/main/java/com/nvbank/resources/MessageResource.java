package com.nvbank.resources;

import com.nvbank.dao.BankUserDao;
import com.nvbank.dao.MessageDao;
import com.nvbank.dao.ActiveSessionDao;
import com.nvbank.model.ActiveSession;
import com.nvbank.model.BankUser;
import com.nvbank.model.Message;

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
@RequestMapping("/messages")
public class MessageResource {

    private final MessageDao messageDao;
    private final BankUserDao bankUserDao;
    private final ActiveSessionDao activeSessionDao;
    private static final Logger logger = LoggerFactory.getLogger(MessageResource.class);

    public MessageResource(MessageDao messageDao, BankUserDao bankUserDao, ActiveSessionDao activeSessionDao) {
        this.bankUserDao = bankUserDao;
        this.messageDao = messageDao;
        this.activeSessionDao = activeSessionDao;
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> sendMessage(@RequestHeader(value="Cookie", defaultValue="") String cookie, @RequestBody Message msg) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(session.getSessionValue());
    	msg.setFromUserId(bu.getId());
    	
        if (messageDao.createMessage(msg)) { return new ResponseEntity<String>("{}", HttpStatus.OK); }
        else { return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMessage(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
        Message msg = messageDao.getMessageById(id);
        return new ResponseEntity<Message>(msg, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMessage(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(session.getSessionValue());
    	Message msg = messageDao.getMessageById(id);
    	
    	if (msg.getFromUserId() != bu.getId() && msg.getToUserId() != bu.getId()) { return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
        if (messageDao.deleteMessage(id)) { return new ResponseEntity<String>("{}", HttpStatus.OK); }
        else { return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR); }
        
    }
    
    @RequestMapping(value = "/{id/read}", method = RequestMethod.GET)
    public ResponseEntity<?> markMsgRead(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) {    	
    	if (messageDao.markMessageAsRead(id)) { return new ResponseEntity<String>("{}", HttpStatus.OK); }
        else { return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR); }
    }
}
