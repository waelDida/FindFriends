package com.findfriends.mycompany.findfriends.Api;

import com.findfriends.mycompany.findfriends.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserApi {

    private static final String COLLECTION_NAME = "users";

    private static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createUser(String uid, String userName, String profileImg, String gender,
                                        String birthday, String minAge, String maxAge, String shownGender, String loginDate, String showAge,
                                        List<String> imageList, List<String> likesList, List<String> reportList, List<String> registrationTokens,
                                        List<String> unmatchedList, List<String> chatList, String aboutMe, String activity,double latitude, double longitude, int maxDistance){
        User userToCreate = new User(uid,userName,profileImg,gender,birthday,minAge,maxAge,shownGender,loginDate,showAge,
                new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>(),
                "","",latitude,longitude, maxDistance);
        return UserApi.getUsersCollection().document(uid).set(userToCreate);

    }

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserApi.getUsersCollection().document(uid).get();
    }

    public static Task<Void> updateImages (String uid, String imageUrl){
        return UserApi.getUsersCollection().document(uid).update("imageList", FieldValue.arrayUnion(imageUrl));
    }

    public static Task<Void> updateLikesList(String uid, String likedUserId){
        return UserApi.getUsersCollection().document(uid).update("likesList", FieldValue.arrayUnion(likedUserId));
    }

    public static Task<Void> deleteFromLikesList(String uid, String likedUserId){
        return UserApi.getUsersCollection().document(uid).update("likesList",FieldValue.arrayRemove(likedUserId));
    }

    public static Task<Void> updateReportList(String uid, String reportedUserId){
        return UserApi.getUsersCollection().document(uid).update("reportList",FieldValue.arrayUnion(reportedUserId));
    }

    public static Task<Void> updateAboutMe(String uid, String aboutText){
        return UserApi.getUsersCollection().document(uid).update("aboutMe",aboutText);
    }

    public static Task<Void> updateActivity(String uid, String activityText){
        return UserApi.getUsersCollection().document(uid).update("activity",activityText);
    }

    public static  Task<Void> updateVerificationId(String uid, String verificationId){
        return UserApi.getUsersCollection().document(uid).update("verificationId",verificationId);
    }

    public static Task<Void> updateSmsCode(String uid, String smsCode){
        return UserApi.getUsersCollection().document(uid).update("smsCode",smsCode);
    }

    public static Task<Void> updateMinAge(String uid, String age){
        return UserApi.getUsersCollection().document(uid).update("minAge",age);
    }

    public static Task<Void> updateMaxAge(String uid, String age){
        return UserApi.getUsersCollection().document(uid).update("maxAge",age);
    }

    public static Task<Void> updateShownGender(String uid, String gender){
        return UserApi.getUsersCollection().document(uid).update("shownGender",gender);
    }

    public static Task<Void> updateRegistrationToken(String uid, String token){
        return UserApi.getUsersCollection().document(uid).update("registrationTokens",FieldValue.arrayUnion(token));
    }

    public static Task<Void> deleteUser(String uid){
        return UserApi.getUsersCollection().document(uid).delete();
    }

    public static Task<Void> updateUserInfo(String uid, String field, String value){
        return UserApi.getUsersCollection().document(uid).update(field,value);
    }

    public static Task<Void> updateUserLikes (String uid, String field, int value){
        return UserApi.getUsersCollection().document(uid).update(field, value);
    }

    public static Task<Void> deleteFromChatList(String uid, String fid){
        return UserApi.getUsersCollection().document(uid).update("chatList",FieldValue.arrayRemove(fid));
    }

    public static Task<Void> updateChatList(String uid, String fid){
        return UserApi.getUsersCollection().document(uid).update("chatList",FieldValue.arrayUnion(fid));
    }

    public static Task<Void> deleteImage (String uid, String imageUrl){
        return UserApi.getUsersCollection().document(uid).update("imageList",FieldValue.arrayRemove(imageUrl));
    }

    public static Task<QuerySnapshot> getUsersHaveCurrentIdInLikesList(String id){
        return getUsersCollection().whereArrayContains("likesList",id).get();
    }

    public static Task<QuerySnapshot> getUsersHaveCurrentIdInChatList(String id){
        return getUsersCollection().whereArrayContains("chatList",id).get();
    }

    public static Task<QuerySnapshot> getAllUsers(String gender){
        return getUsersCollection()
                .whereEqualTo("gender",gender)
                .get();
    }

    public static Task<Void> updateUnmatchedList(String uid, String fid){
        return UserApi.getUsersCollection().document(uid).update("unmatchedList",FieldValue.arrayUnion(fid));
    }

    public static Task<Void> updateLocation(String uid, String field, double value){
        return UserApi.getUsersCollection().document(uid).update(field,value);
    }

    public static Task<Void> updateMaxDistance(String uid, int value){
        return UserApi.getUsersCollection().document(uid).update("maxDistance",value);
    }




}
