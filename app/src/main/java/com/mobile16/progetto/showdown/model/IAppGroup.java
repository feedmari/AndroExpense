package com.mobile16.progetto.showdown.model;

import android.os.Parcelable;

import java.util.List;

/**
 * Interfaccia che modella il concetto di un gruppo all'interno dell'applicazione
 */
public interface IAppGroup extends Parcelable{
    String getGroupName();
    String getGroupDescription();
    String getLastModifiedDate();

    List<String> getUsersList();
    Integer getGroupId();

    void insertUserEmail(String email);
    void insertEmails(List<String> emails);
}
