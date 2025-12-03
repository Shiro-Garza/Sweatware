package edu.utsa.cs3443.sweatware_alpha.model;

/**
 * Represents a user in the Sweatware application.
 * <p>
 * A user has a username, email, age, gender, and weight.
 * This model is used throughout the application to store
 * and transfer user profile information.
 * </p>
 * <p>Instances of this class are typically created upon login
 * and updated when the user edits their profile.</p>
 * @author Aiden Garvett
 * @version final
 */
public class User {

    /** The unique username of the user. */
    private String username;

    /** The user's email address. */
    private String email;

    /** The user's age (stored as a string for simplicity). */
    private String age;

    /** The user's gender. */
    private String gender;

    /** The user's weight (stored as a string for simplicity). */
    private String weight;

    /**
     * Constructs a new {@code User} object with the given details.
     * @param username the user's username
     * @param email    the user's email address
     * @param age      the user's age
     * @param gender   the user's gender
     * @param weight   the user's weight
     */
    public User(String username, String email, String age, String gender, String weight) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
    }

    /** @return the username */
    public String getUsername() {
        return username;
    }

    /** @param username sets the username */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return the email address */
    public String getEmail() {
        return email;
    }

    /** @param email sets the email address */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return the age */
    public String getAge() {
        return age;
    }

    /** @param age sets the age */
    public void setAge(String age) {
        this.age = age;
    }

    /** @return the gender */
    public String getGender() {
        return gender;
    }

    /** @param gender sets the gender */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /** @return the weight */
    public String getWeight() {
        return weight;
    }

    /** @param weight sets the weight */
    public void setWeight(String weight) {
        this.weight = weight;
    }

    /**
     * Returns a string representation of the user,
     * including username and email.
     * @return a string describing the user
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}