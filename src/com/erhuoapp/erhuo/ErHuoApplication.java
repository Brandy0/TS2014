package com.erhuoapp.erhuo;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnMessageNotifyListener;
import com.erhuoapp.erhuo.im.domain.User;
import com.erhuoapp.erhuo.util.AppUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 *
 * 贰货应用程序
 *
 * @author hujiawei
 * @datetime 15/1/5 17:07
 */
public class ErHuoApplication extends Application {

	/**login user name*/ 
	public final String PREF_USERNAME = "username";
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
	public static Context applicationContext;
    private static ErHuoApplication instance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        // ImageLoader初始化
        initImageLoader(getApplicationContext());
        // 初始化app的工具类
        AppUtil.setContext(getApplicationContext());
        
        
        applicationContext = this;
        instance = this;
        hxSDKHelper.onInit(applicationContext);

        
    }

    /**
     * 获取APP名称
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return processName;
    }

    /**
     * 初始化ImageLoader
     * http://blog.csdn.net/vipzjyno1/article/details/23206387
     * ImageLoader﹕ failed to connect to www.erhuoapp.com/123.57.132.230 (port 80) after 5000ms
     java.net.SocketTimeoutException: failed to connect to www.erhuoapp.com/123.57.132.230 (port 80) after 5000ms
     */
    public static void initImageLoader(Context context) {
        // 控制缓存文件的位置
        File cacheDir =  StorageUtils.getOwnCacheDirectory(context, "ErHuoCache");
        // 默认的图片显示设置
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED).bitmapConfig(Bitmap.Config.RGB_565).build();
        // 图片加载工具的配置
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(100 * 1024 * 1024)
                .memoryCache(new WeakMemoryCache())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .imageDownloader(new BaseImageDownloader(context, 10 * 1000, 30 * 1000))
                .build();
        //.writeDebugLogs()
        //.memoryCacheSize(10 * 1024 * 1024)
        // BaseImageDownloader 时间配置
        ImageLoader.getInstance().init(config);
    }

    public static ErHuoApplication getInstance() {
		return instance;
	}
 
	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, User> getContactList() {
	    return hxSDKHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
	    hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param user
	 */
	public void setUserName(String username) {
	    hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
	    hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
	    hxSDKHelper.logout(emCallBack);
	}

}
