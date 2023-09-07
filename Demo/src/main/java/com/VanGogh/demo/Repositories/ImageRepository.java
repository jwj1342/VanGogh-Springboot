package com.VanGogh.demo.Repositories;

import com.VanGogh.demo.Entities.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ImageRepository extends JpaRepository <ImageEntity,Long>{
}
