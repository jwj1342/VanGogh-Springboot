package com.VanGogh.demo.Controllers.Response;

/**
 * 推荐响应类，用于封装推荐信息。
 */
public class RecommendResponse {
    private String title;
    private String url;
    private int likes;

    /**
     * 获取推荐标题。
     *
     * @return 推荐标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置推荐标题。
     *
     * @param title 推荐标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取推荐链接URL。
     *
     * @return 推荐链接URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置推荐链接URL。
     *
     * @param url 推荐链接URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取推荐点赞数。
     *
     * @return 推荐点赞数
     */
    public int getLikes() {
        return likes;
    }

    /**
     * 设置推荐点赞数。
     *
     * @param likes 推荐点赞数
     */
    public void setLikes(int likes) {
        this.likes = likes;
    }
}
