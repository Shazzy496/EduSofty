package com.sharon.edusoft.Video;

public class registration {
    String Email,Name,Role,user_id;

    public registration() {
    }

    public registration(String email, String name, String role, String user_id) {
        Email = email;
        Name = name;
        Role = role;
        this.user_id = user_id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
