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
     * 图片标题
     */
    private String title;

    /**
     * 图片创建时间
     */
    private LocalDateTime createTime;

    // 其他自定义方法或属性

    /**
     * 重写equals方法，判断两个图片实体是否相同。
     *
     * @param o 要比较的对象
     * @return 如果两个图片实体属性相同，则返回true；否则返回false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageEntity that = (ImageEntity) o;
        return likes == that.likes
                && Objects.equals(id, that.id)
                && Objects.equals(user, that.user)
                && Objects.equals(imageUrl, that.imageUrl)
                && Objects.equals(title, that.title);
                //&& Objects.equals(createTime, that.createTime);
    }
}
