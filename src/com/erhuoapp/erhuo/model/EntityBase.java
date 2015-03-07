package com.erhuoapp.erhuo.model;

import java.io.Serializable;

import org.json.JSONObject;

/**
 * Entity的基类
 *
 * @author hujiawei
 * @datetime 15/1/4 20:42
 */
public class EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    private int s = 0;//状态码
    private String e = "";//错误信息
    
    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }
}