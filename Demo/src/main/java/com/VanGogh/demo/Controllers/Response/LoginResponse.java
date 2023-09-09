package com.VanGogh.demo.Controllers.Response;

/**
 * 登录响应类，用于封装登录信息。
 */
public class LoginResponse {
    private Long id;
    private String username;
    private String email;

    /**
     * 无参构造方法。
     */
    public LoginResponse() {
    }

    /**
     * 构造一个登录响应对象。
     *
     * @param id       用户ID
     * @param username 用户名
     * @param email    邮箱
     */
    public LoginResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    /**
     * 获取用户ID。
     *
     * @return 用户ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户ID。
     *
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名。
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名。
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取邮箱。
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱。
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
