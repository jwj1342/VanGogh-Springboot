package com.VanGogh.demo.Controllers.Request;

/**
 * 登录请求类，用于接收登录请求的参数。
 */
public class LoginRequest {
    private String userName;
    private String password;

    /**
     * 设置用户名。
     *
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 设置密码。
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取用户名。
     *
     * @return 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 获取密码。
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }
}
