package com.nvbank.model;

public class Message {
	private int id;
	private int fromUserId;
	private String fromUserName;
	private int toUserId;
	private String toUserName;
	private String subject;
	private String body;
	private boolean read;
	
	public Message() {
		
	}
	
	public Message(int id, int fromUserId, String fromUserName, int toUserId, String toUserName, String subject, String body, boolean read) {
		this.id = id;
		this.fromUserId = fromUserId;
		this.fromUserName = fromUserName;
		this.toUserId = toUserId;
		this.toUserName = toUserName;
		this.subject = subject;
		this.body = body;
		this.read = read;
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
	
	public void setFromUserName(String uname) {
		this.fromUserName = uname;
	}
	
	public String getFromUserName() {
		return fromUserName;
	}
	
	public void setToUserId(int id) {
		this.toUserId = id;
	}
	
	public int getToUserId() {
		return toUserId;
	}
	
	public void setToUserName(String uname) {
		this.toUserName = uname;
	}
	
	public String getToUserName() {
		return toUserName;
	}
	
	public void setSubject(String subj) {
		this.subject = subj;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setRead(boolean read) {
		this.read = read;
	}
	
	public boolean getRead() {
		return read;
	}
}
