package com.family_tree.familytree;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Table(name = "users")  // Specifies the table name in the database
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Uses AUTO_INCREMENT in MySQL
    @Column(name = "user_id")  // Maps this field to the "user_id" column in the database
    private Integer id;

    @Column(name = "username", nullable = false, length = 200)  // Maps to "username", not null, max length 200
    private String username;

    @Column(name = "email_address", nullable = false, length = 200)  // Maps to "email_address", not null, max length 200
    private String email;


    @Column(name = "access_token", nullable= false, length = 512)  // Adjust length as needed
    private String accessToken;

    //Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
