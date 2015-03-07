package com.erhuoapp.erhuo.model;

/**
 * 图片实体类，但是只是适用于发布在卖商品中
 *
 * @author hujiawei
 * @datetime 15/1/6 21:57
 */
public class EntitySellingImage extends EntityBase {

    private static final long serialVersionUID = 1L;

    private String imageUri = "";//图片URL地址
    private String sdpath = "";//图片在sd卡中的路径
    private boolean isStub = false;//是否是空操作图片

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isStub() {
        return isStub;
    }

    public void setStub(boolean isStub) {
        this.isStub = isStub;
    }

    public String getSdpath() {
        return sdpath;
    }

    public void setSdpath(String sdpath) {
        this.sdpath = sdpath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntitySellingImage that = (EntitySellingImage) o;
        if (!imageUri.equals(that.imageUri)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return imageUri.hashCode();
    }
}
