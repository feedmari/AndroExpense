package com.mobile16.progetto.showdown.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Modella il concetto di un gruppo all'interno dell'applicazione
 */
public class AppGroup implements  IAppGroup{

    private final String groupName;
    private final String groupDescription;
    private Integer groupId;
    private String lastModified;

    private List<String> emailList = new ArrayList<>();

    // Costruttori pubblici

    public AppGroup(String groupName, String groupDescription){
        this.groupName = groupName;
        this.groupDescription = groupDescription;
    }

    public AppGroup(String groupName, String groupDescription, int groupId){
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupId = groupId;
    }

    public AppGroup(String groupName, String groupDescription, int groupId, String lastModified){
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupId = groupId;
        this.lastModified = lastModified;
    }

    @Override
    public String getGroupName() {
        return this.groupName;
    }

    @Override
    public String getGroupDescription() {
        return this.groupDescription;
    }

    @Override
    public void insertUserEmail(String email){
        emailList.add(email);
    }

    @Override
    public void insertEmails(List<String> emails) { this.emailList = new ArrayList<>(emails); }

    @Override
    public List<String> getUsersList(){
        return new ArrayList<>(this.emailList);
    }

    @Override
    public Integer getGroupId(){
        return this.groupId;
    }

    @Override
    public String getLastModifiedDate(){
        return this.lastModified;
    }

    @Override
    public String toString(){
        return "Group name: " + groupName + ", Group description: " + groupDescription +
                ", Group id: " + groupId + ". ";
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(obj == null || obj.getClass() != this.getClass()){
            return false;
        }
        return ((IAppGroup)obj).getGroupId() == groupId;
    }


    // PARCELABLE METHODS

    public AppGroup(Parcel p){
        this.groupName = p.readString();
        this.groupDescription = p.readString();
        this.groupId = p.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupName);
        dest.writeString(groupDescription);
        dest.writeInt(groupId); // N.B. il tipo int non può essere null, non è un oggetto.
    }

    // CREATOR required
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public AppGroup createFromParcel(Parcel in) {
            return new AppGroup(in);
        }

        public AppGroup[] newArray(int size) {
            return new AppGroup[size];
        }
    };
}
