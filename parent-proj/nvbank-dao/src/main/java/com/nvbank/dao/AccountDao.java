package com.nvbank.dao;

import com.nvbank.model.Account;
import com.nvbank.model.BankUser;
import com.nvbank.model.Txn;
import java.util.List;


public interface AccountDao {

    List<Account> listAccounts();

    Account getAccountById(int accountId);
    
    Account getAccountByAccountNumber(int accountNum);
    
    List<Account> getAccountsByUserId(int userId);
    
    List<BankUser> getAccountUsersByAccountId(int accId);

    boolean createAccount(Account accounts);
    
    boolean associateUserWithAccount(Account account, BankUser bu);

    boolean deleteAccountById(int accountId);
    
    boolean deleteAccountsByUserId(int userId);
    
    List<Txn> getTxnsByAccountId(int accId);
}
