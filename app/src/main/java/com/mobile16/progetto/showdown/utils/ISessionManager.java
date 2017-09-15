package com.mobile16.progetto.showdown.utils;

import com.mobile16.progetto.showdown.model.User;

/**
 * Session manager utilizzato per ricordare nelle SharedPreferences l'utente loggato.
 */
public interface ISessionManager {
    void setUserLogin(User currentUser);
    void userLogout();
    boolean isLoggedIn();
    User getLoggedUser();
}
