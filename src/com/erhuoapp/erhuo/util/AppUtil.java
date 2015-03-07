package com.erhuoapp.erhuo.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.erhuoapp.erhuo.model.EntityUserInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

/**
 * 应用程序工具类
 *
 * @author hujiawei
 * @datetime 15/1/1 23:22
 */
public class AppUtil implements IConstants {

    private static final String TAG = "AppUtil";

    public static List<Cookie> cookies;
    public static String cookieStr;
    
    private static Context context;
    private static AppUtil instance;

    private static final int MAX_HISTORY_SIZE = 5;// 最多保存的搜索历史条数

    private AppUtil() {
    }

    public static AppUtil getInstance() {//简单形式的单例模式
        if (instance == null) {
            instance = new AppUtil();
        }
        return instance;
    }

    // 向sharedPreferences中添加键值对数据
    public void saveKeyValue(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // 同样是保存键值对数据，但是这里是保存布尔类型数据
    public void saveKeyValue(String key, Boolean value) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // 删除键值对数据
    public void removeKeyValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    // 得到sharedPreferences
    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREF_KEY_APP, Activity.MODE_PRIVATE);
    }

    // 得到用户相关数据的方法
    public String getUserId() {
        return getSharedPreferences().getString(PREF_KEY_UID, "");
    }

    public String getToken() {
        return getSharedPreferences().getString(PREF_KEY_TOKEN, "");
    }

    public String getUserName() {
        return getSharedPreferences().getString(PREF_KEY_NAME, "");
    }

    public String getUserSchool() {
        return getSharedPreferences().getString(PREF_KEY_SCHOOL, "");
    }

    public String getUserGrade() {
        return getSharedPreferences().getString(PREF_KEY_GRADE, "");
    }

    public String getUserHeader() {
        return getSharedPreferences().getString(PREF_KEY_HEADER, "");
    }

    public String getUserNickname() {return getSharedPreferences().getString(PREF_KEY_NICKNAME, "");}

    public String getUserSex() {return getSharedPreferences().getString(PREF_KEY_SEX, "");}

    public String getUserTime() {return getSharedPreferences().getString(PREF_KEY_TIME, "");}

    public String getUserAddress() {return getSharedPreferences().getString(PREF_KEY_ADDRESS, "");}

    public String getUserPhone() {
        return getSharedPreferences().getString(PREF_KEY_PHONE, "");
    }

    public boolean getUserAuth() {
        return getSharedPreferences().getBoolean(PREF_KEY_AUTH, false);
    }

    // 从SharedPreferences中获取用户的信息
    public EntityUserInfo getBasicUserInfo() {
        EntityUserInfo userInfo = new EntityUserInfo();
        userInfo.setId(getUserId());
        userInfo.setName(getUserName());

        userInfo.setNickName(getUserNickname());
        userInfo.setHeader(getUserHeader());
        userInfo.setSchool(getUserSchool());
        userInfo.setGrade(getUserGrade());
        userInfo.setIsAuth(getUserAuth());
        userInfo.setPhone(getUserPhone());
        userInfo.setSex(getUserSex());
        userInfo.setRegisterTime(getUserTime());
        userInfo.setAddress(getUserAddress());//9项
        return userInfo;
    }

    // 保存用户的数据，这个只是在注册或者登录的时候使用 [其实这里每次调用saveKeyValue并不好=z=]
    // 具体的用户的其他信息会在登陆成功之后重新加载一次然后保存下来
    public void saveUserData(String uid, String token) {
        saveKeyValue(PREF_KEY_UID, uid);
        saveKeyValue(PREF_KEY_TOKEN, token);
        saveKeyValue(PREF_KEY_LOGIN, true);//有用户登录数据了
    }

    public void saveNickName(String  info) {
        saveKeyValue(PREF_KEY_NICKNAME, info);
    }
    // 应用启动的时候便会加载用户信息，如果获取到了的话就保存下来
    public void saveUserInfo(EntityUserInfo info) {//id,name不改
        saveKeyValue(PREF_KEY_NAME, info.getName());
        saveKeyValue(PREF_KEY_NICKNAME, info.getNickName());
        saveKeyValue(PREF_KEY_HEADER, info.getHeader());
        saveKeyValue(PREF_KEY_AUTH, info.getIsAuth());
        saveKeyValue(PREF_KEY_GRADE, info.getGrade());
        saveKeyValue(PREF_KEY_SCHOOL, info.getSchool());
        saveKeyValue(PREF_KEY_PHONE, info.getPhone());
        saveKeyValue(PREF_KEY_SEX, info.getSex());
        saveKeyValue(PREF_KEY_TIME, info.getRegisterTime());
        saveKeyValue(PREF_KEY_ADDRESS, info.getAddress());//9项
    }

    // 删除用户数据 退出用户账号时调用
    // token其实可以不用删除，因为发送post请求是根据PREF_KEY_LOGIN是否存在
    public void removeUserData() {
        removeKeyValue(PREF_KEY_NICKNAME);
        removeKeyValue(PREF_KEY_HEADER);
        removeKeyValue(PREF_KEY_AUTH);
        removeKeyValue(PREF_KEY_GRADE);
        removeKeyValue(PREF_KEY_SCHOOL);
        removeKeyValue(PREF_KEY_PHONE);
        removeKeyValue(PREF_KEY_SEX);
        removeKeyValue(PREF_KEY_TIME);
        removeKeyValue(PREF_KEY_ADDRESS);//9项

        removeKeyValue(PREF_KEY_UID);
        removeKeyValue(PREF_KEY_NAME);
        removeKeyValue(PREF_KEY_TOKEN);
        removeKeyValue(PREF_KEY_LOGIN);
    }

    // 判断是否存在用户数据
    public boolean checkUserLogin() {
        return getSharedPreferences().getBoolean(PREF_KEY_LOGIN, false);
    }

    // 得到用户的搜索历史 [因为SharedPreferences不支持保存list，所以采用下面封装的方法共外部调用，看起来像是支持]
    public ArrayList<String> getSearchHistory() {
        ArrayList<String> historyList = new ArrayList<String>();
        String str = null;
        for (int i = 0; i < MAX_HISTORY_SIZE; i++) {
            str = getSharedPreferences().getString("history_" + i, "");
            if (!str.equalsIgnoreCase("")) {//如果不为空就加上
                historyList.add(str);
            }
        }
        return historyList;
    }

    // 保存搜索历史
    public void saveSearchHistory(ArrayList<String> historyList) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //只保留前面5个就行(或者historyList较小时保存全部)
        for (int i = 0; i < MAX_HISTORY_SIZE && i < historyList.size(); i++) {
            editor.putString("history_" + i, historyList.get(i));
        }
        editor.commit();
    }

    /**
     * 修改版本的官方参考实现 (其实是综合了下面两个网址中方法)
     * http://developer.android.com/training/camera/photobasics.html
     * http://developer.android.com/guide/topics/media/camera.html#intents
     */
    public static File createImageFile() throws IOException {
        //为了安全，先要判断是否有SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //这里增加一层，放在ErHuo文件夹下
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ErHuo");
        if (!storageDir.exists()) storageDir.mkdirs();
        //创建一个临时文件用来保存图片
        return File.createTempFile(imageFileName,  /* prefix */ ".jpg",  /* suffix */ storageDir /* directory */);
        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = "file:" + image.getAbsolutePath();
        //currentPhotoUri = "file://" + image.getAbsolutePath();//这种形式的地址UIL能够加载得到
        //Log.e(TAG, "file path: " + currentPhotoPath);
        //file:/mnt/sdcard/Pictures/ErHuo/JPEG_20150128_210727_-1010846197.jpg
        //return image;
    }

    /////// 缩放图片 使用中  note测试：2.6MB 图片 压缩到 270kB 左右 ////////

    /**
     * 根据路径获取图片并压缩，图片按比例大小压缩方法
     * 修改自http://104zz.iteye.com/blog/1694762
     */
    public static void saveImage(String srcPath, File toPath) throws FileNotFoundException {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        bitmap = compressSize(bitmap);//压缩好比例大小后再进行质量压缩，保存图片文件
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(toPath));
    }

    /**
     * 质量压缩图片，使其小于1M
     * http://104zz.iteye.com/blog/1694762
     */
    public static Bitmap compressSize(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {//循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
    }

    // 对字符串进行加密 [验证过没有问题，返回结果是大写的32为MD5加密结果]
    public static String encodeWithMD5(String originString) {
        try {
            //创建具有指定算法名称的信息摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
            byte[] results = md.digest(originString.getBytes());
            //将得到的字节数组变成字符串返回
            String resultString = byteArrayToHexString(results);
            return resultString.toUpperCase();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // 转换字节数组为十六进制字符串
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes) {
            buffer.append(byteToHexString(b));
        }
        return buffer.toString();
    }

    // 十六进制下数字到字符的映射数组
    private final static String[] hexDigits = {"0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    // 将一个字节转化成十六进制形式的字符串
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        AppUtil.context = context;
    }

}
