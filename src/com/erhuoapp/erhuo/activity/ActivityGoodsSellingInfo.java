package com.erhuoapp.erhuo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.adapter.AdapterGoodsCommentList;
import com.erhuoapp.erhuo.adapter.AdapterGoodsImageViewPager;
import com.erhuoapp.erhuo.model.EntityComment;
import com.erhuoapp.erhuo.model.EntityGoodsSelling;
import com.erhuoapp.erhuo.model.EntityHttpResponse;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.IConstants;
import com.erhuoapp.erhuo.util.ToastUtil;
import com.erhuoapp.erhuo.view.FrameLoading;
import com.erhuoapp.erhuo.view.refresh.PullToRefreshBase;
import com.erhuoapp.erhuo.view.refresh.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 在售商品详情界面
 *
 * @author hujiawei
 * @datetime 2015/1/8
 */
public class ActivityGoodsSellingInfo extends FragmentActivity implements IConstants, View.OnClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    private static final String TAG = "ActivityGoodsSellingInfo";

    private boolean isCollect = false;
    private EntityGoodsSelling goods;
    private Context context;
    private View contentView;
    private HashMap<String, String> params;//

    private TextView textViewTitle;
    private TextView textViewPrice;
    private TextView textViewContent;
    private TextView textViewUserName;
    private TextView textViewCollectnum;
    private TextView textViewCommentnum;
    private TextView textViewCommentnum2;
    private TextView textViewTimedist;

    private ViewPager viewPager;
    private ImageView imageViewChat2;
    private ImageView imageViewHeader;
    private ImageView imageViewAuth;
    private ImageView imageViewNew;
    private ImageView imageViewBargin;
    private ImageView imageViewCollect;
    private ImageView imageViewComment;
    private ImageButton imageButtonShare;
    private LinearLayout layoutCollectButton;
    private LinearLayout layoutCommentButton;

    private FrameLoading frameLoading;
    private RelativeLayout relativeLayoutInfo;
    private LinearLayout focusPoints;
    private ArrayList<ImageView> points = new ArrayList<ImageView>();

    // 评论相关
    private boolean removeData = false, refreshData = false;//refreshData方法的两个参数
    private boolean isCommentShow = false;//
    private boolean isComment = false;//当前用户是否评论过了
    private String pageIndex = "0", displayNumber = "10";
    private HashMap<String, String> commentListParams;//评论列表
    private HashMap<String, String> commentParams;//添加评论

    private ListView listView;
    private PullToRefreshListView refreshListView;
    private AdapterGoodsCommentList adapterCommentList;
    private List<EntityComment> commentList;

    private RelativeLayout layoutCommentArea;
    private ImageButton imageButtonCommentClose;
    private FrameLoading frameLoadingComment;
    private Button buttonComment;
    private ImageView imageViewCommentHeader;
    private EditText editTextComment;

    private DisplayImageOptions displayImageOptions_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        contentView = getLayoutInflater().inflate(R.layout.activity_goodssellinginfo, null);
        setContentView(contentView);

        FrameLayout frameLayout = (FrameLayout) contentView.findViewById(R.id.frame_loading_info);
        frameLoading = new FrameLoading(frameLayout);//详情内容中的加载界面 --> 因为本界面有两个frameloading所以要指定清楚父组件

        textViewTitle = (TextView) findViewById(R.id.tv_goods_name);
        textViewPrice = (TextView) findViewById(R.id.tv_goods_price);
        textViewContent = (TextView) findViewById(R.id.tv_goods_content);
        textViewUserName = (TextView) findViewById(R.id.tv_goods_username);
        textViewCollectnum = (TextView) findViewById(R.id.tv_goods_collectnum);
        textViewCommentnum = (TextView) findViewById(R.id.tv_goods_commentnum);
        textViewTimedist = (TextView) findViewById(R.id.tv_goods_timedist);
        imageViewChat2 = (ImageView) findViewById(R.id.iv_goods_chat2);
        imageViewHeader = (ImageView) findViewById(R.id.iv_goods_header);
        imageViewAuth = (ImageView) findViewById(R.id.iv_goods_auth);
        imageViewNew = (ImageView) findViewById(R.id.iv_goods_new);
        imageViewBargin = (ImageView) findViewById(R.id.iv_goods_bargin);
        imageViewCollect = (ImageView) findViewById(R.id.ib_goods_collect);
        imageViewComment = (ImageView) findViewById(R.id.ib_goods_comment);
        imageButtonShare = (ImageButton) findViewById(R.id.ib_goods_share);
        viewPager = (ViewPager) findViewById(R.id.vp_goods_images);
        focusPoints = (LinearLayout) findViewById(R.id.ll_goods_points);
        relativeLayoutInfo = (RelativeLayout) findViewById(R.id.rl_goods_info);
        layoutCollectButton = (LinearLayout) findViewById(R.id.ll_goods_collect);
        layoutCommentButton = (LinearLayout) findViewById(R.id.ll_goods_comment);

        // 评论区域
        layoutCommentArea = (RelativeLayout) findViewById(R.id.rl_goods_comment);
        imageButtonCommentClose = (ImageButton) layoutCommentArea.findViewById(R.id.ib_goods_commentclose);
        frameLoadingComment = new FrameLoading(layoutCommentArea);//评论区域的加载界面
        buttonComment = (Button) layoutCommentArea.findViewById(R.id.btn_goods_comment);
        imageViewCommentHeader = (ImageView) layoutCommentArea.findViewById(R.id.iv_goods_commentheader);
        editTextComment = (EditText) layoutCommentArea.findViewById(R.id.et_goods_commentcontent);
        textViewCommentnum2 = (TextView) layoutCommentArea.findViewById(R.id.tv_goods_commentnum2);
        // 列表组件
        refreshListView = (PullToRefreshListView) contentView.findViewById(R.id.lv_refresh_comment);
        refreshListView.setPullLoadEnabled(true);
        refreshListView.setPullRefreshEnabled(true);
        refreshListView.setOnRefreshListener(this);
        listView = refreshListView.getRefreshableView();

        commentList = new ArrayList<EntityComment>();
        adapterCommentList = new AdapterGoodsCommentList(context, 0, commentList);
        listView.setAdapter(adapterCommentList);

        displayImageOptions_header = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.header_default)
                .showImageOnFail(R.drawable.header_default)
                .showImageOnLoading(R.drawable.header_default)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(100, 3)).build();

        // 事件监听
        findViewById(R.id.ib_goods_return).setOnClickListener(this);
        imageViewChat2.setOnClickListener(this);
        layoutCollectButton.setOnClickListener(this);
        layoutCommentButton.setOnClickListener(this);
        imageButtonShare.setOnClickListener(this);
        imageButtonCommentClose.setOnClickListener(this);
        buttonComment.setOnClickListener(this);
        
        
        // 初始化数据
        goods = (EntityGoodsSelling) (getIntent().getExtras().get("goods"));// ID一定会有的，其他属性值不一定
        params = new HashMap<String, String>();//商品信息的请求参数
        commentListParams = new HashMap<String, String>();//评论列表的请求参数
        commentListParams.put("type", "sell");
        commentParams = new HashMap<String, String>();//添加评论的请求参数
        commentParams.put("type", "sell");
        //这里为了用户进入界面的时候马上能够看到一些必要信息，所以直接从前面的goods中提取信息马上显示
        //但是因为它的信息可能不足，不论是从商品列表还是从收藏列表，所以还是需要重新异步加载商品信息然后更新显示
        params.put("id", goods.getId());//异步加载商品信息
        commentListParams.put("id", goods.getId());//给评论请求参数设置id
        commentParams.put("id", goods.getId());//给评论请求参数设置id

        // 初始化界面
        frameLoading.hideFrame();
        layoutCommentArea.setVisibility(View.GONE);//一开始不显示评论，也不要进行加载
        new CloudUtil().GoodsInfo(params, new GoodsInfoCallback());//加载二货详情
        // 评论部分的用户头像
        if (AppUtil.getInstance().checkUserLogin()) {//只有当前有用户登录了才行
            EntityUserInfo userInfo = AppUtil.getInstance().getBasicUserInfo();
            ImageLoader.getInstance().displayImage(userInfo.getHeader(), imageViewCommentHeader, displayImageOptions_header);
        }
        
        //聊天触发
        findViewById(R.id.iv_goods_chat2).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!AppUtil.getInstance().checkUserLogin()) {//没有用户登录数据
					ActivityGoodsSellingInfo.this.startActivityForResult(new Intent(ActivityGoodsSellingInfo.this, ActivityLogin.class), REQUEST_LOGIN);
		            return;
		        }
				else {
				String uid = goods.getUserId();
				startActivity(new Intent(ActivityGoodsSellingInfo.this, ChatActivity.class)
				.putExtra("userId", uid)
				);
				}
			}
		});
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 暂时没必要每次resume的时候都重新加载数据
        Log.e(TAG, "onResume");
        MobclickAgent.onResume(this);//友盟统计onResume
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Log.e(TAG, "onPause");
    	MobclickAgent.onPause(this);//友盟统计onPause
    }

    // 更新显示商品信息
    private void updateUI(final EntityGoodsSelling goods) {
        textViewTitle.setText(goods.getTitle());
        textViewPrice.setText("￥" + goods.getPrice());
        textViewCollectnum.setText(goods.getCollectNum());
        textViewCommentnum.setText(goods.getCommentNum());
        textViewCommentnum2.setText(goods.getCommentNum());
        textViewContent.setText(goods.getContent());
        textViewUserName.setText(goods.getUserNickName());
        textViewTimedist.setText(goods.getTime() + "　　" + goods.getDistance());

        if (!goods.isNew()) {
            imageViewNew.setVisibility(View.GONE);
        } else {
            imageViewNew.setVisibility(View.VISIBLE);
        }
        if (!goods.isBargin()) {
            imageViewBargin.setVisibility(View.GONE);
        } else {
            imageViewBargin.setVisibility(View.VISIBLE);
        }
        if (goods.isAuth()) {
            imageViewAuth.setVisibility(View.VISIBLE);
        } else {
            imageViewAuth.setVisibility(View.INVISIBLE);
        }
        isComment = goods.isComment();
        updateCommentButton();
        isCollect = goods.isCollect();
        updateCollectButton();

        ImageLoader.getInstance().displayImage(goods.getUserHeader(), imageViewHeader, displayImageOptions_header);
        imageViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityGoodsSellingInfo.this, ActivityOther.class);
                Bundle bundle = new Bundle();
                EntityUserInfo user = new EntityUserInfo();
                user.setId(goods.getUserId());
                user.setHeader(goods.getUserHeader());
                user.setNickName(goods.getUserNickName());
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                ActivityGoodsSellingInfo.this.startActivity(intent);
            }
        });
    }

    /**
     * 商品图片信息
     */
    class GoodsInfoCallback implements CloudUtil.OnPostRequest<EntityGoodsSelling> {

        @Override
        public void onPost() {
            relativeLayoutInfo.setVisibility(View.INVISIBLE);
            frameLoading.showFrame();//需要注意的是这里加载等待界面是放在商品图片列表中的！
        }

        @Override
        public void onPostOk(final EntityGoodsSelling goodstemp) {
            relativeLayoutInfo.setVisibility(View.VISIBLE);
            frameLoading.hideFrame();// 关闭动画
            frameLoading.setListener(null);
            updateUI(goodstemp);

            focusPoints.removeAllViews();//防止多次加载出现数量的叠加
            points = new ArrayList<ImageView>();

            //添加相应数目个的小圆点
            for (int i = 0; i < goodstemp.getImages().size(); i++) {
                ImageView imageView = new ImageView(ActivityGoodsSellingInfo.this);
                imageView.setPadding(0, 0, 10, 0);
                points.add(imageView);
                focusPoints.addView(imageView);//多次加载可能会重复叠加
            }
            //处理viewpager
            updatePoints(0);
            AdapterGoodsImageViewPager adapterGoodsImageViewPager = new AdapterGoodsImageViewPager(ActivityGoodsSellingInfo.this, goodstemp.getImages());
            viewPager.setAdapter(adapterGoodsImageViewPager);
            viewPager.setCurrentItem(0);
            image_first = adapterGoodsImageViewPager.getStrItem(0);//获取第一张图片链接
            
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    updatePoints(position);
                }

                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        @Override
        public void onPostFailure(String err) {
            frameLoading.showMessage("加载失败，点击重试");
            frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                @Override
                public void onFrameLoadingClicked() {
                    new CloudUtil().GoodsInfo(params, new GoodsInfoCallback());//最后一次执行的连接操作
                }
            });
            Log.e(TAG, err);
        }
    }

    // 更新小圆点
    private void updatePoints(int position) {
        for (int i = 0; i < points.size(); i++) {
            if (position == i) {
                points.get(i).setImageResource(R.drawable.round_point);//chosen
            } else {
                points.get(i).setImageResource(R.drawable.round_point_normal);
            }
        }
    }

    // 更新收藏按钮状态
    private void updateCollectButton() {
        if (isCollect) {
            imageViewCollect.setImageResource(R.drawable.favorite2_active);
        } else {
            imageViewCollect.setImageResource(R.drawable.favorite2);
        }
    }

    // 更新评论按钮状态
    private void updateCommentButton() {
        if (isComment) {
            imageViewComment.setImageResource(R.drawable.comment2_active);
        } else {
            imageViewComment.setImageResource(R.drawable.comment2);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_goods_return://返回
                finish();
                break;//这里一定要加上break
            case R.id.ll_goods_collect://收藏
                doCollect();
                break;
            case R.id.ll_goods_comment://打开评论区域
                openCommentArea();
                break;
            case R.id.ib_goods_commentclose://关闭评论区域
                closeCommentArea();
                break;
            case R.id.btn_goods_comment://评论
                doComment();
                break;
            case R.id.ib_goods_share://分享
            	//Toast.makeText(this, image_first, Toast.LENGTH_LONG).show();
            	UM_goodsShare();
            	break;
        }
    }

    //友盟分享
    String image_first;
    
    //友盟分享 -- 商品
    private void UM_goodsShare() {
    	
    	//友盟分享
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        UMImage temp_share_image = new UMImage(this, image_first);
        
    	// 设置分享面板上显示的平台 
    	//mController.getConfig().setPlatforms(SHARE_MEDIA.RENREN);
    	//mController.getConfig().setPlatforms(SHARE_MEDIA.DOUBAN);
    	mController.getConfig().removePlatform( SHARE_MEDIA.TENCENT, SHARE_MEDIA.QZONE);
    	mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
    	
    	// 微信平台
    	UMWXHandler wxHandler = new UMWXHandler(this,"wx73fef33b1a8e1e2e",
    			"74eeaf442cbe3658034f1036be1f2ffa");
    	wxHandler.addToSocialSDK();
    	
    	// 微信朋友圈
    	UMWXHandler wxCircleHandler = new UMWXHandler(this,"wx73fef33b1a8e1e2e",
    			"74eeaf442cbe3658034f1036be1f2ffa");
    	wxCircleHandler.setToCircle(true);
    	wxCircleHandler.addToSocialSDK();
    	
    	// QQ平台
    	UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "1104169970",
                "JSVF4URRWRHgsKkJ");
    	qqSsoHandler.addToSocialSDK();
    	
    	// QQ空间
    	QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, "1104169970",
                "JSVF4URRWRHgsKkJ");
    	qZoneSsoHandler.addToSocialSDK();
    	
    	
    	//设置微信好友分享内容
    	WeiXinShareContent weixinContent = new WeiXinShareContent();
    	//设置分享文字
    	weixinContent.setShareContent("贰货，让你的闲置物品转起来");
    	//设置title
    	weixinContent.setTitle("贰货——大学生闲置物品交易平台");
    	//设置分享内容跳转URL
    	weixinContent.setTargetUrl("http://www.erhuoapp.com");
    	//设置分享图片
    	weixinContent.setShareImage(temp_share_image);
    	mController.setShareMedia(weixinContent);
    	
    	//设置微信朋友圈分享内容
    	CircleShareContent circleMedia = new CircleShareContent();
    	circleMedia.setShareContent("贰货，让你的闲置物品转起来");
    	circleMedia.setTitle("贰货——大学生闲置物品交易平台");
    	circleMedia.setShareImage(temp_share_image);
    	circleMedia.setTargetUrl("http://www.erhuoapp.com");
    	mController.setShareMedia(circleMedia);
    	
    	//设置QQ分享内容
    	QQShareContent qqShareContent = new QQShareContent();
    	qqShareContent.setShareContent("贰货，让你的闲置物品转起来");
    	qqShareContent.setTitle("贰货——大学生闲置物品交易平台");
    	qqShareContent.setShareImage(temp_share_image);
    	qqShareContent.setTargetUrl("http://www.erhuoapp.com");
    	mController.setShareMedia(qqShareContent);
    	
    	// 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent("贰货，让你的闲置物品转起来");
        qzone.setTargetUrl("http://www.erhuoapp.com");
        qzone.setTitle("贰货——大学生闲置物品交易平台");
        qzone.setShareImage(temp_share_image);
        mController.setShareMedia(qzone);
        
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        
        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setShareContent("贰货——大学生闲置物品交易平台\n贰货，让你的闲置物品转起来~\nhttp://www.erhuoapp.com");
        mController.setShareMedia(sinaContent);
    	
        // 分享触发
    	mController.openShare(this, false);
    }
    
    /**
     * ***** 收藏部分 ********
     */

    // 处理收藏
    private void doCollect() {
        if (isCollect) {
            new CloudUtil().RemoveCollect(params, new RemoveCollectCallbak());
        } else {
            new CloudUtil().AddCollect(params, new AddCollectCallback());
        }
    }

    /**
     * 添加收藏
     */
    class AddCollectCallback implements CloudUtil.OnPostRequest<EntityHttpResponse> {

        @Override
        public void onPost() {

        }

        @Override
        public void onPostOk(final EntityHttpResponse result) {
            ToastUtil.showShortToast(ActivityGoodsSellingInfo.this, "收藏成功");
            isCollect = true;
            updateCollectButton();
            textViewCollectnum.setText(String.valueOf(Integer.parseInt(textViewCollectnum.getText().toString()) + 1));
        }

        @Override
        public void onPostFailure(String err) {
            if (null != err && err.equalsIgnoreCase("alreay_collected")) {
                ToastUtil.showShortToast(ActivityGoodsSellingInfo.this, "已经收藏了");
            } else if (null != err && err.equalsIgnoreCase("not_logged")) {
                ToastUtil.showShortToast(ActivityGoodsSellingInfo.this, "请先登录");
            } else {
                ToastUtil.showShortToast(ActivityGoodsSellingInfo.this, "收藏失败");
            }
            Log.e(TAG, err);
        }
    }

    /**
     * 取消收藏
     */
    class RemoveCollectCallbak implements CloudUtil.OnPostRequest<EntityHttpResponse> {

        @Override
        public void onPost() {

        }

        @Override
        public void onPostOk(final EntityHttpResponse result) {
            ToastUtil.showShortToast(ActivityGoodsSellingInfo.this, "取消收藏成功");
            isCollect = false;
            updateCollectButton();
            textViewCollectnum.setText(String.valueOf(Integer.parseInt(textViewCollectnum.getText().toString()) - 1));
        }

        @Override
        public void onPostFailure(String err) {
            if (null != err && err.equalsIgnoreCase("not_collected")) {
                ToastUtil.showShortToast(ActivityGoodsSellingInfo.this, "还未收藏");
            } else if (null != err && err.equalsIgnoreCase("not_logged")) {
                ToastUtil.showShortToast(ActivityGoodsSellingInfo.this, "请先登录");
            } else {
                ToastUtil.showShortToast(ActivityGoodsSellingInfo.this, "取消收藏失败");
            }
            Log.e(TAG, err);
        }
    }

    /**
     * ***** 评论部分 ********
     */

    public void doComment() {
        String content = editTextComment.getText().toString();
        if (null == content || "".equalsIgnoreCase(content)) {
            ToastUtil.showShortToast(context, "请输入评论内容");
            return;
        }
        commentParams.put("content", content);
        new CloudUtil().AddComment(commentParams, new AddCommentCallback());
    }

    // 打开评论区域
    private void openCommentArea() {
        if (!AppUtil.getInstance().checkUserLogin()) {
            ToastUtil.showShortToast(context, "请先登录");
            return;
        }
        isCommentShow = true;
        layoutCommentArea.setVisibility(View.VISIBLE);
        frameLoadingComment.hideFrame();
        // 加载评论数据
        initData();//不论加没加载过数据都要重新加载
    }

    // 关闭评论区域
    private void closeCommentArea() {
        isCommentShow = false;
        layoutCommentArea.setVisibility(View.GONE);
    }

    // 初始化评论内容
    private void initData() {
        pageIndex = "0"; displayNumber = "10";
        refreshData(true, false);
    }

    // OnRefreshListener --- start
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageIndex = "0";
        refreshData(true, true);//kind保持不变
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        int index = Integer.parseInt(pageIndex) + 1;//下一页
        pageIndex = String.valueOf(index);
        refreshData(false, true);//不删除原有数据
    }
    // OnRefreshListener --- end

    // 重新请求获取对应排序方式的数据，调用之前需要设置一些参数，例如kind，auth，pageIndex
    // removeData表示是否要删除以前的列表数据，refreshData表示当前是不是上拉或者下拉刷新
    private void refreshData(boolean removeData, boolean refreshData) {
        this.removeData = removeData;
        this.refreshData = refreshData;

        commentListParams.put("pageIndex", pageIndex);
        commentListParams.put("displayNumber", displayNumber);
        new CloudUtil().GoodsCommentList(commentListParams, new GoodsCommentListCallback());
    }

    // 商品评论列表的回调
    class GoodsCommentListCallback implements CloudUtil.OnPostRequest<ArrayList<EntityComment>> {
        @Override
        public void onPost() {
            if (!refreshData) {//如果不是上拉或者下拉刷新就要显示加载界面
                frameLoadingComment.showFrame();//开始显示动画
            }

            //如果存在数据并且需要清空的话先清空数据
            if (removeData && commentList != null) {
                commentList.clear();//这里会不会notify
            }
        }

        @Override
        public void onPostOk(ArrayList<EntityComment> temp) {
            Log.e(TAG, "data size = " + temp.size());
            frameLoadingComment.hideFrame();

            int oldSize = commentList.size();
            commentList.addAll(temp);//直接对adapter使用addAll方法需要高版本的Android支持
            adapterCommentList.notifyDataSetChanged();//刷新之后可能变为空了，所以这里要刷新一下

            if (refreshData) {
                refreshListView.onPullUpRefreshComplete();//下拉刷新完成
                refreshListView.onPullDownRefreshComplete();//上拉刷新完成
            }
            // 没有数据 [考虑到可能是上拉或者下拉，所以要设置刷新完成]
            if (commentList.size() < 1) {
                frameLoadingComment.showNoComment();
                frameLoadingComment.setListener(new FrameLoading.FrameLoadingListener() {
                    @Override
                    public void onFrameLoadingClicked() {
                        initData();
                    }
                });
                return;
            }

            if (!removeData) {//解决上拉刷新的时候重新回到列表第一个元素的问题
                if (oldSize > 1) {
                    listView.setSelection(oldSize - 1);//将原来数据列表中的最后一个显示在新的数据中的第一个
                } else {
                    listView.setSelection(oldSize);//最新的数据的第一个会出现在显示出来的数据中的第一个
                }
            } else {
                listView.setSelection(0);
            }
        }

        @Override
        public void onPostFailure(String err) {
            frameLoadingComment.showMessage("加载失败，点击重试");
            frameLoadingComment.setListener(new FrameLoading.FrameLoadingListener() {
                @Override
                public void onFrameLoadingClicked() {
                    refreshData(removeData, refreshData);
                }
            });
            Log.e(TAG, err);
        }
    }

    // 添加评论回调
    class AddCommentCallback implements CloudUtil.OnPostRequest<EntityComment> {
        @Override
        public void onPost() {
            closeInputMethod();//发送请求时马上关闭输入键盘
        }

        @Override
        public void onPostOk(EntityComment temp) {
            ToastUtil.showShortToast(context, "添加评论成功");
            if (commentList.size() > 0) {//之前有评论的
                commentList.add(0, temp);
                adapterCommentList.notifyDataSetChanged();
                listView.setSelection(0);//回到第一个
            } else {
                initData();
            }
            editTextComment.setText("");//清空内容
            // 更新评论数目
            int newNum = Integer.parseInt(textViewCommentnum2.getText().toString()) + 1;
            textViewCommentnum.setText(newNum + "");
            textViewCommentnum2.setText(newNum + "");
            // 更新评论按钮
            isComment = true;
            updateCommentButton();
        }

        @Override
        public void onPostFailure(String err) {
            ToastUtil.showShortToast(context, "添加评论失败");
            Log.e(TAG, err);
        }
    }

    // 处理该界面的返回键或者其他键的点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                keyBack();
                return true;
        }
        return super.onKeyDown(keyCode, event);//
    }

    public void keyBack() {//按下返回键 --> 没有result需要设置！
        if (isCommentShow) {
            closeCommentArea();//如果是在显示了评论区域的时候点击返回那么就只要关闭显示区域就行
        } else {
            finish();
        }
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
