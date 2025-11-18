package com.example.fitnessapp.models;

public class User {
    public String UserID;
    public String Username;
    public String Email;
    public String Password;
    public int xp;
    public int current_streak;

    public int age;
    public int weight;
    public int height;
    public String gender;
    public String experience;

    public String goal;



    public User(){}

    public User(String userID, String username, String email){

        this.UserID = userID;
        this.Username = username;
        this.Email = email;
        this.xp = 0;
        this.current_streak = 0;
        this.age = 0;
        this.weight = 0;
        this.height = 0;
        this.gender = "";
        this.experience = "";
        this.goal= "";




    }








}
