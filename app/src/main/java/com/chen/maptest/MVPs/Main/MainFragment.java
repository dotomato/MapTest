package com.chen.maptest.MVPs.Main;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chen.maptest.ComViews.EdittextSizeChangeEvent;
import com.chen.maptest.ComViews.MyTimeShow;
import com.chen.maptest.GlobalConst;
import com.chen.maptest.MVPs.Main.Views.CommentFooterView;
import com.chen.maptest.MVPs.Main.Views.MyMapIcon;
import com.chen.maptest.MapAdapter.MapAdapterLayout;
import com.chen.maptest.MapAdapter.MapAdaterCallback;
import com.chen.maptest.MapAdapter.MyLatlng;
import com.chen.maptest.NetDataType.UserComment;
import com.chen.maptest.NetDataType.UserLikeCommentResult;
import com.chen.maptest.R;
import com.chen.maptest.Utils.ImageWrap;
import com.chen.maptest.Utils.MyUtils;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.codetail.animation.ViewAnimationUtils;


/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public class MainFragment extends Fragment implements MainContract.View, MapAdaterCallback {

    public static final int ALBUM_REQUESR_CODE = 10;
    private static final String TAG = "MainFragment";

    final float h2Radio = 1/3.0f;
    public static int dura = 300;
    public static int dura2 = 1000;


    private FloatingActionButton mFloatingActionButton;

    private MapAdapterLayout mMapAdapter;

    private VerticalSeekBar mZoombar;

    private ViewGroup mZoomCtrl;

    private View mZoomIn;

    private View mZoomOut;

    private View mRetLocation;

    private ListView mMsgScrollView;

    private EditText mMsgTitle;

    private TextView mMsgUsername;

    private ImageView mMsgUsericon;

    private MyTimeShow mMsgTimeshow;

    private EdittextSizeChangeEvent mMsgText;


    private ProgressBar mProgressBar;

    private ViewGroup mMsgContainer;
    private ViewGroup mMsgInnerContainer;

    private View mMapView;
    private MainContract.Presenter mPresenter;

    private int self_w;
    private int self_h;
    private View mView;
    private int h2;
    private View mToolbar;
    private boolean hasAlbum;
    private String albumFullName;
    private List<UserComment> mCommentData;
    private View mHeadView;
    private CommentAdapter mCommentAdapter;
    private CommentFooterView mFooterView;
    private ViewGroup mCommentLayout;
    private View mCommentSendButton;
    private EditText mUserCommentEdit;
    private TextView mPointLikeNum;
    private ShineButton mPointLikeButton;
    private ViewGroup mPointLiker;
    private TextView mPointCommentNum;    
    private ImageView mMsgAlbum;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        //初始控件填充
        mView = inflater.inflate(R.layout.main_frag, container, false);

        mFloatingActionButton = (FloatingActionButton)mView.findViewById(R.id.floatingActionButton);
        mMapAdapter = (MapAdapterLayout)mView.findViewById(R.id.mapAdapter);
        mZoombar = (VerticalSeekBar)mView.findViewById(R.id.zoombar);
        mZoomCtrl = (ViewGroup)mView.findViewById(R.id.zoomCtrl);
        mZoomIn = mView.findViewById(R.id.zoominbutton);
        mZoomOut = mView.findViewById(R.id.zoomoutbutton);
        mRetLocation = mView.findViewById(R.id.retlocalbutton);
        mMsgScrollView = (ListView) mView.findViewById(R.id.msgScrollView);
        mCommentLayout = (ViewGroup) mView.findViewById(R.id.commentLayout);
        mCommentSendButton =  mView.findViewById(R.id.commentsendbutton);
        mUserCommentEdit = (EditText) mView.findViewById(R.id.usercommentedit);
        mMsgContainer = (ViewGroup) mView.findViewById(R.id.msgContainer);
        mMsgInnerContainer = (ViewGroup) mView.findViewById(R.id.msgInnerContainer);

        //变量初始化
        mCommentData = new ArrayList<>();

        //地图控件初始化
        mMapAdapter.onCreate(savedInstanceState);
        mMapView = MapAdapterLayout.getMapView();
        mMapAdapter.setMapAdaterCallback(this);

        //控件初始化
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.newPointButton(null);
            }
        });

        mZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapAdapter.onZoomCtrl(mZoombar.getProgress()*1.0/100-0.1);
            }
        });

        mZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapAdapter.onZoomCtrl(mZoombar.getProgress()*1.0/100+0.1);
            }
        });

        mRetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.retLocation();
            }
        });

        mZoombar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!b) return;
                mMapAdapter.onZoomCtrl(i*1.0/100);}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mCommentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.pointComment(mUserCommentEdit.getText().toString());
            }
        });

        //消息及评论控件填充

        mHeadView = inflater.inflate(R.layout.main_frag_message,mMsgScrollView,false);
        mFooterView = (CommentFooterView) inflater.inflate(R.layout.main_frag_comment_foorter,mMsgScrollView,false);

        mMsgScrollView.addHeaderView(mHeadView);
        mMsgScrollView.addFooterView(mFooterView);


        mCommentAdapter = new CommentAdapter(getContext(),mCommentData);
        mCommentAdapter.setMainPresenter(mPresenter);
        mMsgScrollView.setAdapter(mCommentAdapter);
        mMsgScrollView.setHeaderDividersEnabled(true);
        mMsgScrollView.setFooterDividersEnabled(false);

        //消息内控件初始化
        mMsgTitle = (EditText)mHeadView.findViewById(R.id.msgTitle);
        mMsgUsername = (TextView)mHeadView.findViewById(R.id.msgUsername);
        mMsgUsericon = (ImageView)mHeadView.findViewById(R.id.msgUserIcon);
        mMsgTimeshow = (MyTimeShow)mHeadView.findViewById(R.id.msgTime);
        mMsgText = (EdittextSizeChangeEvent)mHeadView.findViewById(R.id.msgText);
        mMsgAlbum = (ImageView)mHeadView.findViewById(R.id.msgAlbum);
        mProgressBar = (ProgressBar)mHeadView.findViewById(R.id.progressBar2);
        mPointLikeNum = (TextView)mHeadView.findViewById(R.id.pointLikeNum);
        mPointLikeButton = (ShineButton)mHeadView.findViewById(R.id.pointLikeButton);
        mPointLiker = (ViewGroup)mHeadView.findViewById(R.id.pointLiker);
        mPointCommentNum = (TextView)mHeadView.findViewById(R.id.pointCommentNum);

        mMsgAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing()){
                    MyUtils.pickFromGallery(getActivity(), ALBUM_REQUESR_CODE,"选择图片");
                }
            }
        });

        mPointLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.pointLike(mPointLikeButton.isChecked());
            }
        });

//        mMsgText.addTextChangedListener(new TextWatcher() {
//            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
//            @Override public void afterTextChanged(Editable s) {
//                if (mMsgText.getLineCount()>1){mMsgText.setGravity(Gravity.START);
//                } else {mMsgText.setGravity(Gravity.CENTER_HORIZONTAL);}
//            }});

        ViewTreeObserver vto = mView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                self_w = mView.getWidth();
                self_h = mView.getHeight();
                h2 = (int) (self_h*h2Radio);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        mToolbar = getActivity().findViewById(R.id.toolbar);

        return mView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onResume();
    }

    @Override
    public void onPause() {
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onStop();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onDestroy();
        mPresenter.destroy();
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        MapAdapterLayout.getMapView().onSaveInstanceState(outState);
    }

    public void MyTouch(MotionEvent motionEvent) {
    }

    @Override
    public void MyMarkerClick(String pointID, String userID) {
        mPresenter.clickPoint(pointID, userID);
    }

    public void MyCameraChangeStart() {
    }

    public void MyCameraChangeFinish() {
        mZoombar.setProgress((int) (mMapAdapter.getZoom()*100));

        int w = mMapView.getWidth();
        int h = mMapView.getHeight();
        MyLatlng lefttop = mMapAdapter.pointToMyLatlng(new PointF(0,0));
        MyLatlng rightbottom = mMapAdapter.pointToMyLatlng(new PointF(w,h));
        MyLatlng center = mMapAdapter.pointToMyLatlng(new PointF(w/2,h/2));

        mPresenter.mapMove(lefttop, rightbottom, center);
    }

    @Override
    public void MyGPSRecive(MyLatlng latlng) {
        mPresenter.reciveLocation(latlng);
    }

    @Override
    public void firstLocation(final MyLatlng latlng) {
        mMapAdapter.gotoLocationZoomSmooth(latlng,15,1000);
    }


    public void onBackPressed(){
        mPresenter.onBackPressed();
    }

    @Override
    public void finish(){
        getActivity().finish();
    }

    @Override
    public void replaceMsgAlbum(String fullName) {
        hasAlbum = true;
        albumFullName = fullName;
        Glide.with(getContext()).load(fullName).into(mMsgAlbum);
    }

    @Override
    public void setUploadProgress(int progress, int visibility) {
        Log.i(TAG,""+progress);
        mProgressBar.setProgress(progress);
        mProgressBar.setVisibility(visibility);
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void moveMap(final MyLatlng center, boolean delay) {
        if (delay)
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMapAdapter.gotoLocationSmooth(center, dura2);
                }
            },dura);
        else
            mMapAdapter.gotoLocationSmooth(center,dura2);
    }

    @Override
    public void zoomMap(float zoom) {
        mMapAdapter.onZoomCtrl(zoom);
    }

    @Override
    public void addMarker(MyLatlng l, String pointID, String usericon, String msgSmallText,String userID){
        mMapAdapter.addMarker(l,pointID,usericon,msgSmallText,userID);
    }

    @Override
    public void showPoint(String msgTitle, String msgText, String msgAlbum, Date time,
                          int msgLikeNum, boolean isLike) {
        mMsgTitle.setText(msgTitle);
        mMsgText.setText(msgText);
        if (msgAlbum.equals(GlobalConst.NO_ALBUM)) {
            mMsgAlbum.setVisibility(View.GONE);
        }
        else {
            mMsgAlbum.setVisibility(View.VISIBLE);
            ImageWrap.albumjust(getActivity(), msgAlbum, mMsgAlbum);
        }
        mMsgTimeshow.setTime(time);
        layoutagaint();
        mPointLikeNum.setText(String.valueOf(msgLikeNum));
        mPointLikeButton.setChecked(isLike, false);
    }


    @Override
    public void showPointUser(String username, String usericon) {
        mMsgUsername.setText(username);
        ImageWrap.iconjust(getActivity(),usericon,mMsgUsericon);
    }

    @Override
    public void showNewpointShine(MyLatlng l, long delay) {
        final PointF p = mMapAdapter.myLatlgnToPoint(l);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyMapIcon.shine_button(getContext(), mMapAdapter, p);
            }
        }, delay);
    }

    boolean isUped = false;
    @Override
    public void upPointShower(MyLatlng l) {
        mMsgScrollView.setScrollY(0);

        if (isUped)
            return;
        isUped = true;

        resizeMsgContainer();

        mFloatingActionButton.hide();

        mMapAdapter.animate().translationY(h2 /2 - self_h/2).setStartDelay(dura).setDuration(dura2).start();
        mPointLiker.setVisibility(View.VISIBLE);
        mCommentLayout.setVisibility(View.VISIBLE);

//        mHeadView.setAlpha(0);
//        mHeadView.animate().alpha(1).setDuration(dura).setStartDelay(dura).start();

        showMsgInnerContainer(true,l);

        MyUtils.setEditTextEditable(mMsgTitle,false);
        MyUtils.setEditTextEditable(mMsgText,false);
    }

    private void showMsgInnerContainer(boolean isShow, MyLatlng l) {
        if (isShow) {
            PointF p = mMapAdapter.myLatlgnToPoint(l);
            int cx = (int) (p.x - mMsgContainer.getLeft());
            int cy = (int) (p.y - mMsgContainer.getTop());
            cx = Math.max(0,cx);
            cy = Math.max(0,cy);
            int dx = Math.max(cx, mMsgInnerContainer.getWidth() - cx);
            int dy = Math.max(cy, mMsgInnerContainer.getHeight() - cy);
            float finalRadius = (float) Math.hypot(dx, dy);

            mMsgInnerContainer.setVisibility(View.VISIBLE);
            Animator animator =
                    ViewAnimationUtils.createCircularReveal(mMsgInnerContainer, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(dura);
            animator.start();
        } else {
            int cx = (mMsgInnerContainer.getLeft()+mMsgInnerContainer.getRight())/2;
            int cy = mMsgInnerContainer.getBottom();
            cx = Math.max(0,cx);
            cy = Math.max(0,cy);
            int dx = Math.max(cx, mMsgInnerContainer.getWidth() - cx);
            int dy = Math.max(cy, mMsgInnerContainer.getHeight() - cy);
            float finalRadius = (float) Math.hypot(dx, dy);

            Animator animator =
                    ViewAnimationUtils.createCircularReveal(mMsgInnerContainer, cx, cy, finalRadius, 0);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(dura);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mMsgInnerContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();

        }
    }

    private void resizeMsgContainer() {
        int lh = View.MeasureSpec.makeMeasureSpec(self_h - h2, View.MeasureSpec.EXACTLY);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mMsgContainer.getLayoutParams();
        lp.height = lh;
        mMsgContainer.setLayoutParams(lp);
    }

    @Override
    public void downPointShower() {
        mMsgScrollView.setScrollY(0);

        if (!isUped)
            return;
        isUped = false;
        showMsgInnerContainer(false, null);

        mFloatingActionButton.show();
        mCommentLayout.setVisibility(View.GONE);

        mMapAdapter.animate().translationY(0).setStartDelay(0).setDuration(dura).start();
        mCommentData.clear();
        mCommentAdapter.notifyDataSetChanged();
    }

    boolean isEditing = false;
    @Override
    public void upPointEditer() {
        if (isEditing)
            return;
        isEditing = true;

        hasAlbum = false;
        albumFullName = "";

        resizeMsgContainer();

        mMapAdapter.animate().translationY(h2 /2 - self_h/2).setStartDelay(dura).setDuration(dura2).start();

        MyUtils.setEditTextEditable(mMsgTitle,true);
        MyUtils.setEditTextEditable(mMsgText,true);

        mFloatingActionButton.hide();
        MyLatlng l = mMapAdapter.pointToMyLatlng(new PointF(mMapView.getWidth()/2,mMapView.getHeight()/2));
        showMsgInnerContainer(true,l);

        mHeadView.setAlpha(0);
        mHeadView.animate().alpha(1).setDuration(dura).setStartDelay(dura).start();

        mToolbar.setVisibility(View.VISIBLE);
        mToolbar.setAlpha(0);
        mToolbar.animate().alpha(1).setDuration(dura).start();

        mMsgTitle.requestFocus();
        mPointLiker.setVisibility(View.GONE);
        mCommentLayout.setVisibility(View.GONE);

        layoutagaint();
    }

    @Override
    public void downPointEditer() {
        if (!isEditing)
            return;
        isEditing = false;
        showMsgInnerContainer(false,null);

        mMapAdapter.animate().translationY(0).setStartDelay(0).setDuration(dura).start();

        mFloatingActionButton.show();
        mCommentLayout.setVisibility(View.GONE);

        mToolbar.animate().alpha(0).setDuration(dura).start();
        MyUtils.setGoneAfterAnimate(mToolbar,mToolbar.animate());
    }

    @Override
    public boolean isUped() {
        return isUped;
    }

    @Override
    public boolean isEditing() {
        return isEditing;
    }

    public void sendNewpoint() {
        int w = mMapView.getWidth();
        int h = mMapView.getHeight();
        MyLatlng center = mMapAdapter.pointToMyLatlng(new PointF(w/2,h/2));

        mPresenter.sendNewpointButton(
                mMsgTitle.getText().toString(),
                mMsgText.getText().toString(),
                albumFullName,
                center,
                hasAlbum
        );
    }

    @Override
    public void showComment(List<UserComment> comments, int commentNum){
        mCommentData.clear();
        mCommentData.addAll(comments);
        mCommentAdapter.notifyDataSetChanged();
        mPointCommentNum.setText(String.valueOf(commentNum));
    }

    @Override
    public void showCommentEmpty(boolean isEmpty) {
        mFooterView.setEmpty(isEmpty);
    }

    @Override
    public void updateComment(UserLikeCommentResult mVar) {
        for (UserComment uc:mCommentData) {
            if (uc.commentID.equals(mVar.commentID)) {
                uc.commentLikeNum = mVar.commentLikeNum;
                //这里只更新点赞数，而是否点赞是View各自向MyUM询问的，Presenter已经让MyUM更新了
                mCommentAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void clearComment() {
        mUserCommentEdit.setText("");
    }

    @Override
    public void updatePoint(int pointLikeNum, boolean isLike) {
        mPointLikeNum.setText(String.valueOf(pointLikeNum));
        mPointLikeButton.setChecked(isLike);
    }


    private void layoutagaint() {
        mHeadView.measure(0,0);
        mMsgScrollView.requestLayout();
    }
}
