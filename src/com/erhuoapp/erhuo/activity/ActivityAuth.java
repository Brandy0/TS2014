package com.erhuoapp.erhuo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityHttpResponse;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameWaiting;
import com.luminous.pick.Action;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;

/**
 * 用户认证的界面
 *
 * @author hujiawei
 * @datetime 2015/1/28
 */
public class ActivityAuth extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "ActivityAuth";

    private Context context;

    private FrameWaiting frameWaiting;
    private Button buttonScan;
    private Button buttonUpload;
    private TextView textViewSkip;
    private RelativeLayout layoutSkip;
    private FrameLayout layoutChoose;
    private ImageView imageViewPhoto;
    private RelativeLayout layoutPhoto;

    private File currentPhotoFile;
    private String currentPhotoUri;
    private File uploadFile = null;

    //private boolean isCamera = true;
    //private String currentPhotoPath;//暂时留着，没有使用 -> 删除了

    private DisplayImageOptions displayImageOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        context = this;
        View contentView = getLayoutInflater().inflate(R.layout.activity_auth, null);
        setContentView(contentView);

        // 界面组件 默认进入的时候显示提示信息，点击跳过显示跳过的警示界面，点击扫描进行扫描界面
        frameWaiting = new FrameWaiting(contentView);
        layoutSkip = (RelativeLayout) contentView.findViewById(R.id.rl_card_skip);
        textViewSkip = (TextView) contentView.findViewById(R.id.tv_card_skip);
        buttonScan = (Button) contentView.findViewById(R.id.btn_card_scan);
        buttonUpload = (Button) contentView.findViewById(R.id.btn_card_upload);

        layoutChoose = (FrameLayout) contentView.findViewById(R.id.fl_card_photo);
        layoutPhoto = (RelativeLayout) contentView.findViewById(R.id.rl_card_photo);
        imageViewPhoto = (ImageView) contentView.findViewById(R.id.iv_card_result);

        // 事件监听
        buttonScan.setOnClickListener(this);
        textViewSkip.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        contentView.findViewById(R.id.tv_card_confirm).setOnClickListener(this);
        contentView.findViewById(R.id.btn_card_edit).setOnClickListener(this);
        contentView.findViewById(R.id.ib_photo_close).setOnClickListener(this);
        contentView.findViewById(R.id.iv_photo_camera).setOnClickListener(this);
        contentView.findViewById(R.id.iv_photo_gallery).setOnClickListener(this);
        contentView.findViewById(R.id.ib_photo_return).setOnClickListener(this);

        // 初始化界面 -> 总共有5层，要考虑周全
        frameWaiting.hideFrame();
        layoutSkip.setVisibility(View.INVISIBLE);
        layoutChoose.setVisibility(View.INVISIBLE);
        layoutPhoto.setVisibility(View.INVISIBLE);

        displayImageOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();
        //.imageScaleType(ImageScaleType.EXACTLY_STRETCHED) //这个设置很重要，能够保证读取的图片扩展到整个屏幕宽度
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "");
        MobclickAgent.onResume(this);//友盟统计onResume
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Log.e(TAG, "");
    	MobclickAgent.onPause(this);//友盟统计onPause
    }

    /**
     * 解决因为有些手机在拍照的时候会发生横竖屏变换，导致当前的activity重新创建的问题
     * http://blog.sina.com.cn/s/blog_783ede0301014og5.html
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 其实这里什么都不要做
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_card_skip://跳过
                buttonScan.setEnabled(false);//防止这个时候点击了扫描按钮
                layoutSkip.setVisibility(View.VISIBLE);
                textViewSkip.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_card_confirm://确定跳过
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btn_card_edit://乖乖填写
                buttonScan.setEnabled(true);
                layoutSkip.setVisibility(View.INVISIBLE);
                textViewSkip.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_card_scan://扫描学生证
                showChoose();
                break;
            case R.id.ib_photo_close://关闭选择操作界面
                hideChoose();
                break;
            case R.id.iv_photo_camera://选择了相机
                hideChoose();
                chooseCamera();
                break;
            case R.id.iv_photo_gallery://选择了相册
                hideChoose();
                chooseGallery();
                break;
            case R.id.ib_photo_return://关闭图片结果
                layoutPhoto.setVisibility(View.GONE);
                break;
            case R.id.btn_card_upload://上传
                doUpload();
                break;
        }
    }

    // 选择相册
    private void chooseGallery() {
        //new codes 采用新的自定义的从图库中选择图片
        Intent intent = new Intent(Action.ACTION_PICK);
        startActivityForResult(intent, IConstants.REQUEST_PICK_PICTURE);

        //old codes 调用系统的图库
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("image/*");//返回结果 uri --> content:// /external/images/media/181
        //startActivityForResult(intent, IConstants.REQUEST_PICK_PICTURE);
    }

    // 选择相机 http://developer.android.com/training/camera/photobasics.html
    private void chooseCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //上面判断一下，不然如果没有app能够处理这个intent的话，app会crash http://developer.android.com/training/camera/photobasics.html
            //startActivityForResult(takePictureIntent, IConstants.REQUEST_TAKE_PICTURE);
            currentPhotoFile = null;
            try {
                currentPhotoFile = AppUtil.createImageFile();
                currentPhotoUri = "file://" + currentPhotoFile.getAbsolutePath();//这种形式的地址UIL能够加载得到
                //Log.e(TAG, "file path: " + currentPhotoPath);
                //file:/mnt/sdcard/Pictures/ErHuo/JPEG_20150128_210727_-1010846197.jpg
            } catch (IOException ex) {// Error occurred while creating the File
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (currentPhotoFile != null) {//使用这种方式进行拍照可以控制图片保存的位置
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPhotoFile));
                startActivityForResult(takePictureIntent, IConstants.REQUEST_TAKE_PICTURE);
            }
        }
    }

    // 选择相机还是相册
    private void showChoose() {
        textViewSkip.setVisibility(View.INVISIBLE);
        layoutChoose.setVisibility(View.VISIBLE);
    }

    // 关闭选择相机还是相册
    private void hideChoose() {
        textViewSkip.setVisibility(View.VISIBLE);
        layoutChoose.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.e(TAG, "onActivityResult");//有些情况下，这个方法被调用时或者说显示得到的图片时系统资源不够导致这个activity重新创建
        if (resultCode == RESULT_OK) {
            if (requestCode == IConstants.REQUEST_PICK_PICTURE) {
                //old codes
                //Log.e(TAG, intent.getData().toString());//content://media/external/images/media/78338
                //currentPhotoUri = intent.getData().toString();

                //new codes
                String single_path = intent.getStringExtra("single_path");
                currentPhotoUri = "file://" + single_path;//file://
                showCameraImage();
            } else if (requestCode == IConstants.REQUEST_TAKE_PICTURE) {
                //i9000出错，note没有问题 <- 这里是指如果按照TinyWeibo中的代码进行的话 intent.getData() 会报空指针
                //Log.e(TAG, intent.getData().toString());//content://media/external/images/media/36271 <- 这是直接调用不传递保存的图片文件路径
                //Log.e(TAG, intent != null ? intent.getData().toString() : "data is null");//true
                //Log.e(TAG, "intent is null? " + (intent == null));//true
                Log.e(TAG, currentPhotoUri);//file://
                showCameraImage();//intent为空
            }
        }
    }

    // 显示图片
    private void showCameraImage() {//
        //下面注释的代码来自http://developer.android.com/training/camera/photobasics.html
        //这些代码是用来在startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);时获取得到的图片的缩略图
        //Bundle extras = data.getExtras();
        //Bitmap imageBitmap = (Bitmap) extras.get("data");
        //imageViewPhoto.setImageBitmap(imageBitmap);

        //现在版本的拍照是在操作前已经给定了保存的图片文件路径，拍照成功之后图片就保存在那个路径中
        layoutPhoto.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(currentPhotoUri, imageViewPhoto, displayImageOptions);
    }

    // 确认上传
    private void doUpload() {
        if (null == currentPhotoUri) {
            return;
        }
        //区别于往常，这里还需要对图片进行压缩才能进行下一步的操作
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                frameWaiting.showMessage("上传图片...");
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    uploadFile = AppUtil.createImageFile();//创建临时保存图片的文件
                    // file://(xxxx)
                    //Log.e(TAG, "image path = " + currentPhotoUri.substring(6));
                    //Log.e(TAG, "save path = " + uploadFile.getAbsolutePath());
                    AppUtil.saveImage(currentPhotoUri.substring(6), uploadFile);//真正保存图片，这里会对图片进行压缩，得到的图片还是在currentPhotoPath
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (uploadFile != null) {
                    new CloudUtil().UploadCard(uploadFile, new UploadCardCallback());
                } else {
                    frameWaiting.hideFrame();
                    ToastUtil.showShortToast(context, "图片保存失败");
                }
            }
        }.execute();
    }

    // 上传学生证的回调
    class UploadCardCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {
        @Override
        public void onPost() {
            frameWaiting.showMessage("上传图片...");
        }

        @Override
        public void onPostOk(EntityHttpResponse temp) {
            //frameWaiting.hideFrame();//用不着消失
            ToastUtil.showShortToast(context, "上传成功");
            finish();//上传成功就可以结束了
        }

        @Override
        public void onPostFailure(String err) {//no_file
            frameWaiting.hideFrame();
            if (err.equalsIgnoreCase("file_larger_than_1Mb")) {
                ToastUtil.showShortToast(context, "上传图片太大");
            } else {//no_file
                ToastUtil.showShortToast(context, "上传失败，请重试");
            }
            Log.e(TAG, err);
        }
    }

    ///////////////// 暂时没有使用下面的一些方法  /////////////////

    // 选择相机 http://stackoverflow.com/questions/15438085/set-camera-size-parameters-vs-intent?answertab=votes#tab-top
    // 通过设置一些参数，使得在拍照结束之后可以对图片进行裁剪，不调用这个是因为系统自带的裁剪工具有点丑陋
//    private void chooseCameraWithCrop() {
//        currentPhotoFile = null;
//        try {
//            currentPhotoFile = createImageFile();
//            //currentPhotoUri = "file://" + currentPhotoFile.getAbsolutePath();//这种形式的地址UIL能够加载得到
//            //Log.e(TAG, "file path: " + currentPhotoPath);
//            //file:/mnt/sdcard/Pictures/ErHuo/JPEG_20150128_210727_-1010846197.jpg
//        } catch (IOException ex) {// Error occurred while creating the File
//            Log.e(TAG, ex.getMessage());
//        }
//        //Intent takePictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        //takePictureIntent.setType("image/*");
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        takePictureIntent.putExtra("crop", "true");
//        takePictureIntent.putExtra("outputX", 640);
//        takePictureIntent.putExtra("outputY", 480);
//        takePictureIntent.putExtra("aspectX", 1);
//        takePictureIntent.putExtra("aspectY", 1);
//        takePictureIntent.putExtra("scale", true);
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPhotoFile));
//        takePictureIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        startActivityForResult(takePictureIntent, IConstants.REQUEST_TAKE_PICTURE);
//    }

    /////// 缩放图片 这个没有使用 ////////

    /**
     * 缩放图片 (这个并不会缩放原始图片文件)
     * http://developer.android.com/training/camera/photobasics.html
     */
//    private void scaleImage() {
//        // Get the dimensions of the View
//        int targetW = imageViewPhoto.getWidth();
//        int targetH = imageViewPhoto.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
//        imageViewPhoto.setImageBitmap(bitmap);
//    }

}
