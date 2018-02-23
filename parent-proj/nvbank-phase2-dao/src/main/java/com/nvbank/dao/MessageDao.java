package com.nvbank.dao;

import com.nvbank.model.Message;
import java.util.List;


public interface MessageDao {

    List<Message> listMessages();
    
    boolean createMessage(Message msg);
    
    List<Message> getMessagesByUserId(int userId);
    
    Message getMessageById(int msgId);
    
    boolean deleteMessage(int msgId);
    
    boolean markMessageAsRead(int msgId);
}

