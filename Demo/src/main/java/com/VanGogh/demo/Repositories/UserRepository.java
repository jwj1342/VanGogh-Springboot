package com.VanGogh.demo.Repositories;

import com.VanGogh.demo.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户仓库接口，用于操作用户实体对象。
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * 根据用户名查找用户实体对象。
     *
     * @param userName 用户名
     * @return 用户实体对象
     */
    UserEntity findUserEntityByUsername(String userName);

    /**
     * 判断是否存在指定用户名的用户。
     *
     * @param userName 用户名
     * @return 若存在指定用户名的用户，则返回true；否则返回false
     */
    boolean existsByUsername(String userName);
}
