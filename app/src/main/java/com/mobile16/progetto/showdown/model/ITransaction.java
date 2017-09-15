package com.mobile16.progetto.showdown.model;

import android.os.Parcelable;

/**
 * Interfaccia che modella il concetto di una transazione all'interno dell'applicazione
 */
public interface ITransaction extends Parcelable {
    String getType();
    String getDetails();
    IUser getCreator();
    String getID();
    Float getCost();
    String getDate();
    TransactionState getTransactionState();

    void setType(String type);
    void setDetails(String details);
    void setCreator(IUser creator);
    void setID(String id);
    void setCost(Float cost);
    void setDate(String date);
    void setTransactionState(TransactionState state);
}
