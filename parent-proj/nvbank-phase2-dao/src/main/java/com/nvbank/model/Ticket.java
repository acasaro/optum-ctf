package com.nvbank.model;

public class Ticket {
	private int id;
	private int fromUserId;
	private String body;
	private String response;
	private boolean resolved;
	private String filename;
	
	public Ticket() {
		
	}
	
	public Ticket(int id, int fromUserId, String body, String response, boolean resolved, String filename) {
		this.id = id;
		this.fromUserId = fromUserId;
		this.body = body;
		this.response = response;
		this.resolved = resolved;
		this.filename = filename;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setFromUserId(int id) {
		this.fromUserId = id;
	}
	
	public int getFromUserId() {
		return fromUserId;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setResponse(String resp) {
		this.response = resp;
	}
	
	public String getResponse() {
		return response;
	}
	
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
	
	public boolean getResolved() {
		return resolved;
	}
	
	public void setFilename(String name) {
		this.filename = name;
	}
	
	public String getFilename() {
		return filename;
	}
}
