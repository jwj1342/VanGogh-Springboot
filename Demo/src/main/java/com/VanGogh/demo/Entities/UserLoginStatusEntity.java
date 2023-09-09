package com.VanGogh.demo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
public class UserLoginStatusEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime loginTime;

    @OneToOne
    @PrimaryKeyJoinColumn
    private UserEntity userEntity;

    private String token;
}
