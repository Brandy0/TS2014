package com.erhuoapp.erhuo.model;

/**
 * 版本更新的相关数据类
 *
 * @author hujiawei
 * @datetime 15/1/30 10:03
 */
public class EntityUpdate extends EntityBase {

    private String version;
    private String download;//url

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
