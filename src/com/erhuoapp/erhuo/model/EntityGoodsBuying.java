package com.erhuoapp.erhuo.model;

/**
 * 求购的商品的详情
 *
 * @author hujiawei
 * @datetime 15/1/10
 */
public class EntityGoodsBuying extends EntityBase {

    private static final long serialVersionUID = 1L;

    private String id = "";//商品编号
    private String title = "";//商品标题
    private String price = "";//商品价格
    private String time = "";//发布时间
    private String collectNum = "";//收藏数目
    private String commentNum = "";//评论数目
    private String userId = "";//
    private String userHeader = "";
    private String userNickName = "";
    private String distance = "";//
    private String content = "";//商品信息
    private boolean isCollect = false;
    private boolean isComment = false;
    private boolean isAuth = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(String collectNum) {
        this.collectNum = collectNum;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserHeader() {
        return userHeader;
    }

    public void setUserHeader(String userHeader) {
        this.userHeader = userHeader;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        this.isCollect = collect;
    }

    public boolean isComment() {
        return isComment;
    }

    public void setComment(boolean comment) {
        this.isComment = comment;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        this.isAuth = auth;
    }

}
