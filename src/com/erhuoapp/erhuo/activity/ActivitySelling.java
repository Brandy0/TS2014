package com.erhuoapp.erhuo.activity;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.adapter.AdapterSellingClassify;
import com.erhuoapp.erhuo.adapter.AdapterSellingGallery;
import com.erhuoapp.erhuo.model.EntityGoodsClassify;
import com.erhuoapp.erhuo.model.EntityGoodsSelling;
import com.erhuoapp.erhuo.model.EntityHttpResponse;
import com.erhuoapp.erhuo.model.EntityImage;
import com.erhuoapp.erhuo.model.EntitySellingImage;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ErUse;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameWaiting;
import com.erhuoapp.erhuo.view.NoScrollGridView;
import com.luminous.pick.Action;
import com.luminous.pick.CustomGalleryActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 我要卖界面
 * <p/>
 * NOTE: 该类中关于相册和相机方面的代码可以参见ActivityAuth中的详细注释
 * NOTE: 2015/2/3 众多和相机有关的代码来源ActivityAuth类中
 * TODO: 1.有些手机拍照得到的图片要旋转90度 2.编辑时异步加载图片的话顺序可能和原来的不一样了
 *
 * @author hujiawei
 * @datetime 2015/1/27
 */
public class ActivitySelling extends FragmentActivity implements View.OnClickListener {

    private final String TAG = "ActivitySelling";

    private Context context;
    private EntityGoodsSelling goods;

    private FrameWaiting frameWaiting;
    private EditText editTextTitle;
    private EditText editTextPrice;
    private EditText editTextContent;
    private TextView textViewPublish;
    private TextView textViewCategory;
    private TextView textViewTitle;//标题部分
    private NoScrollGridView gridView;//分类
    private NoScrollGridView gridViewImages;
    private CheckBox checkBoxNew;
    private CheckBox checkBoxBargin;
    private CheckBox checkBoxLocation;
    private LinearLayout linearLayoutLocation;
    private ViewSwitcher viewSwitcher;
    private FrameLayout layoutChoose;

    private String title;
    private String price;
    private String content;
    private HashMap<String, String> params;
    private HashMap<String, String> goodsInfoParams;//如果是编辑的话，请求商品信息的参数

    private int currentImageCount = 0;//时刻监控当前有多少张图片
    private int MAX_IMAGE_COUNT = 8;
    private File uploadFile = null;
    private List<File> uploadFiles = new ArrayList<File>();
    private File currentPhotoFile = null;
    private String currentPhotoUri = null;

    //private ImageLoader imageLoader;
    private boolean isImageUpdate = false;
    private AdapterSellingGallery adapter;
    private ArrayList<EntitySellingImage> images;//商品的图片

    private boolean isShowClassify = false;
    private int currentClassifyPositon = -1;
    private ArrayList<EntityGoodsClassify> listClassify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        context = this;
        View contentView = getLayoutInflater().inflate(R.layout.activity_selling, null);
        setContentView(contentView);

        // 界面组件
        frameWaiting = new FrameWaiting(contentView);
        editTextTitle = (EditText) findViewById(R.id.et_selling_title);
        editTextPrice = (EditText) findViewById(R.id.et_selling_price);
        editTextContent = (EditText) findViewById(R.id.et_selling_content);
        textViewPublish = (TextView) findViewById(R.id.tv_selling_publish);
        textViewTitle = (TextView) findViewById(R.id.tv_selling_title);
        textViewCategory = (TextView) findViewById(R.id.tv_selling_category);
        gridView = (NoScrollGridView) findViewById(R.id.gridview_classify);
        gridViewImages = (NoScrollGridView) findViewById(R.id.gridview_images);
        checkBoxNew = (CheckBox) findViewById(R.id.cb_selling_new);
        checkBoxBargin = (CheckBox) findViewById(R.id.cb_selling_bargin);
        checkBoxLocation = (CheckBox) findViewById(R.id.cb_selling_location);
        linearLayoutLocation = (LinearLayout) findViewById(R.id.ll_selling_location);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.vs_selling_images);
        layoutChoose = (FrameLayout) contentView.findViewById(R.id.fl_card_photo);

        // 事件监听
        findViewById(R.id.ib_selling_return).setOnClickListener(this);
        textViewPublish.setOnClickListener(this);
        textViewCategory.setOnClickListener(this);
        contentView.findViewById(R.id.ib_photo_close).setOnClickListener(this);
        contentView.findViewById(R.id.iv_photo_camera).setOnClickListener(this);
        contentView.findViewById(R.id.iv_photo_gallery).setOnClickListener(this);
        contentView.findViewById(R.id.iv_selling_stub).setOnClickListener(this);

        // 如果有数据的话表示是编辑模式
        goods = (EntityGoodsSelling) getIntent().getSerializableExtra("goods");
        params = new HashMap<String, String>();
        goodsInfoParams = new HashMap<String, String>();

        //imageLoader = ImageLoader.getInstance();//使用的都是同一个ImageLoader
        //adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        images = new ArrayList<EntitySellingImage>();
        adapter = new AdapterSellingGallery(context, 0, images);
        adapter.setListener(new AdapterSellingGallery.ItemClickedListener() {
            @Override
            public void OnStubClicked() {//空图片，让用户可以继续增加图片
                showChoose();
            }

            @Override
            public void onItemClicked(EntitySellingImage obj, int position) {
                //点击某张图片之后可以浏览当前商品的所有图片列表
            }

            @Override
            public void onItemRemoved(EntitySellingImage obj, int position) {//删除一张图片
                deleteImage(obj, position);
            }
        });
        gridViewImages.setAdapter(adapter);
        viewSwitcher.setDisplayedChild(1);//默认显示空操作图片

        // 进入时关闭它
        frameWaiting.hideFrame();
        layoutChoose.setVisibility(View.INVISIBLE);
        linearLayoutLocation.setVisibility(View.GONE);

        initData();
        
        //显示输入法
        ErUse.toggleInputMethod(editTextTitle,200);
    }

    private void initData() {
        // 加载获取分类列表
        new CloudUtil().GoodsClassify(new GoodsClassifyCallback());//进来的时候加载一次就行
        // 加载商品图片信息
        if (null != goods) {//编辑模式，加载获取商品信息 -> 这个不能放在onResume中，否则用户对图片的修改会被恢复
            editTextTitle.setText(goods.getTitle());
            editTextContent.setText(goods.getContent());
            editTextPrice.setText(goods.getPrice());
            if (goods.isNew()) {
                checkBoxNew.setChecked(true);
            } else {
                checkBoxNew.setChecked(false);
            }
            if (goods.isBargin()) {
                checkBoxBargin.setChecked(true);
            } else {
                checkBoxBargin.setChecked(false);
            }

            textViewTitle.setText("编辑贰货");
            textViewPublish.setText("完成");
            linearLayoutLocation.setVisibility(View.VISIBLE);//编辑模式下显示

            goodsInfoParams.put("id", goods.getId());
            new CloudUtil().GoodsInfo(goodsInfoParams, new GoodsInfoCallback());//加载二货详情，也就是去获取它的已有的图片
        }
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

    // 添加图片
    private void addImages(List<EntitySellingImage> selectImages) {
        //isImageUpdate = true;//这里不能算，因为一开始显示原来的图片的时候这个方法就会被调用
        viewSwitcher.setDisplayedChild(0);
        if (images.size() >= 1 && images.get(images.size() - 1).isStub()) {//大于等于1的时候
            images.remove(images.size() - 1);//先删除最后一张空操作图片
        }
        //images.addAll(selectImages);//然后再添加新的图片 -> 为了处理掉重复的图片
        for (EntitySellingImage image : selectImages) {
            if (!images.contains(image)) {//将uri相同视为同一张图片
                images.add(image);
            }
        }
        currentImageCount = images.size();//currentImageCount是真实的图片总数，不包括空操作图片
        if (images.size() < MAX_IMAGE_COUNT) {//最后如果还不够最大图片数目就加上空操作图片
            EntitySellingImage image = new EntitySellingImage();
            image.setStub(true);
            images.add(image);
        }
        //adapter.addAll(selectImages);//需要高版本的Android支持
        adapter.notifyDataSetChanged();
        gridViewImages.invalidateViews();//重绘
    }

    // 删除一张图片
    private void deleteImage(EntitySellingImage obj, int position) {
        isImageUpdate = true;//这个肯定算
        images.remove(position);
        currentImageCount -= 1;
        // 如果之前最后一张图片不是空操作图片，那么之后时候就需要加上了
        if (images.size() >= 1 && !images.get(images.size() - 1).isStub()) {//大于等于1的时候
            EntitySellingImage image = new EntitySellingImage();
            image.setStub(true);
            images.add(image);
        }
        adapter.notifyDataSetChanged();
        gridViewImages.invalidateViews();
    }

    // 进入相册选择多张图片
    private void pickMultipleImages() {
        Intent intent = new Intent(Action.ACTION_MULTIPLE_PICK);
        Bundle bundle = new Bundle();
        bundle.putInt(CustomGalleryActivity.KEY_MAX_IMAGE_SELECTION_LIMIT, MAX_IMAGE_COUNT - currentImageCount);
        intent.putExtras(bundle);
        startActivityForResult(intent, IConstants.REQUEST_PICK_PICTURE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_selling_return://返回
                finish();
                break;
            case R.id.tv_selling_publish://发布
                doPublish();
                break;
            case R.id.tv_selling_category://显示分类
            	//这里要添加隐藏键盘的操作
                toggleCategory();
                break;
            case R.id.iv_selling_stub://打开选择操作界面
            	ErUse.hideInputMethod(editTextTitle, 0);
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
        }
    }

    // 选择相册
    private void chooseGallery() {
        pickMultipleImages();
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
                Log.e(TAG, "take picture: " + currentPhotoFile.getAbsolutePath());
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
        layoutChoose.setVisibility(View.VISIBLE);
    }

    // 关闭选择相机还是相册
    private void hideChoose() {
        layoutChoose.setVisibility(View.GONE);
    }

    // 显示或者关闭分类
    private void toggleCategory() {
    	
    	//隐藏输入法
    	ErUse.hideInputMethod(editTextTitle, 0);
    	
        if (isShowClassify) {
            gridView.setVisibility(View.GONE);
            isShowClassify = false;
        } else {
            gridView.setVisibility(View.VISIBLE);
            isShowClassify = true;
        }
    }

    /**
     * 处理因为系统资源有限在打开摄像头界面时当前activity被销毁而丢失数据的问题
     * http://blog.csdn.net/jingwen3699/article/details/10608545
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // 这个方法只会在系统销毁当前视图，程序再次返回时候调用
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        // 成功选择了很多张图片返回显示出来 [确保一定选择了图片，如果没有选择图片，返回RESULT_CANCEL]
        if (requestCode == IConstants.REQUEST_PICK_PICTURE && resultCode == Activity.RESULT_OK) {
            String[] all_path = data.getStringArrayExtra("all_path");
            // 修改后的将选择得到的图片放置在GridView中
            List<EntitySellingImage> selectImages = new ArrayList<EntitySellingImage>();
            for (String str : all_path) {
                EntitySellingImage image = new EntitySellingImage();
                image.setStub(false);
                image.setSdpath(str);
                image.setImageUri("file://" + str);
                selectImages.add(image);
            }
            addImages(selectImages);
            isImageUpdate = true;
        } else if (requestCode == IConstants.REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {// 拍照成功之后返回
            //Log.e(TAG, currentPhotoUri);//file://
            List<EntitySellingImage> selectImages = new ArrayList<EntitySellingImage>();
            EntitySellingImage image = new EntitySellingImage();
            image.setStub(false);
            image.setImageUri(currentPhotoUri);//
            image.setSdpath(currentPhotoFile.getAbsolutePath());//路径
            selectImages.add(image);
            addImages(selectImages);
            isImageUpdate = true;
        }
    }

    // 发布
    private void doPublish() {
        if (checkUserInput()) {
            params.put("title", title);
            params.put("describe", content);
            params.put("price", price);
            params.put("lati", "0");//TODO：这里需要获取用户的地理位置
            params.put("longi", "0");
            params.put("cid", listClassify.get(currentClassifyPositon).getId());
            params.put("new", checkBoxNew.isChecked() ? "1" : "0");
            params.put("bargin", checkBoxBargin.isChecked() ? "1" : "0");

            if (null != goods) {//更新模式下还需要添加的参数
                params.put("id", goods.getId());
                if (checkBoxLocation.isChecked()) {
                    params.put("location", "1");//1表示更新地理位置信息
                } else {
                    params.put("location", "0");
                }

                if (!isImageUpdate){//如果没有修改图片的话那么就可以直接发送请求了
                    params.put("picupdate", "0");//UpdateSelling中根据这个来决定是否要添加上filepart
                    new CloudUtil().UpdateSelling(params, uploadFiles, new UpdateSellingCallback());
                    return;
                }else{//修改了图片的话还要继续执行图片压缩
                    params.put("picupdate", "1");
                }
            }
            //区别于往常，这里还需要对图片进行压缩才能进行下一步的操作
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    frameWaiting.showMessage("上传图片中，请等待");//其实只是图片压缩保存，并不是上传，这里用上传作为提示信息好些
                }

                @Override
                protected Void doInBackground(Void... params) {
                    String sdpath = null;
                    try {//处理所有的商品图片，然后进行发布
                        for (int i = 0; i < currentImageCount; i++) {
                            uploadFile = AppUtil.createImageFile();//创建一个临时文件来保存要上传的图片
                            sdpath = images.get(i).getSdpath();
                            if (sdpath != null && !"".equalsIgnoreCase(sdpath)) {//不处理空操作图片 -> 其实这里不会处理到空操作图片
                                AppUtil.saveImage(sdpath, uploadFile);//真正保存图片，这里会对图片进行压缩，得到的图片还是在currentPhotoPath
                                uploadFiles.add(uploadFile);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (uploadFiles != null && uploadFiles.size() > 0) {
                        if (null!=goods){//编辑模式
                            new CloudUtil().UpdateSelling(params, uploadFiles, new UpdateSellingCallback());
                        }else {//发布模式
                            new CloudUtil().AddSelling(params, uploadFiles, new AddSellingCallback());
                        }
                    } else {//size<=0表示图片保存失败
                        frameWaiting.hideFrame();
                        ToastUtil.showShortToast(context, "图片上传失败");
                    }
                }
            }.execute();
        }
    }

    // 验证输入的内容
    private boolean checkUserInput() {
        //首先检查图片
        if (currentImageCount <= 0) {
            ToastUtil.showShortToast(this, "请至少上传一张贰货图片");
            return false;
        }
        title = editTextTitle.getText().toString();
        if (null == title || "".equalsIgnoreCase(title)) {
            ToastUtil.showShortToast(this, "请输入贰货标题");
            return false;
        }
        if (currentClassifyPositon == -1) {//检查分类
            ToastUtil.showShortToast(this, "请选择贰货分类");
            return false;
        }
        price = editTextPrice.getText().toString();
        if (null == price || "".equalsIgnoreCase(price)) {
            ToastUtil.showShortToast(this, "请输入贰货价格");
            return false;
        }
        if (!price.matches("[0-9]+")) {
            ToastUtil.showShortToast(this, "贰货价格必须是数字");
            return false;
        }
        content = editTextContent.getText().toString();
        if (null == content || "".equalsIgnoreCase(content)) {
            ToastUtil.showShortToast(this, "请描述下贰货物品吧");
            return false;
        }
        if (content.length() > 200){
            ToastUtil.showShortToast(this, "您的描述内容太多了");
            return false;
        }
        return true;
    }

    // 发布事件的回调
    class AddSellingCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {

        @Override
        public void onPost() {
            frameWaiting.showMessage("发布中，请等待...");
        }

        @Override
        public void onPostOk(EntityHttpResponse temp) {
            //frameWaiting.hideFrame();
            ToastUtil.showShortToast(context, "发布成功");
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onPostFailure(String err) {
            frameWaiting.hideFrame();
            ToastUtil.showShortToast(context, "发布失败，请重试");
            Log.e(TAG, err);
        }
    }

    // 更新事件的回调
    class UpdateSellingCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {

        @Override
        public void onPost() {
            //closeInputMethod();//先要关闭输入键盘
            frameWaiting.showMessage("更新中，请等待...");
        }

        @Override
        public void onPostOk(EntityHttpResponse temp) {
            //frameWaiting.hideFrame();
            ToastUtil.showShortToast(context, "更新成功");
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onPostFailure(String err) {
            frameWaiting.hideFrame();
            ToastUtil.showShortToast(context, "更新失败，请重试");
            Log.e(TAG, err);
        }
    }

    /**
     * 商品图片信息
     */
    class GoodsInfoCallback implements CloudUtil.OnPostRequest<EntityGoodsSelling> {

        @Override
        public void onPost() {
            //viewSwitcher.setDisplayedChild(1);//
        }

        @Override
        public void onPostOk(final EntityGoodsSelling goodstemp) {
            new ImageDownloader().execute(goodstemp.getImages());//异步加载原有图片
        }

        @Override
        public void onPostFailure(String err) {
            Log.e(TAG, err); //一般不会失败
            new CloudUtil().GoodsInfo(goodsInfoParams, new GoodsInfoCallback());//加载二货详情
        }
    }

    // 图片下载器
    class ImageDownloader extends AsyncTask<List<EntityImage>, Void, Void> {

        //private List<EntitySellingImage> sellingImages = new ArrayList<>();
        //不能放在doInBackground中声明为final的，否则size永远为0 -> 这个理解是错误的

        @Override
        protected void onPreExecute() {
            //sellingImages.clear();
        }

        @Override
        protected Void doInBackground(List<EntityImage>... params) {
            final List<EntityImage> goodsImages = params[0];
            //Log.e(TAG, "load images " + goodsImages.size());
            // 开始加载图片，在图片加载成功之后
            for (final EntityImage image : goodsImages) {
                // 使用imageLoader加载图片，当图片成功下载时获取它保存的路径
                ImageLoader.getInstance().loadImage(image.getImage(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        //File file = imageLoader.getDiscCache().get(image.getImage());
                        String path = ImageLoader.getInstance().getDiscCache().get(image.getImage()).getAbsolutePath();
                        //Log.e(TAG, "save image path: " + path);//  /storage/sdcard0/ErHuoCache/-363387308
                        EntitySellingImage sellingImage = new EntitySellingImage();
                        sellingImage.setStub(false);
                        sellingImage.setSdpath(path);
                        sellingImage.setImageUri("file://" + path);
                        final List<EntitySellingImage> sellingImages = new ArrayList<EntitySellingImage>();
                        sellingImages.add(sellingImage);
                        // 每次一张图片一张图片的添加 -> 这里要在UI线程执行
                        ActivitySelling.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null != sellingImages && sellingImages.size() > 0) {
                                    addImages(sellingImages);
                                }
                            }
                        });
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Log.e(TAG, "selling images size= " + sellingImages.size());//永远为0，因为图片加载是异步的，当执行到这里的时候图片还没有添加上去
            //if (null != sellingImages && sellingImages.size() > 0) {
            //  addImages(sellingImages);
            //}
        }

    }

    /**
     * 分类列表监听
     */
    class GoodsClassifyCallback implements CloudUtil.OnPostRequest<ArrayList<EntityGoodsClassify>> {

        @Override
        public void onPost() {
            gridView.setVisibility(View.GONE);
        }

        @Override
        public void onPostOk(final ArrayList<EntityGoodsClassify> list) {//分类列表
            listClassify = list;
            final AdapterSellingClassify gridClassify = new AdapterSellingClassify(context, 0, listClassify);
            gridView.setAdapter(gridClassify);//中间值为resource，给0即可
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    setPosition(position, gridClassify);
                    toggleCategory();//点击分类后自动收起
                }
            });
            // 如果是编辑模式下的话，还需要将商品原始的分类选中
            if (null != goods) {
                //Log.e(TAG, "goods cid = " + goods.getCid());
                for (int i = 0; i < listClassify.size(); i++) {
                    if (listClassify.get(i).getId().equalsIgnoreCase(goods.getCid())) {
                        setPosition(i, gridClassify);
                    }
                }
            }
        }

        private void setPosition(int position, AdapterSellingClassify gridClassify) {
            currentClassifyPositon = position;
            textViewCategory.setText(listClassify.get(position).getName());
            gridClassify.setCurrentPosition(position);
            gridView.invalidateViews();//这样做可以
        }

        @Override
        public void onPostFailure(String err) {
            Log.e(TAG, err);// 一般不会失败的
            // 如果真的失败了的话，只能重新再加载
            new CloudUtil().GoodsClassify(new GoodsClassifyCallback());
        }
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

    // 关闭输入法 [没有问题]
    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);//显示了就关闭
        }
        //imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
