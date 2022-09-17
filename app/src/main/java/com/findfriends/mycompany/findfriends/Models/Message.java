package com.findfriends.mycompany.findfriends.Models;



import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class Message {
    private String messageText;
    private Date dateCreated;
    private String receiverId, receiverName, receiverImage;
    private String senderId, senderImage, senderName;

    public Message(){}

    public Message(String messageText, String receiverId, String receiverName,String receiverImage, String senderId, String senderName, String senderImage){
        this.messageText = messageText;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderImage = senderImage;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverId(){return this.receiverId;}

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }

}
