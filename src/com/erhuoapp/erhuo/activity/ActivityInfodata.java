package com.erhuoapp.erhuo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.CutView;
import com.erhuoapp.erhuo.view.FrameWaiting;
import com.luminous.pick.Action;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * 个人信息编辑界面
 * <p/>
 * NOTE: 2015/2/3 众多和相机有关的代码来源ActivityAuth类中
 * <p/>
 * TODO：摄像头拍摄之后用decodeFile的方式显示图像的话会出现旋转90度的问题
 *
 * @author hujiawei
 * @datetime 2015/1/22
 */
public class ActivityInfodata extends FragmentActivity implements View.OnClickListener {

    private final String TAG = "ActivityInfodata";

    private Context context;
    private EntityUserInfo userInfo;
    private DisplayImageOptions displayImageOptions;
    private DisplayImageOptions displayImageOptions_header;

    private FrameWaiting frameWaiting;
    private ImageView imageViewHeader;
    private ImageView imageViewAuthyes;
    private FrameLayout frameLayoutAuthno;
    private LinearLayout linearLayoutAuthinfo;
    private LinearLayout linearLayoutPhonebind;
    private EditText editTextNickname;
    private EditText editTextAddress;
    private TextView textViewSchool;
    private TextView textViewGrade;
    private TextView textViewPhone;
    private TextView textViewFemale;
    private TextView textViewMale;
    private TextView textViewTime;
    private TextView textViewPhoneBind;
    private TextView textViewAuthState;
    private TextView textViewSave;

    private FrameLayout layoutChoose;
    private RelativeLayout layoutPhoto;
    //private ImageView imageViewPhoto;
    private LinearLayout layoutHeader;

    private String usersex;
    private String nickname;
    private String address;
    private String phone;
    private boolean isHeaderChanged = false;
    private HashMap<String, String> params;

    private File currentPhotoFile;//选择拍照才有这个参数
    private String currentPhotoPath;//选择拍照和相册都有
    private String currentPhotoUri;//选择拍照和相册都有
    private File headerFile = null;//编辑之后保存的头像文件

    private CutView cutViewHeader;
    private Bitmap bitmapPhoto;
    private Bitmap bitmapHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        View contentView = getLayoutInflater().inflate(R.layout.activity_infodata, null);
        setContentView(contentView);

        // 界面组件
        frameWaiting = new FrameWaiting(contentView);
        imageViewHeader = (ImageView) findViewById(R.id.iv_infodata_header);
        imageViewAuthyes = (ImageView) findViewById(R.id.iv_infodata_authyes);
        frameLayoutAuthno = (FrameLayout) findViewById(R.id.fl_infodata_auth);
        linearLayoutAuthinfo = (LinearLayout) findViewById(R.id.ll_infodata_authinfo);
        linearLayoutPhonebind = (LinearLayout) findViewById(R.id.ll_infodata_phonebind);
        editTextNickname = (EditText) findViewById(R.id.et_infodata_nickname);
        editTextAddress = (EditText) findViewById(R.id.et_infodata_address);
        textViewSchool = (TextView) findViewById(R.id.tv_infodata_school);
        textViewGrade = (TextView) findViewById(R.id.tv_infodata_grade);
        textViewPhone = (TextView) findViewById(R.id.tv_infodata_phone);
        textViewFemale = (TextView) findViewById(R.id.tv_infodata_female);
        textViewMale = (TextView) findViewById(R.id.tv_infodata_male);
        textViewTime = (TextView) findViewById(R.id.tv_infodata_time);
        textViewPhoneBind = (TextView) findViewById(R.id.tv_infodata_bind);
        textViewAuthState = (TextView) findViewById(R.id.tv_infodata_auth);
        textViewSave = (TextView) findViewById(R.id.tv_infodata_ok);

        layoutChoose = (FrameLayout) contentView.findViewById(R.id.fl_card_photo);
        layoutPhoto = (RelativeLayout) contentView.findViewById(R.id.rl_header_photo);
        //imageViewPhoto = (ImageView) contentView.findViewById(R.id.iv_card_result);
        layoutHeader = (LinearLayout) contentView.findViewById(R.id.ll_photo_result);

        // 事件监听
        findViewById(R.id.ib_infodata_return).setOnClickListener(this);
        findViewById(R.id.tv_infodata_ok).setOnClickListener(this);
        findViewById(R.id.tv_infodata_male).setOnClickListener(this);
        findViewById(R.id.tv_infodata_female).setOnClickListener(this);
        findViewById(R.id.iv_infodata_header).setOnClickListener(this);
        findViewById(R.id.ib_photo_close).setOnClickListener(this);
        findViewById(R.id.iv_photo_camera).setOnClickListener(this);
        findViewById(R.id.iv_photo_gallery).setOnClickListener(this);
        findViewById(R.id.ib_photo_return).setOnClickListener(this);
        findViewById(R.id.btn_header_ok).setOnClickListener(this);

        // 头像 image loader
        displayImageOptions_header = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.header_default)
                .showImageOnFail(R.drawable.header_default)
                .showImageOnLoading(R.drawable.header_default)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(100, 3)).build();
        displayImageOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();


        // 初始化数据 不通过前面传递过来，因为可能用户点击很快导致前面并没有获取到数据而为空
        //userInfo = (EntityUserInfo) getIntent().getSerializableExtra("user");
        userInfo = AppUtil.getInstance().getBasicUserInfo();//从存储的数据中提取
        showBasicUserInfo(userInfo);

        params = new HashMap<String, String>();
        frameWaiting.hideFrame();
        layoutChoose.setVisibility(View.INVISIBLE);
        layoutPhoto.setVisibility(View.INVISIBLE);

        // 获取用户信息，这个不能方式onResume方法中，否则用户的修改容易被重置
        new CloudUtil().UserInfo(new UserInfoCallback());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        MobclickAgent.onResume(this);//友盟统计onResume
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Log.e(TAG, "onPause");
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
            case R.id.ib_infodata_return://返回
                finish();
                break;
            case R.id.tv_infodata_ok://保存
                updateUserInfo();
                break;
            case R.id.tv_infodata_male://男性
                usersex = "1";
                textViewMale.setEnabled(false);
                textViewFemale.setEnabled(true);
                break;
            case R.id.tv_infodata_female://女性
                usersex = "0";
                textViewFemale.setEnabled(false);
                textViewMale.setEnabled(true);
                break;
            case R.id.iv_infodata_header://更换头像
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
            case R.id.btn_header_ok://确定
                saveUserHeader();
                break;
        }
    }

    // 确定头像 - 保存用户编辑得到的头像
    private void saveUserHeader() {
        bitmapHeader = cutViewHeader.getBitmap();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                frameWaiting.showMessage("保存头像...");
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    headerFile = AppUtil.createImageFile();//创建临时保存图片的文件
                    //AppUtil.saveHeader(bitmapHeader, headerFile);//真正保存图片，这里会对图片进行压缩，得到的图片还是在currentPhotoPath
                    bitmapHeader.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(headerFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                frameWaiting.hideFrame();
                if (headerFile == null) {
                    ToastUtil.showShortToast(context, "头像保存失败");
                } else {//头像保存成功
                    isHeaderChanged = true;
                    layoutPhoto.setVisibility(View.INVISIBLE);
                    String headerUri = "file://" + headerFile.getAbsolutePath();
                    ImageLoader.getInstance().displayImage(headerUri, imageViewHeader, displayImageOptions_header);
                }
            }
        }.execute();
    }

    // 保存用户的修改
    private void updateUserInfo() {
        if (checkUserInput()) {
            nickname = editTextNickname.getText().toString();
            address = editTextAddress.getText().toString();
            params.put("sex", usersex);
            params.put("nickname", nickname);
            params.put("address", address);
            if (isHeaderChanged) {
                params.put("headerupdate", "1");
            } else {
                params.put("headerupdate", "0");
            }
            new CloudUtil().UpdateUserInfo(params, headerFile, new UpdateUserInfoCallback());
        }
    }

    // 检查用户的输入
    private boolean checkUserInput() {
        nickname = editTextNickname.getText().toString();
        if (null == nickname || "".equalsIgnoreCase(nickname)) {
            ToastUtil.showShortToast(this, "请输入用户昵称");
            return false;
        }
        //if (!nickname.matches("[a-zA-Z0-9]+") || nickname.length() < 4 || nickname.length() > 15) {
        if (nickname.length() < 4 || nickname.length() > 15) {
            ToastUtil.showShortToast(this, "用户昵称必须为4-15位字符");
            return false;
        }
        return true;
    }

    // 更新界面显示的信息
    private void showBasicUserInfo(EntityUserInfo userInfo) {
        nickname = userInfo.getNickName();
        if (nickname != null && !"".equalsIgnoreCase(nickname)) {
            editTextNickname.setText(nickname);
        }
        address = userInfo.getAddress();
        if (address != null && !"".equalsIgnoreCase(address)) {
            editTextAddress.setText(address);
        }
        // 认证之后才有的数据
        String school = userInfo.getSchool();
        if (school != null && !"".equalsIgnoreCase(school)) {
            textViewSchool.setText(school);
        }
        String grade = userInfo.getGrade();
        if (grade != null && !"".equalsIgnoreCase(grade)) {
            textViewGrade.setText(grade);
        }
        phone = userInfo.getPhone();
        if (phone != null && !"".equalsIgnoreCase(phone) && !"0".equalsIgnoreCase(phone)) {
            changePhoneUI();
        } else {
            bindPhoneUI();
        }

        String header = userInfo.getHeader();
        if (!header.equalsIgnoreCase("")) {//否则显示默认的头像
            ImageLoader.getInstance().displayImage(header, imageViewHeader, displayImageOptions_header);
        }

        if (userInfo.getIsAuth()) {//认证了 auth==1
            frameLayoutAuthno.setVisibility(View.INVISIBLE);
            imageViewAuthyes.setVisibility(View.VISIBLE);
            linearLayoutAuthinfo.setVisibility(View.VISIBLE);//学校院系等信息
        } else {//未认证
            imageViewAuthyes.setVisibility(View.INVISIBLE);
            linearLayoutAuthinfo.setVisibility(View.GONE);
            frameLayoutAuthno.setVisibility(View.VISIBLE);
            if (userInfo.getAuth() == 0) {
                textViewAuthState.setText("立即认证");
                textViewAuthState.setTextColor(getResources().getColor(R.color.font_red_lightred));
                frameLayoutAuthno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //立即认证
                        Intent intent = new Intent(ActivityInfodata.this, ActivityAuth.class);
                        startActivityForResult(intent, IConstants.REQUEST_USER_AUTH);
                    }
                });
            } else {//auth==2
                textViewAuthState.setText("审核中");
                textViewAuthState.setTextColor(getResources().getColor(R.color.main_gray));//灰色，不可点击
//                frameLayoutAuthno.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) { //审核中
//                        Intent intent = new Intent(ActivityInfodata.this, ActivityAuth.class);
//                        startActivityForResult(intent, IConstants.REQUEST_USER_AUTH);
//                    }
//                });//审核中不可以再认证 [暂时测试时开放]
            }
        }

        usersex = userInfo.getSex();
        if (usersex.equalsIgnoreCase("1")) {//默认为女，该性别的TextView设置为disable
            textViewFemale.setEnabled(true);
            textViewMale.setEnabled(false);
        } else {
            textViewFemale.setEnabled(false);
            textViewMale.setEnabled(true);
        }

        if (!userInfo.getRegisterTime().equalsIgnoreCase("")) {
            textViewTime.setText(userInfo.getName() + "\n" + userInfo.getRegisterTime() + " 加入贰货");
            textViewTime.setOnClickListener(new View.OnClickListener() {//TODO 这是一个隐藏的测试入口
                @Override
                public void onClick(View v) {//删除该用户
                    ToastUtil.showShortToast(context, "删除用户");
                    new CloudUtil().DeleteUser();
                }
            });
        }
    }

    // 提示绑定手机号时的界面
    private void bindPhoneUI() {
        textViewPhone.setText("");
        textViewPhoneBind.setText("绑定手机号");
        linearLayoutPhonebind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityPhone.class);
                intent.putExtra("type", IConstants.PHONE_ADD);
                startActivityForResult(intent, IConstants.REQUEST_PHONE);
            }
        });
    }

    // 提示更改手机号时的界面
    private void changePhoneUI() {
        textViewPhone.setText(phone);
        textViewPhoneBind.setText("更改手机号");
        linearLayoutPhonebind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//
                Intent intent = new Intent(context, ActivityPhone.class);
                intent.putExtra("type", IConstants.PHONE_CHANGE);
                startActivityForResult(intent, IConstants.REQUEST_PHONE);
            }
        });
    }

    ////////////  下面是和头像有关的部分 ////////////

    // 选择相册 最多只能取一张图片
    private void chooseGallery() {
        Intent intent = new Intent(Action.ACTION_PICK);
        startActivityForResult(intent, IConstants.REQUEST_PICK_PICTURE);
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
                currentPhotoPath = currentPhotoFile.getAbsolutePath();
                currentPhotoUri = "file://" + currentPhotoFile.getAbsolutePath();//这种形式的地址UIL能够加载得到
                //Log.e(TAG, "take picture: " + currentPhotoFile.getAbsolutePath());
                // i9000: /storage/sdcard0/Pictures/ErHuo/JPEG_20050412_190718_-481455869.jpg
                // note:  /mnt/sdcard/Pictures/ErHuo/JPEG_20150128_210727_-1010846197.jpg
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
        textViewSave.setEnabled(false);
        layoutChoose.setVisibility(View.VISIBLE);
    }

    // 关闭选择相机还是相册
    private void hideChoose() {
        textViewSave.setEnabled(true);
        layoutChoose.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IConstants.REQUEST_PHONE && resultCode == RESULT_OK) {
            //ToastUtil.showShortToast(context, "手机号绑定成功");
            phone = data.getStringExtra("phone");//立即更改比后面加载信息完了之后刷新要快，效果更好些
            changePhoneUI();//成功之后就变成 更改手机号了
            // 手机号码会因为onresume中用户信息自动重新加载而设置上去
        }
        //每次返回肯定会调用onResume，用户信息会重新加载，所以其实没有必要处理这些成功的返回
        else if (requestCode == IConstants.REQUEST_USER_AUTH && resultCode == RESULT_OK) {
            ToastUtil.showShortToast(context, "认证信息上传成功，请等待审核结果");
        } else if (requestCode == IConstants.REQUEST_PICK_PICTURE && resultCode == RESULT_OK) {
            //new codes
            currentPhotoPath = data.getStringExtra("single_path");
            currentPhotoUri = "file://" + currentPhotoPath;//file://
            //Log.e(TAG, currentPhotoUri);
            showCameraImage();
        } else if (requestCode == IConstants.REQUEST_TAKE_PICTURE && resultCode == RESULT_OK) {
            //i9000出错，note没有问题 <- 这里是指如果按照TinyWeibo中的代码进行的话 intent.getData() 会报空指针
            //Log.e(TAG, intent.getData().toString());//content://media/external/images/media/36271 <- 这是直接调用不传递保存的图片文件路径
            //Log.e(TAG, intent != null ? intent.getData().toString() : "data is null");//true
            //Log.e(TAG, "intent is null? " + (intent == null));//true
            //Log.e(TAG, currentPhotoUri);//file://
            showCameraImage();//intent为空
        }
    }

    // 显示图片 -> 这里需要改动，变成可以编辑头像的模式
    private void showCameraImage() {//
        if (currentPhotoPath == null || "".equalsIgnoreCase(currentPhotoPath)) {
            return;//可能会出现这种情况
        }
        //现在版本的拍照是在操作前已经给定了保存的图片文件路径，拍照成功之后图片就保存在那个路径中
        //ImageLoader.getInstance().displayImage(currentPhotoUri, imageViewPhoto, displayImageOptions);
        Log.e(TAG, "currentPhotoPath: " + currentPhotoPath);
        layoutPhoto.setVisibility(View.VISIBLE);
        bitmapPhoto = BitmapFactory.decodeFile(currentPhotoPath);
        cutViewHeader = new CutView(context, bitmapPhoto);
        layoutHeader.removeAllViews();//先删掉以前的view
        layoutHeader.addView(cutViewHeader);

        //BitmapFactory.decodeFile出错：Bitmap too large to be uploaded into a texture
        //解决办法是禁止硬件加速 http://blog.csdn.net/ta893115871/article/details/9043559
        //ImageLoader﹕ Try to initialize ImageLoader which had already been initialized before. To re-init ImageLoader with new configuration call ImageLoader.destroy() at first.
    }

    /**
     * 更新用户信息的回回调
     */
    class UpdateUserInfoCallback implements CloudUtil.OnPostRequest<EntityUserInfo> {
        @Override
        public void onPost() {
            frameWaiting.showMessage("保存中，请等待...");
        }

        @Override
        public void onPostOk(EntityUserInfo temp) {
            //frameWaiting.hideFrame();
            ToastUtil.showShortToast(ActivityInfodata.this, "保存成功");
            finish();
        }

        @Override
        public void onPostFailure(String err) {
            Log.e(TAG, "err = " + err);
            frameWaiting.hideFrame();
            ToastUtil.showShortToast(ActivityInfodata.this, "保存失败");
        }
    }

    /**
     * 获取用户信息的回调
     */
    class UserInfoCallback implements CloudUtil.OnPostRequest<EntityUserInfo> {

        @Override
        public void onPost() {

        }

        @Override
        public void onPostOk(final EntityUserInfo temp) {
            userInfo = temp;
            showBasicUserInfo(temp);
            AppUtil.getInstance().saveUserInfo(temp);
        }

        @Override
        public void onPostFailure(String err) {
            ToastUtil.showShortToast(ActivityInfodata.this, "加载用户信息失败");
            Log.e(TAG, err);
        }
    }

}
