package com.erhuoapp.erhuo.model;

/**
 *
 * HTTP返回信息
 *
 * @author hujiawei
 * @datetime 15/1/4 20:42
 */

import java.io.Serializable;

public class EntityHttpResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int STATUS_CODE_OK = 200;

    private int statuscode = 0;
    private String message = "";
    private String token = "";//某些情况(手机验证码)下会返回token
    private String description = "";//某些情况(手机验证码)下会返回description

    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}