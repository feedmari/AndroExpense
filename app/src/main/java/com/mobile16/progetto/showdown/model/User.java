package com.mobile16.progetto.showdown.model;

/**
 * Modella il concetto di un utente all'interno dell'applicazione
 */
public class User implements IUser {
    private final String mail;
    private final String password;

    private String username = null;
    private Integer userID = null;

    // Costruttori pubblici

    public User(String mail, String password){
        this.mail = mail;
        this.password = password;
    }

    public User(String mail, String password, String username){
        this.mail = mail;
        this.password = password;
        this.username = username;
    }

    public User(String mail, String password, String username, Integer userID){
        this.mail = mail;
        this.password = password;
        this.username = username;
        this.userID = userID;
    }

    public User(IUser user){
        this.mail = user.getMail();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.userID = user.getUserId();
    }

    @Override
    public String getMail(){
        return this.mail;
    }

    @Override
    public String getPassword(){
        return this.password;
    }

    @Override
    public String getUsername(){
        if(isUsernameSet()) {
            return this.username;
        } else{
            return "Ivanov Mistupischi";
        }
    }

    @Override
    public Integer getUserId(){
        return this.userID;
    }

    @Override
    public boolean isUsernameSet(){
        return this.username != null;
    }

    @Override
    public boolean isUserIdSet(){
        return this.userID != null;
    }

    @Override
    public void setUsername(String username){
        this.username = username;
    }

    @Override
    public void setUserId(Integer userID){
        this.userID = userID;
    }

    @Override
    public String toString(){
        return "Mail: " + mail + ", Password: " + password +
                ", Username: " + username + ", UserID: " + userID;
    }
}
