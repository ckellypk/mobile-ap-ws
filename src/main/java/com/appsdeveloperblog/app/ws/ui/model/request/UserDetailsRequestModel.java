package com.appsdeveloperblog.app.ws.ui.model.request;

public class UserDetailsRequestModel {
    // class fields that match json client.
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    //need to generate getters and setters for each of the fields

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
