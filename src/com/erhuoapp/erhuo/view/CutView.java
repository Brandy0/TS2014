package com.erhuoapp.erhuo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;


/**
 * cutView
 * //对外可调用方法
 * //构造函数 	cutView(Context context, Bitmap 原图,int 屏幕宽,int 屏幕高)
 * //获取截图	Bitmap getBitmap()
 * //改变大小	void changeRadius(int 半径)
 * <p/>
 * NOTE: 2015/2/3 hujiawei 进行了一些修改
 *
 * @author chenjianhui
 * @datetime 15/2/3 09:30
 */
public class CutView extends View {

    //父窗口
    Context context;//hujiawei rename

    //图片
    Bitmap btm_old;
    Bitmap btm_photo;
    Bitmap btm_cut;

    //截图区位置参数
    private int l;
    private int t;
    private int r;
    private int b;

    //偏移、缩放参数
    private int bitmap_l;
    private int bitmap_t;
    private float bitmap_s = 1;
    private float bitmap_st = 1;

    //屏幕尺寸
    private int displayWidth;
    private int displayHeight;

    //图片尺寸
    private int bitmapWidth;
    private int bitmapHeight;

    //View尺寸
    private int cutViewWidth;
    private int cutViewHeight;

    //截图区半径
    private int R = 100;//hujiawei 200 -> 100

    //首次OnDraw判断参数
    private boolean first_draw = true;

    //public CutView(Context context, Bitmap bitmap,int DWidth,int DHeight) {
    public CutView(Context context, Bitmap bitmap) {//, int DWidth, int DHeight
        super(context);
        this.context = context;

        //hujiawei 改用Context来取得屏幕长宽，而不用通过传递参数的方式得到
        DisplayMetrics metrics = new DisplayMetrics();
        ((android.app.Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        displayWidth = metrics.widthPixels;
        displayHeight = metrics.heightPixels;

        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();

        this.btm_photo = bitmap;
        this.btm_old = bitmap;

        //判断原图大小并缩放至合适大小
        bitmap_st = (float) Math.max(Math.min(1.0 * displayWidth / bitmapWidth,
                        0.6 * displayHeight / bitmapHeight),
                Math.max(2.0 * R / bitmapWidth, 2.0 * R / bitmapHeight));
        //btm_photo = BitmapScale(btm_old, bitmap_st);

        bitmap_s = bitmap_st;

        //获取缩放后图片大小
        //bitmapWidth = btm_photo.getWidth();
        //bitmapHeight = btm_photo.getHeight();
        //bitmapWidth = (int) (bitmapWidth*bitmap_st);
        //bitmapHeight = (int) (bitmapHeight*bitmap_st);

        //计算缩放后图片放置位置
        bitmap_l = (int) ((displayWidth - bitmapWidth * bitmap_s) / 2);
        bitmap_t = (int) ((displayHeight * 3 / 5 - bitmapHeight * bitmap_s) / 2);
    }

    //触摸坐标参数
    private int Tx00, Ty00, Tx01, Ty01, Tx02, Ty02;
    private int Tx10, Ty10, Tx11, Ty11, Tx12, Ty12;

    //双指缩放比例参数
    private double l1, l2;

    //偏移和缩放
    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("deprecation")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    Tx00 = (int) event.getX();
                    Ty00 = (int) event.getY();
                    Tx02 = Tx00;
                    Ty02 = Ty00;
                    //Toast.makeText(context, "touched "+x0+" "+y0, 0).show();
                    //invalidate();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    Tx01 = Tx02;
                    Ty01 = Ty02;
                    Tx02 = (int) event.getX();
                    Ty02 = (int) event.getY();
                    //Toast.makeText(context, "touch_moved "+x1+" "+y1+"|"+x2+" "+y2, 0).show();
                    if (Math.abs(Tx02 - Tx01) < 100 && Math.abs(Ty02 - Ty01) < 100) {
                        myMove(Tx02 - Tx01, Ty02 - Ty01);
                        invalidate();
                    }
                    break;
                }
                default:
                    break;
            }
        } else if (pointerCount == 2) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    Tx00 = (int) event.getX(0);
                    Ty00 = (int) event.getY(0);
                    Tx02 = Tx00;
                    Ty02 = Ty00;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    Tx01 = Tx02;
                    Ty01 = Ty02;
                    Tx02 = (int) event.getX(0);
                    Ty02 = (int) event.getY(0);
                    Tx11 = Tx12;
                    Ty11 = Ty12;
                    Tx12 = (int) event.getX(1);
                    Ty12 = (int) event.getY(1);
                    //Toast.makeText(context, "double_touched! "+Tx02+" "+Ty02+"|"+Tx12+" "+Ty12, 0).show();
                    l1 = Math.sqrt((Tx11 - Tx01) * (Tx11 - Tx01) + (Ty11 - Ty01) * (Ty11 - Ty01));
                    l2 = Math.sqrt((Tx12 - Tx02) * (Tx12 - Tx02) + (Ty12 - Ty02) * (Ty12 - Ty02));
                    if (l1 != 0) {
                        myZoom(l2 / l1, (Tx00 + Tx10) / 2, (Ty00 + Ty10) / 2);
                        invalidate();
                    }
                    //Toast.makeText(context, "double_touched! "+Tx02+" "+Ty02+"|"+Tx12+" "+Ty12, 0).show();
                    //Toast.makeText(context, "double_touched! "+l1+" "+l2, 0).show();
                    break;
                }
                case MotionEvent.ACTION_POINTER_2_DOWN: {
                    Tx10 = (int) event.getX(1);
                    Ty10 = (int) event.getY(1);
                    Tx12 = Tx10;
                    Ty12 = Ty10;
                    //l1 = Math.sqrt((Tx10-Tx00)*(Tx10-Tx00)+(Ty10-Ty00)*(Ty10-Ty00));
                    //Toast.makeText(context, "double_touched! "+Tx00+" "+Ty00+"|"+Tx10+" "+Ty10, 0).show();
                    break;
                }
                case MotionEvent.ACTION_POINTER_2_UP: {
                    //bitmap_st = (float) (bitmap_st*(l2/l1));
                    break;
                }
                default:
                    break;
            }
        }
        return true;
    }

    //获取截图
    public Bitmap getBitmap() {
        btm_photo = BitmapScale(btm_old, bitmap_s);
        myMove(0, 0);
        btm_cut = Bitmap.createBitmap(btm_photo, l - bitmap_l, t - bitmap_t, 2 * R, 2 * R, null, true);
        return btm_cut;
    }

    public void changeRadius(int radius) {
        R = radius;
        invalidate();
    }

    Paint mPaint = new Paint();
    Paint mPaint1 = new Paint();
    Paint mPaint2 = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        if (first_draw) {
            mPaint2.setColor(Color.GREEN);
            mPaint2.setStyle(Style.STROKE);
            mPaint2.setStrokeWidth(1);//hujiawei
            mPaint2.setAlpha(255);
        }
        //canvas.drawRect(-1000, -1000, displayWidth+1000, b+1000, mPaint2);

        //canvas.drawBitmap(btm_photo, bitmap_l ,bitmap_t, null);
        canvas.drawBitmap(btm_old, null, new Rect(bitmap_l, bitmap_t,
                (int) (bitmap_l + bitmapWidth * bitmap_s),
                (int) (bitmap_t + bitmapHeight * bitmap_s)), null);

        if (first_draw) {
            cutViewWidth = this.getWidth();
            cutViewHeight = this.getHeight();

            mPaint.setStyle(Style.STROKE);  //绘制空心圆或 空心矩形
            mPaint.setStrokeMiter(6);
            mPaint.setStrokeWidth(2);//hujiawei
            //int color = Color.argb(255, 214, 97, 94);
            int color = Color.WHITE;//hujiawei
            mPaint.setColor(color);
            mPaint.setAntiAlias(true);//消除锯齿

            mPaint1.setColor(Color.BLACK);
            mPaint1.setAlpha(150);
            mPaint1.setAntiAlias(true);//消除锯齿

            myMove(0, 0);
            invalidate();

            first_draw = false;
        }
        //canvas.drawCircle((Tx00+Tx10)/2,(Ty00+Ty10)/2,R/5, mPaint);

        l = displayWidth / 2 - R;
        t = cutViewHeight / 2 - R;
        r = displayWidth / 2 + R;
        b = cutViewHeight / 2 + R;

        //canvas.drawRect(l, t, r, b, mPaint);//hujiawei 取消绘制矩形

        /*
        mPaint1.setStyle(Style.FILL);
        mPaint1.setStrokeWidth(1);

        canvas.drawRect(0, 0, displayWidth, t, mPaint1);
        canvas.drawRect(0, b, displayWidth, 800, mPaint1);
        canvas.drawRect(0, t, l, b, mPaint1);
        canvas.drawRect(r, t, displayWidth, b, mPaint1);
        */

        mPaint1.setStyle(Style.STROKE);
        mPaint1.setStrokeWidth(800);
        //canvas.drawCircle(displayWidth / 2, cutViewHeight / 2, R + 400, mPaint1);//hujiawei 取消绘制

        canvas.drawCircle(displayWidth / 2, cutViewHeight / 2, R, mPaint);

        super.onDraw(canvas);
    }

    private Bitmap BitmapScale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }


    //偏移方法
    private void myMove(int move_x, int move_y) {

        int bitmapWidth2 = (int) (bitmapWidth * bitmap_s);
        int bitmapHeight2 = (int) (bitmapHeight * bitmap_s);

        if (bitmap_l + move_x > displayWidth / 2 - R) {
            bitmap_l = displayWidth / 2 - R;
        } else if (bitmap_l + bitmapWidth2 + move_x > displayWidth / 2 + R) {
            bitmap_l = bitmap_l + move_x;
        } else if (bitmap_l + bitmapWidth2 + move_x < displayWidth / 2 + R) {
            bitmap_l = displayWidth / 2 + R - bitmapWidth2;
        } else if (bitmap_l + move_x < displayWidth / 2 - R) {
            bitmap_l = bitmap_l + move_x;
        }

        if (bitmap_t + move_y > cutViewHeight / 2 - R) {
            bitmap_t = cutViewHeight / 2 - R;
        } else if (bitmap_t + bitmapHeight2 + move_y > cutViewHeight / 2 + R) {
            bitmap_t = bitmap_t + move_y;
        } else if (bitmap_t + bitmapHeight2 + move_y < cutViewHeight / 2 + R) {
            bitmap_t = cutViewHeight / 2 + R - bitmapHeight2;
        } else if (bitmap_t + move_y < cutViewHeight / 2 - R) {
            bitmap_t = bitmap_t + move_y;
        }

        //Toast.makeText(context, "moved "+bitmap_l+" "+bitmap_t, 0).show();
    }


    //缩放方法
    private void myZoom(double scale, double x_c, double y_c) {
        if (bitmapWidth * bitmap_s * scale < 5 * displayWidth &&
                bitmapHeight * bitmap_s * scale < 5 * displayHeight) {
            if (bitmapWidth * bitmap_s * scale > 2 * R && bitmapHeight * bitmap_s * scale > 2 * R) {
                bitmap_l = (int) (scale * bitmap_l + (1 - scale) * x_c);
                bitmap_t = (int) (scale * bitmap_t + (1 - scale) * y_c);
                bitmap_s = (float) (bitmap_s * scale);
                myMove(0, 0);
                //bitmapWidth = btm_photo.getWidth();
                //bitmapHeight = btm_photo.getHeight();
                //bitmapWidth = (int) (bitmapWidth*scale);
                //bitmapHeight = (int) (bitmapHeight*scale);
                //Toast.makeText(context, "scaled!"+bitmap_l+" "+bitmap_t+" "+bitmap_s, 0).show();
            } else {
                //Toast.makeText(context, "已达最小", 0).show();
                //ToastUtil.showShortToast(context, "已达最小");
            }
        } else {
            //Toast.makeText(context, "已达最大", 0).show();
            //ToastUtil.showShortToast(context, "已达最大");
        }
    }

}
