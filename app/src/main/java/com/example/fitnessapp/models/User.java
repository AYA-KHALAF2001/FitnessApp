package com.example.fitnessapp.models;

public class User {
    public String UserID;
    public String Username;
    public String Email;
    public String Password;
    public int xp;
    public int current_streak;
    public String profilephoto_url;

    public User(){}

    public User(String userID, String username, String email){

        this.UserID = userID;
        this.Username = username;
        this.Email = email;
        this.xp = 0;
        this.current_streak = 0;
        this.profilephoto_url = "";


    }








}
