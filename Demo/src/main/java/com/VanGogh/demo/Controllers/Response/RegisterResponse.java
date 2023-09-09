package com.VanGogh.demo.Controllers.Response;

/**
 * 注册响应类，用于封装注册信息。
 */
public class RegisterResponse {
    private String password;
    private String userName;

    /**
     * 获取用户密码。
     *
     * @return 用户密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置用户密码。
     *
     * @param password 用户密码
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
