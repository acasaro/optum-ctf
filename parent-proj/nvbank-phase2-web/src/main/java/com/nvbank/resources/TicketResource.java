package com.nvbank.resources;

import com.nvbank.dao.BankUserDao;
import com.nvbank.dao.TicketDao;
import com.nvbank.dao.ActiveSessionDao;
import com.nvbank.model.ActiveSession;
import com.nvbank.model.BankUser;
import com.nvbank.model.Ticket;
import com.nvbank.model.TicketComment;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;

import java.util.UUID;
import java.util.List;
import java.util.Collection;
import java.util.Base64;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.net.URI;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@RestController
@RequestMapping("/tickets")
public class TicketResource {

    private final TicketDao ticketDao;
    private final BankUserDao bankUserDao;
    private final ActiveSessionDao activeSessionDao;
    
    private static final Logger logger = LoggerFactory.getLogger(TicketResource.class);

    public TicketResource(TicketDao ticketDao, BankUserDao bankUserDao, ActiveSessionDao activeSessionDao) {
        this.bankUserDao = bankUserDao;
        this.ticketDao = ticketDao;
        this.activeSessionDao = activeSessionDao;
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> createTicket(@RequestHeader(value="Cookie", defaultValue="") String cookie, @RequestBody Ticket ticket) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(session.getSessionValue());
    	ticket.setFromUserId(bu.getId());
    	
    	int ticket_id = ticketDao.createTicket(ticket);
        if (ticket_id > 0) { return new ResponseEntity<String>("{\"id\": " + ticket_id + "}", HttpStatus.OK); }
        else { return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getTicket(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
        Ticket ticket = ticketDao.getTicketById(id);
        return new ResponseEntity<Ticket>(ticket, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}/comment", method = RequestMethod.POST)
    public ResponseEntity<?> createTicketComment(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie, @RequestBody Ticket ticket) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(sessionValue);
    	
    	if (bu.getRoleId() != 2) { return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	if (ticketDao.addResponseToTicketById(id, ticket.getResponse())) { return new ResponseEntity<String>("{}", HttpStatus.OK); }
    	else { return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    
    @RequestMapping(value = "/{id}/resolved", method = RequestMethod.GET)
    public ResponseEntity<?> closeTicket(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(sessionValue);
    	
    	if ((bu.getRoleId() != 2) && (ticketDao.getTicketById(id).getFromUserId() != bu.getId())) { 
    		return new ResponseEntity(HttpStatus.FORBIDDEN); 
    	}
    	
    	if (ticketDao.closeTicket(id)) { return new ResponseEntity<String>("{}", HttpStatus.OK); }
    	else { return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    
    @RequestMapping(value = "/{id}/attachment", method = RequestMethod.GET)
    public ResponseEntity<?> getTicketAttachment(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie) throws IOException {
    	String sessionValue = null;
    	try { sessionValue = cookie.split("nvisBankSession=")[1].split(";", 2)[0]; }
    	catch (Exception e) { logger.warn("bad cookie val"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	ActiveSession session = activeSessionDao.getSessionBySessionString(sessionValue);
    	if (session == null) { logger.warn("null session id"); return new ResponseEntity(HttpStatus.FORBIDDEN); }
    	
    	BankUser bu = activeSessionDao.getUserBySessionString(session.getSessionValue());
    	Ticket ticket = ticketDao.getTicketById(id);
    	
    	if ((bu.getRoleId() != 2) && (bu.getId() != ticket.getFromUserId())) {
    		return new ResponseEntity(HttpStatus.FORBIDDEN);
    	}
    	
    	Path path = Paths.get(ticket.getFilename());
    	File file = path.toFile();
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        HttpHeaders headers = new HttpHeaders(); headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName());
	
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    } 
    
    @RequestMapping(value = "/{id}/attachment", method = RequestMethod.POST)
    public ResponseEntity<?> addTicketAttachment(@PathVariable int id, @RequestHeader(value="Cookie", defaultValue="") String cookie, @RequestParam("attachment") MultipartFile attachment) {
    	try {
            byte[] bytes = attachment.getBytes();
            String fileName = attachment.getOriginalFilename();
            
            String dateAppender = new SimpleDateFormat("ddMMyyyy").format(new Date());         
        	String finalFileName = new File("./attachments" + File.separator + dateAppender + "_" + fileName).getCanonicalPath();      
            //Ensure we don't overwrite other user files!
            int counter = 0;
            while (new File(finalFileName).exists()) {
              logger.warn("file already exists! generating a new one");
              finalFileName = new File("./attachments" + File.separator + dateAppender + "_" + String.valueOf(counter) + fileName).getCanonicalPath();
              counter++;
            }
            
            //Make sure the directory exists
            Path uploadPath = Paths.get(finalFileName);
            Path parentDir = uploadPath.getParent();
            if (!Files.exists(parentDir))
                Files.createDirectories(parentDir);

            Files.write(uploadPath, bytes);
            
            if (ticketDao.addAttachmentToTicketById(id, finalFileName)) { return new ResponseEntity<String>("{}", HttpStatus.OK); }
            else { return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR); }            

        } catch (Exception e) {
            logger.warn(e.toString());
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
