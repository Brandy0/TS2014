package com.erhuoapp.erhuo.model;

import java.util.ArrayList;

/**
 * 出售的商品的详情
 *
 * @author hujiawei
 * @datetime 15/1/6 21:56
 */
public class EntityGoodsSelling extends EntityBase {

    private static final long serialVersionUID = 1L;

    private String id = "";//商品编号
    private String title = "";//商品标题
    private String price = "";//商品价格
    private String time = "";//发布时间
    private String collectNum = "";//收藏数目
    private String commentNum = "";//评论数目
    private String userId = "";//
    private String userHeader = "";
    private String userName = "";
    private String userNickName = "";
    private String distance = "";//表示商品与当前所在位置的距离
    private boolean isCollect = false;
    private boolean isComment = false;
    private boolean isAuth = false;
    private boolean isNew = false;
    private boolean isBargin = false;
    private String image = "";
    private String content = "";//商品信息
    private String cid = "";//类别id
    private ArrayList<EntityImage> images;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isBargin() {
        return isBargin;
    }

    public void setBargin(boolean isBargin) {
        this.isBargin = isBargin;
    }

    public ArrayList<EntityImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<EntityImage> images) {
        this.images = images;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
