package edu.utsa.cs3443.sweatware_alpha.model;

public class User {
    private String username;
    private String email;
    private String age;
    private String gender;
    private String weight;

    // Constructor
    public User(String username, String email, String age, String gender, String weight) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
    }

    // Getters and Setters
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}