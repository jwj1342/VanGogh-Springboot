package com.VanGogh.demo.Controllers.Request;

/**
 * 注册请求类，用于接收注册请求的参数。
 */
public class RegisterRequest {
    private String password;
    private String userName;

    /**
     * 获取密码。
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
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
     * 设置用户名。
     *
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
