package com.erhuoapp.erhuo.model;

/**
 * 商品焦点 （推荐商品）
 *
 * @author hujiawei
 * @datetime 15/1/5 23:19
 */
public class EntityGoodsFocus extends EntityBase {

    private static final long serialVersionUID = 1L;

    private String id = "";//id
    private String title = "";//主题
    private String price = "";//价格
    private String image = "";//图片地址
    private int imageWidth = 0;
    private int imageHeight = 0;
    private int target = 0;
    private String url = "";
    private String targetCid = "";
    private String targetName = "";

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTargetCid() {
        return targetCid;
    }

    public void setTargetCid(String targetCid) {
        this.targetCid = targetCid;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
