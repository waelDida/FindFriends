package com.findfriends.mycompany.findfriends.Api;

import com.findfriends.mycompany.findfriends.Models.Chat;
import com.findfriends.mycompany.findfriends.Models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ChatTestApi {

    private static final String COLLECTION_NAME = "chatsTest";
    private static final String SUB_COLLECTION_NAME ="messages";

    private static CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createChat(List<String> senderReceiverList, String userId, String userName, String date){
        Chat chat = new Chat(senderReceiverList,userId,userName,date);
        return ChatTestApi.getChatCollection().document().set(chat);
    }

    public static Task<DocumentReference> createMessageForChat(String id, String messageText, String receiverId, String receiverName, String receiverImage, String senderId, String senderName, String senderImage){
        Message message = new Message(messageText, receiverId,receiverName,receiverImage,senderId, senderName, senderImage);
        return ChatTestApi.getChatCollection().document(id).collection(SUB_COLLECTION_NAME).add(message);
    }

    public static Task<QuerySnapshot> getDocumentId(String uid){
        return ChatTestApi.getChatCollection()
                .whereArrayContains("senderReceiverList",uid)
                .get();
    }

    public static Query getAllMessageForChat(String roomId){
        return ChatTestApi.getChatCollection()
                .document(roomId)
                .collection(SUB_COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }
    public static Task<Void> deleteChat(String id){
        return ChatTestApi.getChatCollection().document(id).delete();
    }
}
