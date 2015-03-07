package com.erhuoapp.erhuo.util;

import android.content.Context;
import android.widget.Toast;

/**
 *
 * 显示Toast信息的工具类
 *
 * NOTE: 可以改善成类似AppUtil类形式，这样两个方法都可以减少context参数
 *
 * @author hujiawei
 * @datetime 15/1/1 23:42
 */
public class ToastUtil {

    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
