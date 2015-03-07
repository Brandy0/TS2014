package com.erhuoapp.erhuo.util;

import android.os.AsyncTask;
import android.util.Log;

import com.erhuoapp.erhuo.model.EntityComment;
import com.erhuoapp.erhuo.model.EntityGoodsBuying;
import com.erhuoapp.erhuo.model.EntityGoodsClassify;
import com.erhuoapp.erhuo.model.EntityGoodsFocus;
import com.erhuoapp.erhuo.model.EntityGoodsSelling;
import com.erhuoapp.erhuo.model.EntityHttpResponse;
import com.erhuoapp.erhuo.model.EntityImage;
import com.erhuoapp.erhuo.model.EntityUpdate;
import com.erhuoapp.erhuo.model.EntityUserInfo;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import internal.org.apache.http.entity.mime.HttpMultipartMode;
import internal.org.apache.http.entity.mime.MultipartEntity;
import internal.org.apache.http.entity.mime.content.FileBody;
import internal.org.apache.http.entity.mime.content.StringBody;

/**
 * 与服务器端进行数据操作的接口类
 *
 * @author hujiawei
 * @datetime 15/1/4 20:19
 */
public class CloudUtil {

    private static final String TAG = "CloudUtil";

    // cookie key
    public final static String COOKIE_KEY_ID = "id";
    public final static String COOKIE_KEY_TOKEN = "token";

    //返回数据的json字符串key
    public static final String RESPONSE_JSON_ERROR = "error";
    public static final String RESPONSE_JSON_DATA = "data";
    public static final String RESPONSE_JSON_DESCRIPTION = "description";
    public static final String RESPONSE_JSON_TOKEN = "token";

    // 服务器图片请求地址
    //public final static String HTTP_IMAGE_URL = "http://123.57.132.230/images/";
    // 服务器请求地址 //http://123.57.132.230/InterfaceDev.php
    public final static String HTTP_API_URL = "http://www.erhuoapp.com/Interface-2.php";
    // 用户注册
    public final static String HTTP_API_REGISTER = "user_reg";
    // 登陆
    public final static String HTTP_API_LOGIN = "user_login";
    // 第三方登陆
    public final static String HTTP_API_LOGIN_THIRD = "user_login_third";
    // 用户信息
    public final static String HTTP_API_USER_INFO = "user_info";
    // 密码修改
    public final static String HTTP_API_EDIT_PASSWORD = "pwd_update";
    // 密码重置
    public final static String HTTP_API_RESET_PASSWORD = "pwd_reset";
    // 用户信息修改
    public final static String HTTP_API_EDIT_USER_INFO = "user_update";
    // 首页焦点图（推荐商品）
    public final static String HTTP_API_GOODS_FOCUS = "recommend_goods_list";
    // 商品分类
    public final static String HTTP_API_GOODS_CLASSIFY = "class_list";
    // 正在卖商品列表
    public final static String HTTP_API_GOODS_SELLING = "sell_goods_list";
    // 商品详情
    public final static String HTTP_API_GOODS_INFO = "sell_goods_details";
    // 评论列表
    public final static String HTTP_API_GOODS_COMMENT = "comment_list";
    // 在卖商品搜索
    public final static String HTTP_API_GOODS_SEARCH = "search_sell_goods";
    // 求购商品列表
    public final static String HTTP_API_GOODS_BUYING = "shopping_goods_list";
    // 求购商品详情
    public final static String HTTP_API_GOODS_BUYING_INFO = "shopping_goods_details";
    // 我的在卖列表
    public final static String HTTP_API_GOODS_SELLING_MY = "my_sell_goods";
    // 我的求购列表
    public final static String HTTP_API_GOODS_BUYING_MY = "my_shopping_goods";
    // 我的已售列表
    public final static String HTTP_API_GOODS_SOLD_MY = "my_sold_goods";
    // 他人的在卖列表
    public final static String HTTP_API_GOODS_SELLING_OTHER = "user_sell_goods";
    // 他人的求购列表
    public final static String HTTP_API_GOODS_BUYING_OTHER = "user_shopping_goods";
    // 他人的已售列表
    public final static String HTTP_API_GOODS_SOLD_OTHER = "user_sold_goods";
    // 添加收藏
    public final static String HTTP_API_COLLECT_ADD = "insert_collect";
    // 取消收藏
    public final static String HTTP_API_COLLECT_REMOVE = "cancel_collect";
    // 我的收藏列表
    public final static String HTTP_API_COLLECT_MY = "my_collect";
    // 添加评论
    public final static String HTTP_API_COMMENT_ADD = "insert_comment";
    // 删除评论
    public final static String HTTP_API_COMMENT_REMOVE = "del_comment";
    // 我的评论列表
    public final static String HTTP_API_COMMENT_MY = "my_comment";
    // 发布求购
    public final static String HTTP_API_PUBLISH_BUYING = "insert_shopping_goods";
    // 发布商品
    public final static String HTTP_API_PUBLISH_SELLING = "insert_sell_goods";
    // 修改发布商品
    public final static String HTTP_API_PUBLISH_SELLING_UPDATE = "update_sell_goods";
    // 删除发布商品
    public final static String HTTP_API_PUBLISH_SELLING_DELETE = "delete_sell_goods";
    // 已售发布商品
    public final static String HTTP_API_PUBLISH_SELLING_SOLD = "sold_sell_goods";
    // 修改求购商品
    public final static String HTTP_API_PUBLISH_BUYING_UPDATE = "update_shopping_goods";
    // 删除求购商品
    public final static String HTTP_API_PUBLISH_BUYING_DELETE = "delete_shopping_goods";

    // 意见反馈
    public final static String HTTP_API_POST_ADVICE = "post_advice";
    // 热门搜索
    public final static String HTTP_API_HOT_SEARCH = "hot_keywords";
    // 请求发送手机验证码
    public final static String HTTP_API_REQUEST_CODE = "user_request_code";
    // 请求验证手机验证码
    public final static String HTTP_API_VALIDATE_CODE = "user_confirm_code";

    // 上传学生证
    public final static String HTTP_API_UPLOAD_CARD = "user_idcard";
    // 更改手机号
    public final static String HTTP_API_CHANGE_PHONE = "user_phone_change";
    // 检查更新
    public final static String HTTP_API_CHECK_UPDATE = "check_for_update";

    /**
     * 删除当前用户 TODO 测试用的接口
     */
    public void DeleteUser() {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi("delete_user").getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            Log.e(TAG, "用户已删除");
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {

        }
    }

    /**
     * 异步请求完成时，接口回调
     */
    public interface OnPostRequest<obj> {

        void onPost();

        void onPostOk(obj temp);

        void onPostFailure(String err);
    }

    /**
     * HTTP请求的快速封装类
     */
    public class Packaging {

        private String key_client = "client";
        private String key_api = "api";
        private String key_pageIndex = "pageIndex";
        private String key_displayNumber = "displayNumber";

        // 初始化封装
        private MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        // 得到封装 [这里trick一下，每次得到MultipartEntity的时候都会默认添加client为1的参数，表明这是来自客户端]
        public MultipartEntity getMultipartEntity() throws UnsupportedEncodingException {
            multipartEntity.addPart(key_client, new StringBody("1", Charset.forName("UTF-8")));//
            return multipartEntity;
        }

        // 设置封装
        public void setMultipartEntity(MultipartEntity multipartEntity) {
            this.multipartEntity = multipartEntity;
        }

        // 添加普通字符串
        public Packaging addString(String key, String str) throws UnsupportedEncodingException {
            multipartEntity.addPart(key, new StringBody(str, Charset.forName("UTF-8")));
            return this;
        }

        // 添加api参数方法
        public Packaging addApi(String api) throws UnsupportedEncodingException {
            Log.e(TAG, "API = " + api);//如果发生错误的话可以方便的看到当前调用的是哪个接口
            multipartEntity.addPart(key_api, new StringBody(api, Charset.forName("UTF-8")));
            return this;
        }

        // 添加pageIndex参数方法
        public Packaging addPageIndex(String pageIndex) throws UnsupportedEncodingException {
            multipartEntity.addPart(key_pageIndex, new StringBody(pageIndex, Charset.forName("UTF-8")));
            return this;
        }

        // 添加displayNumber参数方法
        public Packaging addDisplayNumber(String displayNumber) throws UnsupportedEncodingException {
            multipartEntity.addPart(key_displayNumber, new StringBody(displayNumber, Charset.forName("UTF-8")));
            return this;
        }
    }

    /**
     * post请求方法
     *
     * @param url             请求接口地址
     * @param multipartEntity 请求体内容
     * @return 返回一个封装，包含状态码以及内容
     */
    public EntityHttpResponse doPost(String url, MultipartEntity multipartEntity) throws ParseException, IOException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpParams params = httpclient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 50000);//超时设置
        HttpConnectionParams.setSoTimeout(params, 50000);
        //每次发送post请求时先要看是否有cookie数据，如果有的话一定要带上
        HttpPost httpPost = new HttpPost(url);
        if (AppUtil.getInstance().checkUserLogin()) {//一定要将所有的cookie和在一起
            httpPost.setHeader("cookie", "id=" + AppUtil.getInstance().getUserId() + ";token=" + AppUtil.getInstance().getToken());
        }
        //httpclient.getCookieStore().addCookie(new BasicClientCookie(COOKIE_KEY_ID, AppUtil.getInstance().getUserId()));
        //httpclient.getCookieStore().addCookie(new BasicClientCookie(COOKIE_KEY_TOKEN, AppUtil.getInstance().getToken()));
        httpPost.setEntity(multipartEntity);
        //printHttpPost(httpPost);//
        HttpResponse httpResponse = httpclient.execute(httpPost);
        //printHttpResponse(httpResponse);
        EntityHttpResponse entityHttpResponse = new EntityHttpResponse();
        entityHttpResponse.setMessage(EntityUtils.toString(httpResponse.getEntity()));
        entityHttpResponse.setStatuscode(httpResponse.getStatusLine().getStatusCode());
        //Log.e(TAG, entityHttpResponse.getMessage());
        return entityHttpResponse;
    }

//    /**
//     * post请求方法，但是带上了文件
//     *
//     * @param url             请求接口地址
//     * @param multipartEntity 请求体内容
//     * @return 返回一个封装，包含状态码以及内容
//     */
//    public EntityHttpResponse doPostFile(String url, MultipartEntity multipartEntity) throws ParseException, IOException {
//        DefaultHttpClient httpclient = new DefaultHttpClient();
//        HttpParams params = httpclient.getParams();
//        HttpConnectionParams.setConnectionTimeout(params, 50000);//超时设置
//        HttpConnectionParams.setSoTimeout(params, 50000);
//        //httpclient.DefaultRequestHeaders.Add("Accept", "application/xml");
//        //每次发送post请求时先要看是否有cookie数据，如果有的话一定要带上
//        HttpPost httpPost = new HttpPost(url);
//        if (AppUtil.getInstance().checkUserLogin()) {//一定要将所有的cookie和在一起
//            httpPost.setHeader("cookie", "id=" + AppUtil.getInstance().getUserId() + ";token=" + AppUtil.getInstance().getToken());
//        }
//        httpPost.addHeader("Content-type", "image/png");//TODO 暂时没有测试
//        //httpclient.getCookieStore().addCookie(new BasicClientCookie(COOKIE_KEY_ID, AppUtil.getInstance().getUserId()));
//        //httpclient.getCookieStore().addCookie(new BasicClientCookie(COOKIE_KEY_TOKEN, AppUtil.getInstance().getToken()));
//        httpPost.setEntity(multipartEntity);
//        //printHttpPost(httpPost);//
//        HttpResponse httpResponse = httpclient.execute(httpPost);
//        //printHttpResponse(httpResponse);
//        EntityHttpResponse entityHttpResponse = new EntityHttpResponse();
//        entityHttpResponse.setMessage(EntityUtils.toString(httpResponse.getEntity()));
//        entityHttpResponse.setStatuscode(httpResponse.getStatusLine().getStatusCode());
//        //Log.e(TAG, entityHttpResponse.getMessage());
//        return entityHttpResponse;
//    }

    /**
     * 在注册成功的时候会得到cookie数据，注册失败的时候没有cookie数据
     * =========httpclient cookies=========
     * cookie[id] 728
     * cookie[token] H55MCZ36ZBWKFZVCYWPN
     */
    private void printCookies(DefaultHttpClient httpclient) {
        Log.e(TAG, "=========httpclient cookies=========");
        for (Cookie cookie : httpclient.getCookieStore().getCookies()) {
            Log.e(TAG, cookie.getName() + " " + cookie.getValue());
        }
    }

    private void printHttpResponse(HttpResponse httpResponse) {
        Log.e(TAG, "=========httpresponse headers=========");
        Header[] headers = httpResponse.getAllHeaders();
        for (Header header : headers) {
            Log.e(TAG, header.getName() + " " + header.getValue());
        }
    }

    private void printHttpPost(HttpPost httpPost) {
        Log.e(TAG, "=========httppost headers=========");
        Header[] headers = httpPost.getAllHeaders();
        for (Header header : headers) {
            Log.e(TAG, header.getName() + " " + header.getValue());
        }
    }

    /**
     * 用户注册 （特定的doPost）
     */
    public void Register(final HashMap<String, String> params, final OnPostRequest<EntityUserInfo> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_REGISTER)
                    .addString("nickname", params.get("userName")).addString("pwd", params.get("userPassword"))
                    .addString("phone", params.get("phone")).addString("token", params.get("token")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    EntityUserInfo entityUserInfo = new EntityUserInfo();
                    try {
                        //原来是通过doPost方法得到返回结果的，但是这种方式没法得到cookie数据，所以这个方法作为特例，直接处理而不再调用doPost
                        //EntityHttpResponse entityHttpResponse = doPostStoreCookies(HTTP_API_URL, params[0]);
                        DefaultHttpClient httpclient = new DefaultHttpClient();//通过httpclient对象可以得到cookie数据
                        HttpPost httpPost = new HttpPost(HTTP_API_URL);
                        httpPost.setEntity(multipartEntity);
                        HttpResponse httpResponse = httpclient.execute(httpPost);
                        EntityHttpResponse entityHttpResponse = new EntityHttpResponse();
                        entityHttpResponse.setMessage(EntityUtils.toString(httpResponse.getEntity()));
                        entityHttpResponse.setStatuscode(httpResponse.getStatusLine().getStatusCode());
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            entityUserInfo.setS(jsonObject.getInt(RESPONSE_JSON_ERROR));//JSON状态码
                            if (0 == entityUserInfo.getS()) {
                                //注册成功的话会返回cookie数据
                                printCookies(httpclient);
                                List<Cookie> cookies = httpclient.getCookieStore().getCookies();
                                AppUtil.getInstance().saveUserData(cookies.get(0).getValue(), cookies.get(1).getValue());
                                result = entityUserInfo;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityUserInfo) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }
    
    
    /**
     * 第三方登陆 （特定的doPost）
     */
    public void ThirdRegister(final HashMap<String, String> params, final OnPostRequest<EntityUserInfo> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_LOGIN_THIRD)
                    .addString("openid", params.get("openid")).addString("third_party", params.get("third_party"))
                    .addString("nickname", params.get("nickname")).addString("header", params.get("header"))
                    .addString("sex", params.get("sex")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    EntityUserInfo entityUserInfo = new EntityUserInfo();
                    try {
                        //原来是通过doPost方法得到返回结果的，但是这种方式没法得到cookie数据，所以这个方法作为特例，直接处理而不再调用doPost
                        //EntityHttpResponse entityHttpResponse = doPostStoreCookies(HTTP_API_URL, params[0]);
                        DefaultHttpClient httpclient = new DefaultHttpClient();//通过httpclient对象可以得到cookie数据
                        HttpPost httpPost = new HttpPost(HTTP_API_URL);
                        httpPost.setEntity(multipartEntity);
                        HttpResponse httpResponse = httpclient.execute(httpPost);
                        EntityHttpResponse entityHttpResponse = new EntityHttpResponse();
                        entityHttpResponse.setMessage(EntityUtils.toString(httpResponse.getEntity()));
                        entityHttpResponse.setStatuscode(httpResponse.getStatusLine().getStatusCode());
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            entityUserInfo.setS(jsonObject.getInt(RESPONSE_JSON_ERROR));//JSON状态码
                            if (0 == entityUserInfo.getS()) {
                                //注册成功的话会返回cookie数据
                                printCookies(httpclient);
                                List<Cookie> cookies = httpclient.getCookieStore().getCookies();
                                AppUtil.getInstance().saveUserData(cookies.get(0).getValue(), cookies.get(1).getValue());
                                result = entityUserInfo;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityUserInfo) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }
    

    /**
     * 用户登录 （特定的doPost）
     */
    public void Login(final HashMap<String, String> params, final OnPostRequest<EntityUserInfo> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_LOGIN)
                    .addString("phone", params.get("phone")).addString("pwd", params.get("pwd"))
                    .getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    EntityUserInfo entityUserInfo = new EntityUserInfo();
                    try {
                        //原来是通过doPost方法得到返回结果的，但是这种方式没法得到cookie数据，所以这个方法作为特例，直接处理而不再调用doPost
                        //EntityHttpResponse entityHttpResponse = doPostStoreCookies(HTTP_API_URL, params[0]);
                        DefaultHttpClient httpclient = new DefaultHttpClient();//通过httpclient对象可以得到cookie数据
                        HttpPost httpPost = new HttpPost(HTTP_API_URL);
                        httpPost.setEntity(multipartEntity);
                        HttpResponse httpResponse = httpclient.execute(httpPost);
                        EntityHttpResponse entityHttpResponse = new EntityHttpResponse();
                        entityHttpResponse.setMessage(EntityUtils.toString(httpResponse.getEntity()));
                        entityHttpResponse.setStatuscode(httpResponse.getStatusLine().getStatusCode());
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            entityUserInfo.setS(jsonObject.getInt(RESPONSE_JSON_ERROR));//JSON状态码
                            if (0 == entityUserInfo.getS()) {
                                //登录成功的话会返回cookie数据
                                printCookies(httpclient);
                                List<Cookie> cookies = httpclient.getCookieStore().getCookies();
                                AppUtil.cookies = cookies;//读cookie
                                AppUtil.cookieStr = cookies.toString();
                                Log.e(TAG, "Cookie" + AppUtil.cookieStr);
                                String uid = cookies.get(0).getValue();
                                String token = cookies.get(1).getValue();
                                AppUtil.getInstance().saveUserData(uid, token);
                                result = entityUserInfo;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityUserInfo) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 首页焦点图
     */
    public void GoodsFocus(final OnPostRequest<ArrayList<EntityGoodsFocus>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging()
                    .addApi(HTTP_API_GOODS_FOCUS).getMultipartEntity();
            //.addPageIndex(pageIndex).addDisplayNumber(displayNumber)
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsFocus> list = new ArrayList<EntityGoodsFocus>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsFocus item = new EntityGoodsFocus();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setTitle(obj.getString("title"));
                                    item.setPrice(obj.getString("price"));
                                    item.setImage(obj.getString("recommend_img"));
                                    item.setUrl(obj.getString("url"));
                                    item.setTarget(obj.getInt("target"));
                                    item.setTargetCid(obj.getString("target_cid"));
                                    item.setTargetName(obj.getString("target_cname"));
                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsFocus>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 首页分类列表
     */
    public void GoodsClassify(final OnPostRequest<ArrayList<EntityGoodsClassify>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_CLASSIFY).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsClassify> list = new ArrayList<EntityGoodsClassify>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsClassify item = new EntityGoodsClassify();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("cid"));
                                    item.setName(obj.getString("cname"));
                                    //item.setIcon(obj.getString("icon"));
                                    item.setIcon(obj.getString("new_icon"));//使用新版图标
                                    item.setSmallIcon(obj.getString("small_icon"));
                                    item.setPostIcon(obj.getString("post_icon"));
                                    item.setPostIconActive(obj.getString("post_icon_clicked"));
                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsClassify>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }


    /**
     * 在卖商品列表
     */
    public void GoodsSelling(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityGoodsSelling>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_SELLING)
                    .addString("cid", params.get("cid")).addString("order_c", params.get("order"))
                    .addString("lati", params.get("lati")).addString("longi", params.get("longi"))
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .addString("auth", params.get("auth")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsSelling> list = new ArrayList<EntityGoodsSelling>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsSelling item = new EntityGoodsSelling();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setTitle(obj.getString("title"));
                                    item.setContent(obj.getString("describe"));
                                    item.setPrice(obj.getString("price"));
                                    item.setTime(obj.getString("publish_time"));
                                    item.setCollectNum(obj.getString("collect_num"));
                                    item.setCommentNum(obj.getString("comment_num"));
                                    item.setUserId(obj.getString("uid"));
                                    item.setUserHeader(obj.getString("header"));
                                    item.setUserName(obj.getString("nickname"));
                                    item.setUserNickName(obj.getString("nickname"));
                                    item.setDistance(obj.getString("dist"));
                                    item.setCollect(obj.getInt("is_collect") == 1);
                                    item.setComment(obj.getInt("is_comment") == 1);
                                    item.setNew(obj.getInt("new") == 1);
                                    item.setBargin(obj.getInt("bargin") == 1);
                                    item.setAuth(obj.getInt("auth") == 1);
                                    item.setImage(obj.getString("recommend_img"));

                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsSelling>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 在卖商品详情
     */
    public void GoodsInfo(HashMap<String, String> params, final OnPostRequest<EntityGoodsSelling> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_INFO)
                    .addString("id", params.get("id")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                EntityGoodsSelling item = new EntityGoodsSelling();
                                JSONObject obj = new JSONObject(jsonObject.getString(RESPONSE_JSON_DATA));
                                JSONArray imageArray = new JSONArray(obj.getString("img"));
                                ArrayList<EntityImage> images = new ArrayList<EntityImage>();
                                //"img":[{"img":"http:\/\/www.erhuoapp.com\/images\/201501220355--37252.jpg","width":null,"height":null}]}}
                                for (int i = 0; i < imageArray.length(); i++) {
                                    EntityImage image = new EntityImage();
                                    JSONObject img = imageArray.getJSONObject(i);
                                    image.setImage(img.getString("img"));
                                    image.setImageWidth(img.getInt("width"));
                                    image.setImageHeight(img.getInt("height"));
                                    images.add(image);
                                }
                                item.setImages(images);

                                item.setId(obj.getString("id"));
                                item.setTitle(obj.getString("title"));
                                item.setContent(obj.getString("describe"));
                                item.setPrice(obj.getString("price"));
                                item.setTime(obj.getString("publish_time"));
                                item.setCollectNum(obj.getString("collect_num"));
                                item.setCommentNum(obj.getString("comment_num"));
                                item.setUserId(obj.getString("uid"));
                                item.setUserHeader(obj.getString("header"));
                                item.setUserName(obj.getString("nickname"));
                                item.setUserNickName(obj.getString("nickname"));
                                item.setDistance(obj.getString("dist"));
                                item.setCollect(obj.getInt("is_collect") == 1);
                                item.setComment(obj.getInt("is_comment") == 1);
                                item.setNew(obj.getInt("new") == 1);
                                item.setBargin(obj.getInt("bargin") == 1);
                                item.setAuth(obj.getInt("auth") == 1);
                                item.setCid(obj.getString("cid"));//类别id

                                result = item;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityGoodsSelling) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 求购商品列表
     */
    public void GoodsBuying(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityGoodsBuying>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_BUYING)
                    .addString("order_c", params.get("order")).addString("auth", params.get("auth"))
                    .addString("lati", params.get("lati")).addString("longi", params.get("longi"))
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsBuying> list = new ArrayList<EntityGoodsBuying>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                //Log.e(TAG, entityHttpResponse.getMessage());//
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsBuying item = new EntityGoodsBuying();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setTitle(obj.getString("title"));
                                    item.setContent(obj.getString("describe"));
                                    item.setPrice(obj.getString("price"));
                                    item.setTime(obj.getString("publish_time"));
                                    item.setCollectNum(obj.getString("collect_num"));
                                    item.setCommentNum(obj.getString("comment_num"));
                                    item.setUserId(obj.getString("uid"));
                                    item.setUserHeader(obj.getString("header"));
                                    item.setUserNickName(obj.getString("nickname"));
                                    item.setDistance(obj.getString("dist"));
                                    item.setCollect(obj.getInt("is_collect") == 1);
                                    item.setComment(obj.getInt("is_comment") == 1);
                                    item.setAuth(obj.getInt("auth") == 1);

                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsBuying>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 搜索商品
     */
    public void GoodsSearch(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityGoodsSelling>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_SEARCH)
                    .addString("keyword", params.get("keyword")).addString("order_c", params.get("order"))
                    .addString("lati", params.get("lati")).addString("longi", params.get("longi"))
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .addString("auth", params.get("auth")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsSelling> list = new ArrayList<EntityGoodsSelling>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsSelling item = new EntityGoodsSelling();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setTitle(obj.getString("title"));
                                    item.setContent(obj.getString("describe"));
                                    item.setPrice(obj.getString("price"));
                                    item.setTime(obj.getString("publish_time"));
                                    item.setCollectNum(obj.getString("collect_num"));
                                    item.setCommentNum(obj.getString("comment_num"));
                                    item.setUserId(obj.getString("uid"));
                                    item.setUserHeader(obj.getString("header"));
                                    item.setUserName(obj.getString("nickname"));
                                    item.setUserNickName(obj.getString("nickname"));
                                    item.setDistance(obj.getString("dist"));
                                    item.setCollect(obj.getInt("is_collect") == 1);
                                    item.setComment(obj.getInt("is_comment") == 1);
                                    item.setNew(obj.getInt("new") == 1);
                                    item.setBargin(obj.getInt("bargin") == 1);
                                    item.setAuth(obj.getInt("auth") == 1);
                                    item.setImage(obj.getString("recommend_img"));

                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsSelling>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 获取用户信息
     */
    public void UserInfo(final OnPostRequest<EntityUserInfo> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging()
                    .addApi(HTTP_API_USER_INFO).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    EntityUserInfo entityUserInfo = new EntityUserInfo();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, multipartEntities[0]);
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            Log.e(TAG, entityHttpResponse.getMessage());
                            entityUserInfo.setS(jsonObject.getInt(RESPONSE_JSON_ERROR));//JSON状态码
                            if (0 == entityUserInfo.getS()) {
                                JSONObject object = new JSONObject(jsonObject.getString(RESPONSE_JSON_DATA));
                                entityUserInfo.setId(object.getString("uid"));
                                entityUserInfo.setName(object.getString("uname"));
                                entityUserInfo.setNickName(object.getString("nickname"));
                                entityUserInfo.setHeader(object.getString("header"));
                                entityUserInfo.setSex(object.getString("sex"));
                                entityUserInfo.setPhone(object.getString("phone"));
                                entityUserInfo.setSchool(object.getString("school"));
                                entityUserInfo.setRegisterTime(object.getString("reg_time"));
                                entityUserInfo.setGrade(object.getString("grade"));
                                entityUserInfo.setIsAuth(object.getInt("auth") == 1);
                                entityUserInfo.setAddress(object.getString("address"));
                                entityUserInfo.setAuth(object.getInt("auth"));//看起来重复，实际有用，在账号管理中

                                result = entityUserInfo;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityUserInfo) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 获取其他用户信息 [和获取用户信息差不多，只是我不想改动已有的UserInfo方法]
     */
    public void OtherUserInfo(HashMap<String, String> params, final OnPostRequest<EntityUserInfo> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging()
                    .addString("uid", params.get("uid")).addApi(HTTP_API_USER_INFO).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    EntityUserInfo entityUserInfo = new EntityUserInfo();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, multipartEntities[0]);
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            Log.e(TAG, entityHttpResponse.getMessage());
                            entityUserInfo.setS(jsonObject.getInt(RESPONSE_JSON_ERROR));//JSON状态码
                            if (0 == entityUserInfo.getS()) {
                                JSONObject object = new JSONObject(jsonObject.getString(RESPONSE_JSON_DATA));
                                entityUserInfo.setId(object.getString("uid"));
                                entityUserInfo.setNickName(object.getString("nickname"));
                                entityUserInfo.setHeader(object.getString("header"));
                                entityUserInfo.setSex(object.getString("sex"));
                                entityUserInfo.setSchool(object.getString("school"));
                                entityUserInfo.setGrade(object.getString("grade"));
                                entityUserInfo.setIsAuth(object.getInt("auth") == 1);
                                entityUserInfo.setSellingnum(object.getString("sell_num"));
                                entityUserInfo.setSoldnum(object.getString("sold_num"));
                                entityUserInfo.setBuyingnum(object.getString("shop_num"));

                                result = entityUserInfo;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityUserInfo) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }


    /**
     * 添加收藏
     */
    public void AddCollect(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_COLLECT_ADD)
                    .addString("sid", params.get("id")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 取消收藏
     */
    public void RemoveCollect(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_COLLECT_REMOVE)
                    .addString("sid", params.get("id")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 我的收藏列表
     */
    public void UserInfoFavorite(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityGoodsSelling>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_COLLECT_MY)
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsSelling> list = new ArrayList<EntityGoodsSelling>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            //Log.e(TAG, "UserInfoFavorite: "+entityHttpResponse.getMessage());
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsSelling item = new EntityGoodsSelling();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("sid"));
                                    item.setTitle(obj.getString("title"));
                                    item.setContent(obj.getString("describe"));
                                    item.setPrice(obj.getString("price"));
                                    item.setTime(obj.getString("publish_time"));
                                    item.setCollectNum(obj.getString("collect_num"));
                                    item.setCommentNum(obj.getString("comment_num"));
                                    item.setImage(obj.getString("recommend_img"));
                                    item.setNew(obj.getInt("new") == 1);
                                    item.setBargin(obj.getInt("bargin") == 1);

                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsSelling>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 我的求购商品列表
     */
    public void UserInfoBuying(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityGoodsBuying>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_BUYING_MY)
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsBuying> list = new ArrayList<EntityGoodsBuying>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsBuying item = new EntityGoodsBuying();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setTitle(obj.getString("title"));
                                    item.setContent(obj.getString("describe"));
                                    item.setPrice(obj.getString("price"));
                                    item.setTime(obj.getString("publish_time"));
                                    item.setCommentNum(obj.getString("comment_num"));

                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsBuying>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 我的已售列表
     */
    public void UserInfoSold(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityGoodsSelling>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_SOLD_MY)
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsSelling> list = new ArrayList<EntityGoodsSelling>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsSelling item = new EntityGoodsSelling();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setTitle(obj.getString("title"));
                                    item.setContent(obj.getString("describe"));
                                    item.setPrice(obj.getString("price"));
                                    item.setTime(obj.getString("publish_time"));
                                    item.setCollectNum(obj.getString("collect_num"));
                                    item.setCommentNum(obj.getString("comment_num"));
                                    item.setImage(obj.getString("recommend_img"));
                                    item.setNew(obj.getInt("new") == 1);
                                    item.setBargin(obj.getInt("bargin") == 1);

                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsSelling>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 我的出售列表
     */
    public void UserInfoSelling(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityGoodsSelling>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_SELLING_MY)
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsSelling> list = new ArrayList<EntityGoodsSelling>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsSelling item = new EntityGoodsSelling();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setTitle(obj.getString("title"));
                                    item.setContent(obj.getString("describe"));
                                    item.setPrice(obj.getString("price"));
                                    item.setTime(obj.getString("publish_time"));
                                    item.setCollectNum(obj.getString("collect_num"));
                                    item.setCommentNum(obj.getString("comment_num"));
                                    item.setImage(obj.getString("recommend_img"));
                                    item.setNew(obj.getInt("new") == 1);
                                    item.setBargin(obj.getInt("bargin") == 1);
                                    item.setCid(obj.getString("cid"));//类别id

                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsSelling>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 发布求购
     */
    public void AddBuying(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_PUBLISH_BUYING)
                    .addString("title", params.get("title")).addString("describe", params.get("content"))
                    .addString("lati", params.get("lati")).addString("longi", params.get("longi"))
                    .addString("price", params.get("price")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 修改求购
     */
    public void UpdateBuying(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_PUBLISH_BUYING_UPDATE)
                    .addString("title", params.get("title")).addString("describe", params.get("content"))
                    .addString("lati", params.get("lati")).addString("longi", params.get("longi"))
                    .addString("price", params.get("price")).addString("location", params.get("location"))
                    .addString("id", params.get("id")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 求购商品详情
     */
    public void GoodsBuyingInfo(HashMap<String, String> params, final OnPostRequest<EntityGoodsBuying> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_BUYING_INFO)
                    //.addString("lati", params.get("lati")).addString("longi", params.get("longi"))
                    .addString("id", params.get("id")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                EntityGoodsBuying item = new EntityGoodsBuying();
                                JSONObject obj = new JSONObject(jsonObject.getString(RESPONSE_JSON_DATA));
                                item.setId(obj.getString("id"));
                                item.setTitle(obj.getString("title"));
                                item.setContent(obj.getString("describe"));
                                item.setPrice(obj.getString("price"));
                                item.setTime(obj.getString("publish_time"));
                                item.setCollectNum(obj.getString("collect_num"));
                                item.setCommentNum(obj.getString("comment_num"));
                                item.setUserId(obj.getString("uid"));
                                item.setUserHeader(obj.getString("header"));
                                item.setUserNickName(obj.getString("nickname"));
                                item.setDistance(obj.getString("dist"));
                                item.setCollect(obj.getInt("is_collect") == 1);
                                item.setComment(obj.getInt("is_comment") == 1);
                                item.setAuth(obj.getInt("auth") == 1);
                                result = item;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityGoodsBuying) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 删除求购
     */
    public void RemoveBuying(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_PUBLISH_BUYING_DELETE)
                    .addString("id", params.get("id")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }


    /**
     * 他人的出售列表
     */
    public void OtherSelling(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityGoodsSelling>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_SELLING_OTHER)
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .addString("uid", params.get("uid")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsSelling> list = new ArrayList<EntityGoodsSelling>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsSelling item = new EntityGoodsSelling();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setTitle(obj.getString("title"));
                                    item.setContent(obj.getString("describe"));
                                    item.setPrice(obj.getString("price"));
                                    item.setTime(obj.getString("publish_time"));
                                    item.setCollectNum(obj.getString("collect_num"));
                                    item.setCommentNum(obj.getString("comment_num"));
                                    item.setImage(obj.getString("recommend_img"));
                                    item.setNew(obj.getInt("new") == 1);
                                    item.setBargin(obj.getInt("bargin") == 1);
                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsSelling>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 他人的求购商品列表
     */
    public void OtherBuying(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityGoodsBuying>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_BUYING_OTHER)
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .addString("uid", params.get("uid")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsBuying> list = new ArrayList<EntityGoodsBuying>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsBuying item = new EntityGoodsBuying();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setTitle(obj.getString("title"));
                                    item.setContent(obj.getString("describe"));
                                    item.setPrice(obj.getString("price"));
                                    item.setTime(obj.getString("publish_time"));
                                    item.setCommentNum(obj.getString("comment_num"));

                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsBuying>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 他人的已售列表
     */
    public void OtherSold(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityGoodsSelling>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_SOLD_OTHER)
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .addString("uid", params.get("uid")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityGoodsSelling> list = new ArrayList<EntityGoodsSelling>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityGoodsSelling item = new EntityGoodsSelling();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setTitle(obj.getString("title"));
                                    item.setContent(obj.getString("describe"));
                                    item.setPrice(obj.getString("price"));
                                    item.setTime(obj.getString("publish_time"));
                                    item.setCollectNum(obj.getString("collect_num"));
                                    item.setCommentNum(obj.getString("comment_num"));
                                    item.setImage(obj.getString("recommend_img"));
                                    item.setNew(obj.getInt("new") == 1);
                                    item.setBargin(obj.getInt("bargin") == 1);
                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityGoodsSelling>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 意见反馈 [其实这个OnPostRequest中不需要执行什么内容，因为没有反馈数据]
     */
    public void PostAdvice(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_POST_ADVICE)
                    .addString("content", params.get("content")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 热门搜索
     */
    public void HotSearch(HashMap<String, String> params, final OnPostRequest<ArrayList<String>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_HOT_SEARCH)
                    .addString("num", params.get("num")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<String> list = new ArrayList<String>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {//"RESPONSE_JSON_ERROR"
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    list.add(jsonArray.getString(i));
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<String>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 更新用户信息
     */
    public void UpdateUserInfo(HashMap<String, String> params, File headerFile, final OnPostRequest<EntityUserInfo> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_EDIT_USER_INFO)
                    .addString("sex", params.get("sex")).addString("nickname", params.get("nickname"))
                    .addString("address", params.get("address")).getMultipartEntity();
            if (params.get("headerupdate").equalsIgnoreCase("1")) {
                multipartEntity.addPart("upfile", new FileBody(headerFile));
            }
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    EntityUserInfo entityUserInfo = new EntityUserInfo();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, multipartEntities[0]);
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            Log.e(TAG, entityHttpResponse.getMessage());
                            entityUserInfo.setS(jsonObject.getInt(RESPONSE_JSON_ERROR));//JSON状态码
                            if (0 == entityUserInfo.getS()) {
                                entityUserInfo.setHeader(jsonObject.getString("header"));
                                result = entityUserInfo;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityUserInfo) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 用户请求短信验证码
     */
    public void UserRequestCode(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_REQUEST_CODE)
                    .addString("phone", params.get("phone")).addString("type", params.get("type"))
                    .addString("old_phone", params.get("old_phone")).addString("token", params.get("token"))
                    .getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, multipartEntities[0]);
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {//JSON状态码
                                result = entityHttpResponse;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 用户请求验证短信验证码
     */
    public void UserValidateCode(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_VALIDATE_CODE)
                    .addString("phone", params.get("phone")).addString("type", params.get("type"))
                    .addString("code", params.get("code")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, multipartEntities[0]);
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {//JSON状态码
                                // 不一定包含token
                                if (jsonObject.has(RESPONSE_JSON_TOKEN)) {
                                    entityHttpResponse.setToken(jsonObject.getString(RESPONSE_JSON_TOKEN));
                                }
                                if (jsonObject.has(RESPONSE_JSON_DESCRIPTION)) {
                                    entityHttpResponse.setDescription(jsonObject.getString(RESPONSE_JSON_DESCRIPTION));
                                }
                                result = entityHttpResponse;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }


    /**
     * 商品评论列表
     */
    public void GoodsCommentList(HashMap<String, String> params, final OnPostRequest<ArrayList<EntityComment>> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_GOODS_COMMENT)
                    .addPageIndex(params.get("pageIndex")).addDisplayNumber(params.get("displayNumber"))
                    .addString("id", params.get("id")).addString("type", params.get("type"))
                    .getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    ArrayList<EntityComment> list = new ArrayList<EntityComment>();
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            //Log.e(TAG, "GoodsCommentList: "+entityHttpResponse.getMessage());
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONArray jsonArray = jsonObject.getJSONArray(RESPONSE_JSON_DATA);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    EntityComment item = new EntityComment();
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    item.setId(obj.getString("id"));
                                    item.setContent(obj.getString("content"));
                                    item.setTime(obj.getString("dateline"));
                                    item.setUid(obj.getString("uid"));
                                    item.setUserHeader(obj.getString("header"));
                                    item.setUserName(obj.getString("nickname"));
                                    list.add(item);
                                }
                                result = list;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((ArrayList<EntityComment>) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 添加评论
     */
    public void AddComment(HashMap<String, String> params, final OnPostRequest<EntityComment> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_COMMENT_ADD)
                    .addString("type", params.get("type")).addString("content", params.get("content"))
                    .addString("sid", params.get("id")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                JSONObject obj = jsonObject.getJSONObject(RESPONSE_JSON_DATA);
                                EntityComment item = new EntityComment();
                                item.setId(obj.getString("id"));
                                item.setContent(obj.getString("content"));
                                item.setTime(obj.getString("dateline"));
                                item.setUid(obj.getString("uid"));
                                item.setUserHeader(obj.getString("header"));
                                item.setUserName(obj.getString("nickname"));
                                result = item;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityComment) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 上传学生证
     */
    public void UploadCard(File file, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_UPLOAD_CARD).getMultipartEntity();
            multipartEntity.addPart("upfile", new FileBody(file));//添加文件
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            Log.e(TAG, entityHttpResponse.getMessage());
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {//有可能是 上传文件太大！或者 上传文件格式不对！
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }


    /**
     * 更改手机号
     */
    public void ChangePhone(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_CHANGE_PHONE)
                    .addString("phone", params.get("phone")).addString("pwd", params.get("pwd"))
                    .getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, multipartEntities[0]);
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {//JSON状态码
                                entityHttpResponse.setToken(jsonObject.getString(RESPONSE_JSON_TOKEN));
                                result = entityHttpResponse;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }


    /**
     * 重置密码
     */
    public void ResetPassword(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_RESET_PASSWORD)
                    .addString("phone", params.get("phone")).addString("pwd", params.get("pwd"))
                    .addString("token", params.get("token")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, multipartEntities[0]);
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {//JSON状态码
                                result = entityHttpResponse;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 更改密码
     */
    public void ChangePassword(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_EDIT_PASSWORD)
                    .addString("old_pwd", params.get("old_pwd")).addString("new_pwd", params.get("new_pwd"))
                    .getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, multipartEntities[0]);
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {//JSON状态码
                                result = entityHttpResponse;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 检查更新
     */
    public void CheckUpdate(final OnPostRequest<EntityUpdate> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_CHECK_UPDATE)
                    .addString("os", "android").getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... multipartEntities) {
                    MultipartEntity multipartEntity = multipartEntities[0];
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, multipartEntities[0]);
                        // 请求成功，得到正确的状态码
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {//请求成功
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {//JSON状态码
                                JSONObject obj = jsonObject.getJSONObject(RESPONSE_JSON_DATA);
                                EntityUpdate entityUpdate = new EntityUpdate();
                                entityUpdate.setVersion(obj.getString("version"));
                                entityUpdate.setDownload(obj.getString("download"));
                                result = entityUpdate;
                            } else {//请求成功，但是结果有误，错误信息在JSON中
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {//请求失败
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityUpdate) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 发布在卖
     */
    public void AddSelling(HashMap<String, String> params, List<File> uploadFiles, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_PUBLISH_SELLING)
                    .addString("title", params.get("title")).addString("describe", params.get("describe"))
                    .addString("lati", params.get("lati")).addString("longi", params.get("longi"))
                    .addString("cid", params.get("cid")).addString("new", params.get("new"))
                    .addString("bargin", params.get("bargin")).addString("price", params.get("price")).getMultipartEntity();
            for (int i = 0; i < uploadFiles.size(); i++) {//添加文件
                File file = uploadFiles.get(i);
                multipartEntity.addPart("pic[" + i + "]", new FileBody(file));
            }
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 删除出售
     */
    public void RemoveSelling(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_PUBLISH_SELLING_DELETE)
                    .addString("id", params.get("id")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 已售出售
     */
    public void SoldSelling(HashMap<String, String> params, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_PUBLISH_SELLING_SOLD)
                    .addString("id", params.get("id")).getMultipartEntity();
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

    /**
     * 修改在卖
     */
    public void UpdateSelling(HashMap<String, String> params, List<File> uploadFiles, final OnPostRequest<EntityHttpResponse> onPostRequest) {
        try {
            // 使用快速封装方法封装一个对象
            MultipartEntity multipartEntity = new Packaging().addApi(HTTP_API_PUBLISH_SELLING_UPDATE)
                    .addString("title", params.get("title")).addString("describe", params.get("describe"))
                    .addString("lati", params.get("lati")).addString("longi", params.get("longi"))
                    .addString("cid", params.get("cid")).addString("new", params.get("new"))
                    .addString("bargin", params.get("bargin")).addString("price", params.get("price"))
                    .addString("id", params.get("id")).addString("location", params.get("location"))
                    .getMultipartEntity();
            if (params.get("picupdate").equalsIgnoreCase("1")) {//修改了图片的话才要重新发送
                for (int i = 0; i < uploadFiles.size(); i++) {//添加文件
                    File file = uploadFiles.get(i);
                    multipartEntity.addPart("pic[" + i + "]", new FileBody(file));
                }
            }
            // 开始异步加载数据
            new AsyncTask<MultipartEntity, Integer, Object>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if (null != onPostRequest) {
                        onPostRequest.onPost();
                    }
                }

                @Override
                protected Object doInBackground(MultipartEntity... params) {
                    Object result = null;
                    try {
                        EntityHttpResponse entityHttpResponse = doPost(HTTP_API_URL, params[0]);
                        if (entityHttpResponse.getStatuscode() == EntityHttpResponse.STATUS_CODE_OK) {
                            JSONObject jsonObject = new JSONObject(entityHttpResponse.getMessage());
                            //Log.e(TAG, entityHttpResponse.getMessage());
                            if (0 == jsonObject.getInt(RESPONSE_JSON_ERROR)) {
                                result = entityHttpResponse;
                            } else {
                                result = jsonObject.getString(RESPONSE_JSON_DESCRIPTION);
                            }
                        } else {
                            result = "接口请求失败，错误码：" + entityHttpResponse.getStatuscode();
                        }
                    } catch (ParseException e) {
                        result = e.toString();
                    } catch (IOException e) {
                        result = e.toString();
                    } catch (JSONException e) {
                        result = e.toString();
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Object result) {
                    if (null != onPostRequest) {
                        if (!(result instanceof String)) {
                            onPostRequest.onPostOk((EntityHttpResponse) result);
                        } else {
                            onPostRequest.onPostFailure((String) result);
                        }
                    }
                }
            }.execute(multipartEntity);
        } catch (UnsupportedEncodingException e1) {
            if (null != onPostRequest) {
                onPostRequest.onPostFailure(e1.toString());
            }
        }
    }

}
