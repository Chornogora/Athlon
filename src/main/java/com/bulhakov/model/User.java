package com.bulhakov.model;

import java.util.Date;

//@Document(collection = "users")
public class User {

    //@Id
    private String id;

    private String login;

    private String name;

    private Date birthday;

    public User(){

    }

    public User(String id, String login, String name) {
        this.id = id;
        this.login = login;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
