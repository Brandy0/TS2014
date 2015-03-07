package com.erhuoapp.erhuo.model;

/**
 * 商品类别类
 *
 * @author hujiawei
 * @datetime 15/1/4 21:35
 */
public class EntityGoodsClassify extends EntityBase {

    private static final long serialVersionUID = 1L;

    private String id = "";
    private String name = "";
    private String icon = "";//商品分类图标(主页使用)
    private String smallIcon = "";//商品分类图标(侧边栏使用)
    private String postIcon = "";//商品分类图标(发布商品使用)
    private String postIconActive = "";//商品分类图标(发布商品选中后使用)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String getPostIcon() {
        return postIcon;
    }

    public void setPostIcon(String postIcon) {
        this.postIcon = postIcon;
    }

    public String getPostIconActive() {
        return postIconActive;
    }

    public void setPostIconActive(String postIconActive) {
        this.postIconActive = postIconActive;
    }
}
