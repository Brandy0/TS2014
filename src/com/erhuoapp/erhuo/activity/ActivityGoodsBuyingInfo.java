package com.erhuoapp.erhuo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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

import com.erhuoapp.erhuo.R;
import com.erhuoapp.erhuo.adapter.AdapterGoodsCommentList;
import com.erhuoapp.erhuo.im.db.HeadAndName;
import com.erhuoapp.erhuo.im.domain.UserHeadAndName;
import com.erhuoapp.erhuo.model.EntityComment;
import com.erhuoapp.erhuo.model.EntityGoodsBuying;
import com.erhuoapp.erhuo.model.EntityUserInfo;
import com.erhuoapp.erhuo.util.AppUtil;
import com.erhuoapp.erhuo.util.CloudUtil;
import com.erhuoapp.erhuo.util.ErUse;
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

/**
 * 求购商品详情界面
 *
 * @author hujiawei
 * @datetime 2015/1/10
 */
public class ActivityGoodsBuyingInfo extends FragmentActivity implements IConstants, View.OnClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    private static final String TAG = "ActivityGoodsBuyingInfo";

    private Context context;
    private View contentView;
    private EntityGoodsBuying goods;
    private HashMap<String, String> params;

    private TextView textViewTitle;
    private TextView textViewPrice;
    private TextView textViewContent;
    private TextView textViewUserName;
    private TextView textViewCommentnum;
    private TextView textViewTime;
    private TextView textViewDist;

    private FrameLayout layoutAuthinfo;
    private LinearLayout layoutNoauthinfo;
    private TextView textViewTimeAuth;
    private TextView textViewDistAuth;
    private TextView textViewNameAuth;

    private ImageView imageViewHeader;
    private ImageButton imageButtonShare;
    private FrameLoading frameLoading;

    // 评论相关
    private boolean removeData = false, refreshData = false;//refreshData方法的两个参数
    private String pageIndex = "0", displayNumber = "10";
    private HashMap<String, String> commentListParams;//评论列表
    private HashMap<String, String> commentParams;//添加评论

    private ListView listView;
    private PullToRefreshListView refreshListView;
    private AdapterGoodsCommentList adapterCommentList;
    private List<EntityComment> commentList;

    private RelativeLayout layoutCommentArea;
    private FrameLoading frameLoadingComment;
    private Button buttonComment;
    private ImageView imageViewCommentHeader;
    private EditText editTextComment;

    private DisplayImageOptions displayImageOptions_header;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        contentView = getLayoutInflater().inflate(R.layout.activity_goodsbuyinginfo, null);
        setContentView(contentView);

        FrameLayout frameLayout = (FrameLayout) contentView.findViewById(R.id.frame_loading_info);
        frameLoading = new FrameLoading(frameLayout);//详情内容中的加载界面 --> 因为本界面有两个frameloading所以要指定清楚父组件

        textViewTitle = (TextView) findViewById(R.id.tv_goods_name);
        textViewPrice = (TextView) findViewById(R.id.tv_goods_price);
        textViewContent = (TextView) findViewById(R.id.tv_goods_content);
        textViewUserName = (TextView) findViewById(R.id.tv_goods_username);
        textViewCommentnum = (TextView) findViewById(R.id.tv_goods_commentnum);
        textViewTime = (TextView) findViewById(R.id.tv_goods_time);
        textViewDist = (TextView) findViewById(R.id.tv_goods_dist);
        imageViewHeader = (ImageView) findViewById(R.id.iv_goods_header);
        imageButtonShare = (ImageButton) findViewById(R.id.ib_goods_share);
        layoutAuthinfo = (FrameLayout) findViewById(R.id.fl_goods_auth);
        layoutAuthinfo = (FrameLayout) findViewById(R.id.ll_goods_authinfo);
        layoutNoauthinfo = (LinearLayout) findViewById(R.id.ll_goods_noauthinfo);
        textViewTimeAuth = (TextView) findViewById(R.id.tv_goods_auth_time);
        textViewDistAuth = (TextView) findViewById(R.id.tv_goods_auth_dist);
        textViewNameAuth = (TextView) findViewById(R.id.tv_goods_auth_username);

        // 评论区域
        layoutCommentArea = (RelativeLayout) findViewById(R.id.rl_goods_comment);
        frameLoadingComment = new FrameLoading(layoutCommentArea);//评论区域的加载界面
        buttonComment = (Button) layoutCommentArea.findViewById(R.id.btn_goods_comment);
        imageViewCommentHeader = (ImageView) layoutCommentArea.findViewById(R.id.iv_goods_commentheader);
        editTextComment = (EditText) layoutCommentArea.findViewById(R.id.et_goods_commentcontent);
        textViewCommentnum = (TextView) layoutCommentArea.findViewById(R.id.tv_goods_commentnum);
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
        imageButtonShare.setOnClickListener(this);
        buttonComment.setOnClickListener(this);

        // 初始化数据
        goods = (EntityGoodsBuying) (getIntent().getExtras().get("goods"));// ID一定会有的，其他属性值不一定
        params = new HashMap<String, String>();
        commentListParams = new HashMap<String, String>();//评论列表的请求参数
        commentListParams.put("type", "shopping");
        commentParams = new HashMap<String, String>();//添加评论的请求参数
        commentParams.put("type", "shopping");
        params.put("id", goods.getId());
        commentListParams.put("id", goods.getId());//给评论请求参数设置id
        commentParams.put("id", goods.getId());//给评论请求参数设置id

        // 初始化界面
        editTextComment.clearFocus();
        frameLoading.hideFrame();
        new CloudUtil().GoodsBuyingInfo(params, new GoodsBuyingInfoCallbak());//加载求购信息
        initData();//加载评论列表
        // 评论部分的用户头像
        if (AppUtil.getInstance().checkUserLogin()) {//只有当前有用户登录了才行
            EntityUserInfo userInfo = AppUtil.getInstance().getBasicUserInfo();
            ImageLoader.getInstance().displayImage(userInfo.getHeader(), imageViewCommentHeader, displayImageOptions_header);
        }
        
        //聊天触发
        findViewById(R.id.iv_goods_chat).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!AppUtil.getInstance().checkUserLogin()) {//没有用户登录数据
					ActivityGoodsBuyingInfo.this.startActivityForResult(new Intent(ActivityGoodsBuyingInfo.this, ActivityLogin.class), REQUEST_LOGIN);
		            return;
		        }
				else {
				String uid = goods.getUserId();
				startActivity(new Intent(ActivityGoodsBuyingInfo.this, ChatActivity.class)
				.putExtra("userId", uid)
				);
				}
			}
		});
       
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_goods_return://返回
                finish();
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
    
   
    //友盟分享 -- 商品
    private void UM_goodsShare() {
    	
    	//友盟分享
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        UMImage temp_share_image = new UMImage(this, R.drawable.icon512);
        
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

    // 更新数据
    private void updateUI(final EntityGoodsBuying goods) {
        textViewTitle.setText(goods.getTitle());
        textViewPrice.setText("￥" + goods.getPrice());
        textViewCommentnum.setText(goods.getCommentNum());
        textViewUserName.setText(goods.getUserNickName());
        textViewTime.setText(goods.getTime());
        textViewDist.setText(goods.getDistance());
        textViewContent.setText(goods.getContent());
        textViewNameAuth.setText(goods.getUserNickName());
        textViewDistAuth.setText(goods.getDistance());
        textViewTimeAuth.setText(goods.getTime());

        if (goods.isAuth()) {
            layoutAuthinfo.setVisibility(View.VISIBLE);
            layoutNoauthinfo.setVisibility(View.GONE);
        } else {
            layoutAuthinfo.setVisibility(View.GONE);
            layoutNoauthinfo.setVisibility(View.VISIBLE);
        }

        ImageLoader.getInstance().displayImage(goods.getUserHeader(), imageViewHeader, displayImageOptions_header);
        imageViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityGoodsBuyingInfo.this, ActivityOther.class);
                Bundle bundle = new Bundle();
                EntityUserInfo user = new EntityUserInfo();
                user.setId(goods.getUserId());
                user.setHeader(goods.getUserHeader());
                user.setNickName(goods.getUserNickName());
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                ActivityGoodsBuyingInfo.this.startActivity(intent);
            }
        });
    }

    /**
     * 商品图片信息
     */
    class GoodsBuyingInfoCallbak implements CloudUtil.OnPostRequest<EntityGoodsBuying> {

        @Override
        public void onPost() {
            frameLoading.showFrame();//需要注意的是这里加载等待界面是整个中间界面，所以加载前用不着设置数据
        }

        @Override
        public void onPostOk(final EntityGoodsBuying goodstemp) {
            frameLoading.hideFrame();// 关闭动画
            updateUI(goodstemp);
            
            HeadAndName mHeadAndName = new HeadAndName(ActivityGoodsBuyingInfo.this);
            UserHeadAndName user = new UserHeadAndName();
            user.setHead(goodstemp.getUserHeader());
            user.setNick(goodstemp.getUserNickName());
            user.setUserId(goodstemp.getUserId());
			mHeadAndName.saveContact(user );
            
        }

        @Override
        public void onPostFailure(String err) {
            frameLoading.showMessage("加载失败，点击重试");
            frameLoading.setListener(new FrameLoading.FrameLoadingListener() {
                @Override
                public void onFrameLoadingClicked() {
                    new CloudUtil().GoodsBuyingInfo(params, new GoodsBuyingInfoCallbak());
                }
            });
            Log.e(TAG, err);
        }
    }

    /**
     * ***** 评论部分 ********
     */
    public void doComment() {
        if (!AppUtil.getInstance().checkUserLogin()){
            ToastUtil.showShortToast(context, "请先登录");
            return;
        }
        String content = editTextComment.getText().toString();
        if (null == content || "".equalsIgnoreCase(content)) {
            ToastUtil.showShortToast(context, "请输入评论内容");
            return;
        }
        commentParams.put("content", content);
        new CloudUtil().AddComment(commentParams, new AddCommentCallback());
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
        refreshData(true, true);
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
            int newNum = Integer.parseInt(textViewCommentnum.getText().toString()) + 1;
            textViewCommentnum.setText(newNum + "");
            //ErUse.numOfComment = newNum;
        }

        @Override
        public void onPostFailure(String err) {
            ToastUtil.showShortToast(context, "添加评论失败");
            Log.e(TAG, err);
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
