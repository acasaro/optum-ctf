/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nvbank.dao;


import com.nvbank.model.BankUser;
import java.util.List;


public interface BankUserDao {

    List<BankUser> listUsers();

    BankUser getUser(int id);
    
    BankUser getUserByUserName(String username);

    boolean addUser(BankUser user);

    boolean updateUserProfile(int id, BankUser user);

    boolean deleteUser(int id);
    
    boolean incFailedAuth(BankUser user);
    
    boolean resetFailedAuth(BankUser user);
    
    int getLockoutCount(BankUser user);
}
