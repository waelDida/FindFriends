package com.findfriends.mycompany.findfriends.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class User implements Parcelable {

    private String  uid, userName,profileImg;
    private String aboutMe;
    private String gender;
    private String birthday;
    private String minAge, maxAge;
    private String shownGender;
    private String loginDate;
    private String showAge;
    private List<String> imageList;
    private List<String> likesList,reportList;
    private List<String> registrationTokens;
    private List<String> unmatchedList;
    private List<String> chatList;

    private String verificationId;
    private String smsCode;
    private String activity;
    private double latitude;
    private double longitude;
    private int maxDistance;

    public User(){}

    public User(String uid, String userName, String profileImg,String gender,
                String birthday, String minAge, String maxAge, String shownGender, String loginDate, String showAge,
                List<String> imageList, List<String> likesList, List<String> reportList, List<String> registrationTokens,
                List<String> unmatchedList, List<String> chatList, String aboutMe, String activity, double latitude, double longitude,
                int maxDistance) {
        this.uid = uid;
        this.userName = userName;
        this.profileImg = profileImg;
        this.gender = gender;
        this.birthday = birthday;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.shownGender = shownGender;
        this.loginDate = loginDate;
        this.showAge = showAge;
        this.imageList = imageList;
        this.likesList = likesList;
        this.reportList = reportList;
        this.registrationTokens = registrationTokens;
        this.unmatchedList = unmatchedList;
        this.chatList = chatList;
        this.aboutMe = aboutMe;
        this.activity = activity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.maxDistance = maxDistance;
    }

    protected User(Parcel in) {
        uid = in.readString();
        userName = in.readString();
        profileImg = in.readString();
        aboutMe = in.readString();
        gender = in.readString();
        birthday = in.readString();
        minAge = in.readString();
        maxAge = in.readString();
        shownGender = in.readString();
        loginDate = in.readString();
        showAge = in.readString();
        imageList = in.createStringArrayList();
        likesList = in.createStringArrayList();
        reportList = in.createStringArrayList();
        registrationTokens = in.createStringArrayList();
        unmatchedList = in.createStringArrayList();
        chatList = in.createStringArrayList();
        verificationId = in.readString();
        smsCode = in.readString();
        activity = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        maxDistance = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getMinAge() {
        return minAge;
    }

    public String getMaxAge() {
        return maxAge;
    }

    public String getShownGender() {
        return shownGender;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public String getShowAge(){return showAge;}

    public List<String> getImageList() {
        return imageList;
    }

    public List<String> getLikesList() {
        return likesList;
    }

    public List<String> getReportList() {
        return reportList;
    }

    public List<String> getRegistrationTokens() {
        return registrationTokens;
    }

    public List<String> getUnmatchedList() {
        return unmatchedList;
    }

    public List<String> getChatList() {
        return chatList;
    }

    public String getVerificationId(){return verificationId;}

    public String getSmsCode(){return smsCode;}

    public String getActivity(){return activity;}

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public int getMaxDistance(){return maxDistance;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(userName);
        parcel.writeString(profileImg);
        parcel.writeString(aboutMe);
        parcel.writeString(gender);
        parcel.writeString(birthday);
        parcel.writeString(minAge);
        parcel.writeString(maxAge);
        parcel.writeString(shownGender);
        parcel.writeString(loginDate);
        parcel.writeString(showAge);
        parcel.writeStringList(imageList);
        parcel.writeStringList(likesList);
        parcel.writeStringList(reportList);
        parcel.writeStringList(registrationTokens);
        parcel.writeStringList(unmatchedList);
        parcel.writeStringList(chatList);
        parcel.writeString(verificationId);
        parcel.writeString(smsCode);
        parcel.writeString(activity);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeInt(maxDistance);
    }
}
