package com.bulhakov.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "USERS", schema = "ATHLON")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User { // Changed from 'record' to 'class'

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "external_id", nullable = false, unique = true)
    private Long externalId;

    @Column(name = "login", unique = true, length = 64)
    private String login;

    @Column(name = "username", nullable = false, length = 32)
    private String username;

    @Column(name = "birthdate")
    private Date birthday;

    @Column(name = "banned", nullable = false)
    private Boolean banned;
}