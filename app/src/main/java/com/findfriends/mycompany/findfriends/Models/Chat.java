package com.findfriends.mycompany.findfriends.Models;

import java.util.List;

public class Chat {

    private List<String> senderReceiverList;
    private String userId;
    private String userName;
    private String date;


    public Chat(){}

    public Chat(List<String> senderReceiverList, String userId, String userName, String date) {
        this.senderReceiverList = senderReceiverList;
        this.userId = userId;
        this.userName = userName;
        this.date = date;
    }
    public List<String> getSenderReceiverList() {
        return senderReceiverList;
    }
    public String getUserId(){return this.userId;}
    public String getUserName(){return this.userName;}
    public String getDate(){return this.date;}


}
