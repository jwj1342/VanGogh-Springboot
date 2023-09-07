package com.VanGogh.demo.Repositories;

import com.VanGogh.demo.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository <UserEntity,Long>{
    public UserEntity findUserEntityByUsername(String userName);

    boolean existsByUsername(String userName);
}
