package com.luminous.pick;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 自定义的从图库中选择多张图片的开源项目
 * <p/>
 * https://codeload.github.com/AizazAZ/MultiImagePick/
 */
public class CustomGalleryActivity extends Activity {

    private GridView gridGallery;
    private Handler handler;
    private GalleryAdapter adapter;

    private ImageView imgNoMedia;
    private Button btnGalleryOk;
    private TextView textCount;//hujiawei 增加的字段

    private String action;
    private ImageLoader imageLoader;
    public static final String KEY_MAX_IMAGE_SELECTION_LIMIT = "KEY_MAX_IMAGE_SELECTION";
    private int maxImageAllowedForSelection = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gallery);

        action = getIntent().getAction();
        if (action == null) {
            finish();
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            maxImageAllowedForSelection = bundle.getInt(KEY_MAX_IMAGE_SELECTION_LIMIT);
        }

        init();
    }

    // 初始化工作  // hujiawei 修改版本
    private void init() {
        handler = new Handler();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer(10)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultOptions).memoryCache(new WeakMemoryCache()).build();
        imageLoader = ImageLoader.getInstance();//imageloader 在我的应用中已经配置过了 -> 但是这里还是按照原来项目的情况配置一下吧，增加了个圆角设置
        imageLoader.init(config);
        textCount = (TextView) findViewById(R.id.tvCount);
        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, true, true);
        gridGallery.setOnScrollListener(listener);

        if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK)) {//多选
            findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
            gridGallery.setOnItemClickListener(mItemMulClickListener);
            adapter.setMultiplePick(true);
            textCount.setVisibility(View.VISIBLE);
        } else if (action.equalsIgnoreCase(Action.ACTION_PICK)) {//单选
            findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
            gridGallery.setOnItemClickListener(mItemSingleClickListener);
            adapter.setMultiplePick(false);
            textCount.setVisibility(View.GONE);
        }

        gridGallery.setAdapter(adapter);
        imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

        btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
        btnGalleryOk.setOnClickListener(mOkClickListener);

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.addAll(getGalleryPhotos());
                        checkImageStatus();
                    }
                });
                Looper.loop();
            }
        }.start();

    }

    private void checkImageStatus() {
        if (adapter.isEmpty()) {
            imgNoMedia.setVisibility(View.VISIBLE);
        } else {
            imgNoMedia.setVisibility(View.GONE);
        }
    }

    View.OnClickListener mOkClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ArrayList<CustomGallery> selected = adapter.getSelected();

            String[] allPath = new String[selected.size()];
            for (int i = 0; i < allPath.length; i++) {
                allPath[i] = selected.get(i).sdcardPath;
            }

            Intent data = new Intent().putExtra("all_path", allPath);
            setResult(RESULT_OK, data);
            finish();

        }
    };

    // hujiawei 原有方法有bug，当选择了最大数目张数的图片之后再想取消都不可以了
    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            if (adapter.isSelected(position) || adapter.getSelected().size() <= maxImageAllowedForSelection - 1) {
                adapter.changeSelection(v, position);
                textCount.setText("已选" + adapter.getSelected().size() + "张图片");
            } else {
                ToastUtil.showShortToast(CustomGalleryActivity.this, "最多选择" + maxImageAllowedForSelection + "张图片");
            }
        }
    };

    AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            CustomGallery item = adapter.getItem(position);
            Intent data = new Intent().putExtra("single_path", item.sdcardPath);
            setResult(RESULT_OK, data);
            finish();
        }
    };

    private ArrayList<CustomGallery> getGalleryPhotos() {
        ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

        try {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            if (imagecursor != null && imagecursor.getCount() > 0) {
                while (imagecursor.moveToNext()) {
                    CustomGallery item = new CustomGallery();
                    int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    item.sdcardPath = imagecursor.getString(dataColumnIndex);
                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

}
