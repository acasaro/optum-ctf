package com.nvbank.resources;

import com.nvbank.dao.BankUserDao;
import com.nvbank.model.BankUser;

import org.springframework.stereotype.Controller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Controller
@RequestMapping("/static")
public class StaticResource {
	
	private static final Logger logger = LoggerFactory.getLogger(StaticResource.class); 
	
	/*
	@GET
	@Path("/{file: [a-zA-Z\\-\\_]*}")
	@Produces({MediaType.TEXT_HTML})
	public InputStream viewHTML(@PathParam("file") String filePath)
	{
		return this.getClass().getResourceAsStream("/" + filePath + ".html");
	}
	
	@GET
	@Path("/js/{file: [a-zA-Z0-9\\-\\_\\.]*}")
	@Produces({MediaType.WILDCARD})
	public InputStream viewJS(@PathParam("file") String filePath)
	{
		return this.getClass().getResourceAsStream("/js/" + filePath);
	}
	
	@GET
	@Path("/css/{file: [a-zA-Z0-9\\-\\_\\.]*}")
	@Produces({MediaType.WILDCARD})
	public InputStream viewCSS(@PathParam("file") String filePath)
	{
		return this.getClass().getResourceAsStream("/css/" + filePath);
	}
	
	@GET
	@Path("/fonts/{file: [a-zA-Z0-9\\-\\_\\.]*}")
	@Produces({MediaType.WILDCARD})
	public InputStream viewFonts(@PathParam("file") String filePath)
	{
		return this.getClass().getResourceAsStream("/fonts/" + filePath);
	}
	*/
}