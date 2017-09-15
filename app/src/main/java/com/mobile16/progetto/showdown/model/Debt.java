package com.mobile16.progetto.showdown.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modella il concetto di un debito all'interno dell'applicazione. Essa si discosta
 * leggermente dal concetto di transazione, poichè qui sono presenti in più il concetto del
 * debitore e del creditore della transazione. Utilizzato design pattern DECORATOR.
 */
public class Debt implements IDebt {

    // Design pattern decorator. I metodi di base li richiamo sulla ITransaction originale.
    private final ITransaction transaction = new Transaction();

    private String creditoreUsername;
    private String debitoreUsername;
    private String debitoreId;
    private String creditoreId;
    private String debtId;
    private Float ammontare;
    private String dataChiusura;

    public Debt(){}

    // SETTERS

    @Override
    public void setCreditoreUsername(String username) {
        this.creditoreUsername = username;
    }

    @Override
    public void setDebitoreUsername(String username) {
        this.debitoreUsername = username;
    }

    @Override
    public void setCreditoreId(String id) {
        this.creditoreId = id;
    }

    @Override
    public void setDebitoreId(String id) {
        this.debitoreId = id;
    }

    @Override
    public void setDebtId(String id) {
        this.debtId = id;
    }

    @Override
    public void setAmmontare(Float ammontare) {
        this.ammontare = ammontare;
    }

    @Override
    public void setType(String type) {
        transaction.setType(type);
    }

    @Override
    public void setDetails(String details) {
        transaction.setDetails(details);
    }

    @Override
    public void setCreator(IUser creator) {
        transaction.setCreator(creator);
    }

    @Override
    public void setID(String id) {
        transaction.setID(id);
    }

    @Override
    public void setCost(Float cost) {
        transaction.setCost(cost);
    }

    @Override
    public void setDate(String date) {
        transaction.setDate(date);
    }

    @Override
    public void setTransactionState(TransactionState state) {
        transaction.setTransactionState(state);
    }

    @Override
    public void setDataChiusura(String dataChiusura){
        this.dataChiusura = dataChiusura;
    }


    // GETTERS

    @Override
    public String getCreditoreUsername() {
        return this.creditoreUsername;
    }

    @Override
    public String getDebitoreUsername() {
        return this.debitoreUsername;
    }

    @Override
    public String getCreditoreId() {
        return this.creditoreId;
    }

    @Override
    public String getDebitoreId() {
        return this.debitoreId;
    }

    @Override
    public String getDebtId() {
        return this.debtId;
    }

    @Override
    public Float getAmmontare() {
        return this.ammontare;
    }

    @Override
    public String getType() {
        return transaction.getType();
    }

    @Override
    public String getDetails() {
        return transaction.getDetails();
    }

    @Override
    public IUser getCreator() {
        return transaction.getCreator();
    }

    @Override
    public String getID() {
        return transaction.getID();
    }

    @Override
    public Float getCost() {
        return transaction.getCost();
    }

    @Override
    public String getDate() {
        return transaction.getDate();
    }

    @Override
    public TransactionState getTransactionState() {
        return transaction.getTransactionState();
    }

    @Override
    public String getDataChiusura(){
        return this.dataChiusura;
    }



    @Override
    public String toString(){
        return transaction.toString() + " idDebt " + debtId + " ammontare " + ammontare +
                " CreatoreID " + creditoreId + " creatoreUsername " + creditoreUsername +
                " debitoreID " + debitoreId + " debitoreUsername " + debitoreUsername +
                " dataSaldo " + dataChiusura;
    }



    // PARCELABLE METHODS

    public Debt(Parcel p){
        creditoreUsername = p.readString();
        debitoreUsername = p.readString();
        debitoreId = p.readString();
        creditoreId = p.readString();
        debtId = p.readString();
        ammontare = p.readFloat();
        dataChiusura = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creditoreUsername);
        dest.writeString(debitoreUsername);
        dest.writeString(debitoreId);
        dest.writeString(creditoreId);
        dest.writeString(debtId);
        dest.writeFloat(ammontare);
        dest.writeString(dataChiusura);

    }

    // CREATOR required
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Debt createFromParcel(Parcel in) {
            return new Debt(in);
        }

        public Debt[] newArray(int size) {
            return new Debt[size];
        }
    };
}
