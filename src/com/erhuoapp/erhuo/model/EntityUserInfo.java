package com.erhuoapp.erhuo.model;

/**
 * 用户信息，本地数据库有table
 *
 * @author hujiawei
 * @datetime 15/1/6 16:58
 */
public class EntityUserInfo extends EntityBase {

    private static final long serialVersionUID = 1L;

    private String id = "";
    private String name = "";
    private String password = "";
    private String nickName = "";
    private String header = "";
    private String sex = "";
    private String phone = "";
    private String school = "";
    private String grade = "";
    private String registerTime = "";
    private String third_part = "";
    private String openId = "";
    private String cookie = "";
    private String sellingnum = "";
    private String buyingnum = "";
    private String soldnum = "";
    private String address = "";
    private Boolean isAuth = false;
    private int auth = 0;//刚开始没有设计好，auth现在变成3中状态，当auth=2时表示正在审核中

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getThird_part() {
        return third_part;
    }

    public void setThird_part(String third_part) {
        this.third_part = third_part;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public Boolean getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(Boolean isAuth) {
        this.isAuth = isAuth;
    }

    public String getSellingnum() {
        return sellingnum;
    }

    public void setSellingnum(String sellingnum) {
        this.sellingnum = sellingnum;
    }

    public String getBuyingnum() {
        return buyingnum;
    }

    public void setBuyingnum(String buyingnum) {
        this.buyingnum = buyingnum;
    }

    public String getSoldnum() {
        return soldnum;
    }

    public void setSoldnum(String soldnum) {
        this.soldnum = soldnum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
