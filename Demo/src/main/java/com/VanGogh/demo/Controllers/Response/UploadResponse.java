package com.VanGogh.demo.Controllers.Response;

/**
 * 上传响应类，用于封装上传图片的信息。
 */
public class UploadResponse {
    private String imageUrl;

    /**
     * 获取上传图片的URL地址。
     *
     * @return 图片的URL地址
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 设置上传图片的URL地址。
     *
     * @param imageUrl 图片的URL地址
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
