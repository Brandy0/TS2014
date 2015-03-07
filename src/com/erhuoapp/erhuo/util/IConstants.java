package com.erhuoapp.erhuo.util;

/**
 *
 * 常量
 *
 * @author hujiawei
 * @datetime 15/1/10 12:16
 */
public interface IConstants {

    // 版本号
    public static final String VERSION = "2.0.0";

    // 关于我们网址
    public static final String ABOUT_US_URL = "http://www.erhuoapp.com/about.html";

    // request code
    public static final int REQUEST_GUIDE = 100;
    public static final int REQUEST_LOGIN = 101;
    public static final int REQUEST_SETTINGS = 102;
    public static final int REQUEST_BUYING_PUBLISH = 103;
    public static final int REQUEST_BUYING_UPDATE = 104;
    public static final int REQUEST_REGISTER = 105;
    public static final int REQUEST_PHONE = 106;

    public static final int REQUEST_USER_AUTH = 111;//用户认证
    public static final int REQUEST_TAKE_PICTURE = 112;//请求拍照
    public static final int REQUEST_PICK_PICTURE = 113;//选择图片

    public static final int REQUEST_CAMERA_CARD = 107;// 进入摄像头界面的三种类型 <- 之前自定义使用的
    public static final int REQUEST_CAMERA_HEADER = 108;
    public static final int REQUEST_CAMERA_GOODS = 109;
    public static final int REQUEST_SELECT_PHOTO = 110;
    public static final int REQUEST_CHECK_BUYING = 111;

    // result code


    // 进入手机号验证界面的类型
    public static final String PHONE_USER_REGISTER = "user_reg";
    public static final String PHONE_ADD = "add_phone";
    public static final String PHONE_PASSWORD_RESET = "pwd_reset";
    public static final String PHONE_CHANGE = "change_phone";

    // preference key
    public static final String PREF_KEY_APP = "erhuo";//标识为是二货应用
    public static final String PREF_KEY_FIRST = "first";//是否是第一次进入应用
    public static final String PREF_KEY_UID = "id";//cookie中的用户id
    public static final String PREF_KEY_TOKEN = "token";//cookie中的token

    // 我不想用文件来保存用户数据，所以直接用SharedPreference来保存
    // NOTE: 后来我发现其实不用存这么多数据在这里，部分频繁使用的数据倒是可以存在这里
    public static final String PREF_KEY_NAME = "uname";//用户的名称
    public static final String PREF_KEY_SCHOOL = "school";//用户的学校
    public static final String PREF_KEY_GRADE = "grade";//用户的年级
    public static final String PREF_KEY_HEADER = "header";//用户的头像
    public static final String PREF_KEY_NICKNAME = "nickname";//用户的昵称
    public static final String PREF_KEY_AUTH = "auth";//用户的认证情况
    public static final String PREF_KEY_PHONE = "phone";//用户的手机号码
    public static final String PREF_KEY_SEX = "sex";//用户的性别
    public static final String PREF_KEY_TIME = "time";//用户的注册时间
    public static final String PREF_KEY_ADDRESS = "address";//用户的联系地址

    public static final String PREF_KEY_LOGIN = "login";//表示是否有用户已经登录了
    public static final String PREF_KEY_HISTORY = "history";//用户的搜索历史


}
