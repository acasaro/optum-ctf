package com.nvbank.resources;

//import com.nvbank.resources.PhaseTwoTemplateResource;

import com.nvbank.dao.ActiveSessionDao;
import com.nvbank.dao.AccountDao;
import com.nvbank.dao.BankUserDao;
import com.nvbank.model.ActiveSession;
import com.nvbank.model.BankUser;
import com.nvbank.model.Account;

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
public class TemplateResource {
	
	private static final Logger logger = LoggerFactory.getLogger(TemplateResource.class); 
	private final ActiveSessionDao activeSessionDao;
	private final AccountDao accountDao;
	private final BankUserDao bankUserDao;
    

    public TemplateResource(ActiveSessionDao activeSessionDao, AccountDao accountDao, BankUserDao bankUserDao) {
        this.activeSessionDao = activeSessionDao;
        this.accountDao = accountDao;
        this.bankUserDao = bankUserDao;
    }
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(@RequestHeader(value="Cookie", defaultValue="") String cookie) {
		String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return "login"; }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return "login"; }
    	
    	return "redirect:dashboard";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register() {
		return "register";
	}
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String dashboard(@RequestHeader(value="Cookie", defaultValue="") String cookie, Model model) {
		String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return "login"; }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return "login"; }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(sessionValue);
    	
    	if (bu.getRoleId() == 1) { 
	    	model.addAttribute("bankuser", bu);
	    	
	    	List<Account> accountList = accountDao.getAccountsByUserId(bu.getId());
	    	model.addAttribute("accountList", accountList);
	    	
	    	return "dashboard";
    	}
    	else if (bu.getRoleId() == 2) {
    		return "redirect:phase-2/tickets";
    	}
    	else {
        	return "redirect:admin/user-management";
    	}
	}
	
	@RequestMapping(value = "/profile-details", method = RequestMethod.GET)
	public String profileDetails(@RequestHeader(value="Cookie", defaultValue="") String cookie, Model model) {
		String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return "login"; }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return "login"; }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(sessionValue);
    	model.addAttribute("bankuser", bu);

    	return "profile-details";
	}
	
	/*
	@RequestMapping(value = "/profile-details/{userId}", method = RequestMethod.GET)
	public String profileDetails(@PathVariable int userId, @RequestHeader(value="Cookie", defaultValue="") String cookie, Model model) {
		String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return "login"; }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return "login"; }
    	
    	BankUser bu = bankUserDao.getUser(userId);
    	model.addAttribute("bankuser", bu);

    	return "profile-details";
	}
	*/
	
	@RequestMapping(value = "/account-details", method = RequestMethod.GET)
	public String account_overview(@RequestHeader(value="Cookie", defaultValue="") String cookie, Model model) {
		String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return "login"; }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return "login"; }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(sessionValue);
    	List<Account> acc_list = accountDao.getAccountsByUserId(bu.getId());
    	model.addAttribute("accountList", acc_list);
    	
    	return "account-details";
	}
	
	/*
	@RequestMapping(value = "/account-details/{accId}", method = RequestMethod.GET)
	public String account_details(@PathVariable int accId, @RequestHeader(value="Cookie", defaultValue="") String cookie, Model model) {
		String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return "login"; }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return "login"; }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(sessionValue);
    	
    	if (bu.getRoleId() != 3) { 
    		//TODO: check if user has accountmapping entry
	    	return "redirect:/login";
    	}
    	
		Account acc = accountDao.getAccountById(accId);
    	model.addAttribute("account", acc);
    	
    	return "account-details";
	}
	*/
	
	@RequestMapping(value = "/admin/user-management", method = RequestMethod.GET)
	public String user_management(@RequestHeader(value="Cookie", defaultValue="") String cookie, Model model) {
		String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return "login"; }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return "login"; }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(sessionValue);
    	
    	if (bu.getRoleId() <= 1) { 
	    	return "redirect:/login";
    	}
    	else {
    		List<BankUser> userList = bankUserDao.listUsers();
        	model.addAttribute("userlist", userList);
        	
        	return "user-management";
    	}
	}
	
	@RequestMapping(value = "/admin/account-management", method = RequestMethod.GET)
	public String account_management(@RequestHeader(value="Cookie", defaultValue="") String cookie, Model model) {
		String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { return "login"; }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { return "login"; }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(sessionValue);
    	
    	if (bu.getRoleId() != 3) { 
	    	return "redirect:/login";
    	}
    	else {
    		List<Account> accountList = accountDao.listAccounts();
        	model.addAttribute("accountList", accountList);
        	
        	return "account-management";
    	}
	}
}