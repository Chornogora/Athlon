package com.bulhakov.model;

import java.util.Date;

//@Document(collection = "users")
public record User (String id, Long externalId, String login, String username, Date birthday, Boolean banned) {
}
