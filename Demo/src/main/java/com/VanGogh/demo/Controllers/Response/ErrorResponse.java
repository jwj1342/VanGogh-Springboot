package com.VanGogh.demo.Controllers.Response;

/**
 * 错误响应类，用于封装错误信息。
 */
public class ErrorResponse {
    private String error;

    /**
     * 构造一个错误响应对象。
     *
     * @param error 错误信息
     */
    public ErrorResponse(String error) {
        this.error = error;
    }

    /**
     * 获取错误信息。
     *
     * @return 错误信息
     */
    public String getError() {
        return error;
    }
}
