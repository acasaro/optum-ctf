package com.nvbank.model;

public class TicketComment {
	private int id;
	private String body;
	private int ticketId;
	private int userId;
	
	public TicketComment() {
		
	}
	
	public TicketComment(int id, String body, int ticketId, int userId) {
		this.id = id;
		this.ticketId = ticketId;
		this.userId = userId;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setTicketId(int id) {
		this.ticketId = id;
	}
	
	public int getTicketId() {
		return ticketId;
	}
	
	public void setUserId(int id) {
		this.userId = id;
	}
	
	public int getUserid() {
		return userId;
	}
}
