package com.mobile16.progetto.showdown.model;

import java.io.Serializable;

/**
 * Interfaccia che modella il concetto di un utente all'interno dell'applicazione
 */
public interface IUser extends Serializable {
    String getMail();
    String getPassword();

    String getUsername();
    void setUsername(String username);
    boolean isUsernameSet();

    Integer getUserId();
    void setUserId(Integer userId);
    boolean isUserIdSet();


}
