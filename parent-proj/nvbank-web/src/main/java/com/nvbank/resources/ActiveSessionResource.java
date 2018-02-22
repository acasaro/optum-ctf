package com.nvbank.resources;

import com.nvbank.dao.BankUserDao;
import com.nvbank.dao.ActiveSessionDao;
import com.nvbank.model.BankUser;
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
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.commons.text.RandomStringGenerator;

@RestController
@RequestMapping("/sessions")
public class ActiveSessionResource {

    private final ActiveSessionDao activeSessionDao;
    private final BankUserDao bankUserDao;
    private static final Logger logger = LoggerFactory.getLogger(ActiveSessionResource.class);

    public ActiveSessionResource(ActiveSessionDao activeSessionDao, BankUserDao bankUserDao) {
        this.bankUserDao = bankUserDao;
        this.activeSessionDao = activeSessionDao;
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> authenticate(@RequestBody BankUser user, HttpServletResponse response) {
        BankUser dbUser = bankUserDao.getUserByUserName(user.getUserName());
        if (dbUser == null) { return new ResponseEntity(HttpStatus.FORBIDDEN); }
        
        if (!dbUser.getPassword().equals(user.getPassword())) { 
        	bankUserDao.incFailedAuth(dbUser);
        	return new ResponseEntity(HttpStatus.UNAUTHORIZED); 
        }
        
        if (bankUserDao.getLockoutCount(dbUser) > 5) {
        	return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        
        activeSessionDao.deleteSessionByUser(dbUser);
        bankUserDao.resetFailedAuth(dbUser);
        
        String sessionValue = generateRandomString();
        ActiveSession session = new ActiveSession();
        session.setSessionValue(sessionValue);
        session.setUserId(dbUser.getId());
        activeSessionDao.createSession(session);
        
        Cookie cookie = new Cookie("nvisBankSession", sessionValue);
        cookie.setMaxAge(265 * 24 * 60 * 60);  // (s)
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setContentType("application/json");
        
        return new ResponseEntity<String>("{}", HttpStatus.OK);
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity logout(HttpServletResponse response) {
    	Cookie cookie = new Cookie("nvisBankSession", "");
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setContentType("application/json");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");    
        return new ResponseEntity<String>(headers, HttpStatus.FOUND);
    }
    
    private String generateRandomString()
    {
    	List<Character> validChars = new ArrayList<Character>();
    	java.util.Random rand = new java.util.Random(System.currentTimeMillis() / 1000l);
    	
    	for (char c = 'a'; c <= 'z'; c++) {
    		validChars.add(c);
    	}
    	
    	for (char c = 'A'; c <= 'Z'; c++) {
    		validChars.add(c);
    	}
    	
    	for (char c = '0'; c <= '9'; c++) {
    		validChars.add(c);
    	}
    	
    	char[] session = new char[10];
    	for (int i=0; i<10; i++)
    	{
    		session[i] = validChars.get(rand.nextInt(validChars.size()));
    	}
    	
    	logger.info("generated session value: " + String.valueOf(session));
    	return String.valueOf(session);
    }
    
}
