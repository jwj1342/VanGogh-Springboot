package com.VanGogh.demo.Entities;

import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片实体类。
 */
@Entity
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "likes")
    private int likes;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "title")
    private String title;

    @Column(name = "createTime")
    private String createTime;


    /**
     * 无参构造函数。
     */
    public ImageEntity() {
    }

    /**
     * 获取图片标题。
     *
     * @return 图片标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置图片标题。
     *
     * @param title 图片标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取图片创建时间。
     *
     * @return 图片创建时间
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 设置图片创建时间。
     *
     * @param createTime 图片创建时间
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取图片所属用户。
     *
     * @return 图片所属用户
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * 设置图片所属用户。
     *
     * @param user 图片所属用户
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * 获取图片点赞数。
     *
     * @return 图片点赞数
     */
    public int getLikes() {
        return likes;
    }

    /**
     * 设置图片点赞数。
     *
     * @param likes 图片点赞数
     */
    public void setLikes(int likes) {
        this.likes = likes;
    }

    /**
     * 获取图片URL。
     *
     * @return 图片URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 设置图片URL。
     *
     * @param imageUrl 图片URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    /**
     * 获取图片ID。
     *
     * @return 图片ID
     */
    public Long getId() {
        return id;
    }
}
