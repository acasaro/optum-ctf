package com.nvbank.model;

public class AccountLinkRequest {
	private int id;
	private int fromUserId;
	private int toUserId;
	private int accountId;
	private int status;

	public AccountLinkRequest() {
		
	}
	
	public AccountLinkRequest(int id, int fromUserId, int toUserId, int accountId, int status){
		this.id = id;
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.accountId = accountId;
		this.status = status;
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
	
	public void setToUserid(int id) {
		this.toUserId = id;
	}
	
	public int getToUserId() {
		return toUserId;
	}
	
	public void setAccountId(int id) {
		this.accountId = id;
	}
	
	public int getAccountId(int id) {
		return accountId;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
}
