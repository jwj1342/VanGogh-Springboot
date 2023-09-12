package com.VanGogh.demo.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data // 自动生成getter、setter、equals、hashCode等方法
public class ImageEntity {
    /**
     * 图片ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 图片所属用户
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * 图片点赞数
     */
    private int likes;

    /**
     * 图片URL
     */
    private String imageUrl;
    /**
     * 处理过后图片URL
     */
    private String imageUrlAfter;

    /**
     * 图片标题
     */
    private String title;

    /**
     * 图片创建时间
     */
    private LocalDateTime createTime;
}
