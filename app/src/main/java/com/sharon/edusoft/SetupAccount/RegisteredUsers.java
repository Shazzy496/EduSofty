package com.sharon.edusoft.SetupAccount;

public class RegisteredUsers {
    public String user_id,profile_image,name,bio,username,email;


   public RegisteredUsers(){

   }

    public RegisteredUsers(String user_id, String profile_image, String name, String bio, String username, String email) {
        this.user_id = user_id;
        this.profile_image = profile_image;
        this.name = name;
        this.bio = bio;
        this.username = username;
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
