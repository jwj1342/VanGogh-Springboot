package com.VanGogh.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类。
 */
@Entity
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime registrationTime;

    @OneToOne
    @PrimaryKeyJoinColumn
    private UserLoginStatusEntity loginStatus;

}
