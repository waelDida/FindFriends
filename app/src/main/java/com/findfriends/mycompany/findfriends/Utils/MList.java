package com.findfriends.mycompany.findfriends.Utils;

import com.findfriends.mycompany.findfriends.Models.User;

import java.util.ArrayList;
import java.util.List;

public class MList {

    private  List<User> myList = new ArrayList<>();
    public void addToList(User user){
        myList.add(user);
    }

    public List<User> getList(){
        return myList;
    }
}
