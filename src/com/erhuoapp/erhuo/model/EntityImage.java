package com.erhuoapp.erhuo.model;

/**
 * 图片实体类
 *
 * @author hujiawei
 * @datetime 15/1/6 21:57
 */
public class EntityImage extends EntityBase {

    private static final long serialVersionUID = 1L;

    private String image = "";//图片URL地址
    private int imageWidth = 0;
    private int imageHeight = 0;

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

}
