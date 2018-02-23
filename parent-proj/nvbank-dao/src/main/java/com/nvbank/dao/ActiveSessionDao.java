package com.nvbank.dao;


import com.nvbank.model.ActiveSession;
import com.nvbank.model.BankUser;
import java.util.Collection;


public interface ActiveSessionDao {

    Collection<ActiveSession> listSessions();

    ActiveSession getSessionBySessionString(String session);
    
    ActiveSession getSessionByUserId(int userId);
    
    BankUser getUserBySessionString(String session);

    boolean createSession(ActiveSession session);

    void deleteSessionById(int id);
    
    void deleteSessionByUser(BankUser user);
}
