package com.VanGogh.demo.Repositories;

import com.VanGogh.demo.Entities.UserLoginStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginStatusRepository extends JpaRepository<UserLoginStatusEntity, Long> {

}

