package com.mobile16.progetto.showdown.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modella il concetto di una transazione all'interno dell'applicazione
 */
public class Transaction implements ITransaction{
    private TransactionState state = TransactionState.IN_CREATION;
    private String type = "";
    private String details = "";
    private IUser creator;
    private String date;
    private String id;
    private Float cost;

    public Transaction(){}

    public Transaction(String type, String details, String date){
        this.type = type;
        this.details = details;
        this.date = date;
    }

    // GETTERS

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getDetails() {
        return details;
    }

    @Override
    public IUser getCreator() {
        return creator;
    }

    @Override
    public String getDate() {
        return this.date;
    }

    @Override
    public String getID(){
        return this.id;
    }

    @Override
    public Float getCost(){
        return this.cost;
    }

    @Override
    public TransactionState getTransactionState() {
        return state;
    }


    // SETTERS

    @Override
    public void setID(String id){
        this.id = id;
    }

    @Override
    public void setCost(Float cost){
        this.cost = cost;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public void setCreator(IUser creator) {
        this.creator = creator;
    }

    @Override
    public void setDate(String date){
        this.date = date;
    }

    @Override
    public void setTransactionState(TransactionState state){
        this.state = state;
    }


    @Override
    public String toString(){
        return "State: " + state + ", Type: " + type + ", Details: " + details +
                ", Creator: " + creator + ", Date: " + date + ", Cost: " + cost;
    }


    // PARCELABLE METHODS

    public Transaction(Parcel p){
        this.type = p.readString();
        this.details = p.readString();
        this.state = (TransactionState)p.readSerializable();
        this.creator = (IUser)p.readSerializable();
        this.date =  p.readString();
        this.id = p.readString();
        this.cost = p.readFloat();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(details);
        dest.writeSerializable(state);
        dest.writeSerializable(creator);
        dest.writeString(date);
        dest.writeString(id);
        dest.writeFloat(cost);
    }

    // CREATOR required
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
