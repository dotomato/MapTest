package com.chen.maptest.MVPs.Main;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.chen.maptest.MVPs.Main.Views.MyMapIcon;
import com.chen.maptest.Manager.MyUM;
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
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public class MainFragment extends Fragment implements MainContract.View, MapAdaterCallback {

    public static final int ALBUM_REQUESR_CODE = 10;
    private static final String TAG = "MainFragment";

    final float h2Radio = 1/3.0f;

    public FloatingActionButton mFloatingActionButton;

    public MapAdapterLayout mMapAdapter;

    public VerticalSeekBar mZoombar;

    public ViewGroup mZoomCtrl;

    public View mZoomIn;

    public View mZoomOut;

    public View mRetLocation;

    public ListView mMsgScrollView;

    public EditText mMsgTitle;

    public TextView mMsgUsername;

    public ImageView mMsgUsericon;

    public MyTimeShow mMsgTimeshow;

    public EdittextSizeChangeEvent mMsgText;

    public ImageView mMsgAlbum;

    public ProgressBar mProgressBar;

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

    // TODO: 17-5-5 渐变部分、图片压缩、波浪上升

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

        //消息及评论控件填充

        mHeadView = inflater.inflate(R.layout.main_frag_message,mMsgScrollView,false);
        View emptyView = inflater.inflate(R.layout.main_frag_comment_empty,mMsgScrollView,false);
        View footerView = inflater.inflate(R.layout.main_frag_comment_foorter,mMsgScrollView,false);

        mMsgScrollView.addHeaderView(mHeadView);
        mMsgScrollView.addFooterView(footerView);


        mCommentAdapter = new CommentAdapter(getContext(),mCommentData);
        mMsgScrollView.setAdapter(mCommentAdapter);

        mMsgScrollView.setVisibility(View.GONE);

        //消息内控件初始化
        mMsgTitle = (EditText)mHeadView.findViewById(R.id.msgTitle);
        mMsgUsername = (TextView)mHeadView.findViewById(R.id.msgUsername);
        mMsgUsericon = (ImageView)mHeadView.findViewById(R.id.msgUserIcon);
        mMsgTimeshow = (MyTimeShow)mHeadView.findViewById(R.id.msgTime);
        mMsgText = (EdittextSizeChangeEvent)mHeadView.findViewById(R.id.msgText);
        mMsgAlbum = (ImageView)mHeadView.findViewById(R.id.msgAlbum);
        mProgressBar = (ProgressBar)mHeadView.findViewById(R.id.progressBar2);

        mMsgAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing()){
                    MyUtils.pickFromGallery(getActivity(), ALBUM_REQUESR_CODE,"选择图片");
                }
            }
        });

        ViewTreeObserver vto = mView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                self_w = mView.getWidth();
                self_h = mView.getHeight();
                h2 = (int) (self_h*h2Radio);
                Log.i(TAG,""+self_w+" "+self_h);
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
        mMapAdapter.gotoLocation(latlng,15);
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
    public void setUploadProgress(int progress) {
        mProgressBar.setProgress(progress);
        if (progress==100){
            mProgressBar.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void moveMap(MyLatlng center) {
        mMapAdapter.gotoLocationSmooth(center);
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
    public void showPoint(String msgTitle, String msgText, String msgAlbum, Date time) {
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

    int dura = 300;
    boolean isUped = false;
    @Override
    public void upPointShower() {
        mMsgScrollView.setScrollY(0);

        if (isUped)
            return;
        isUped = true;

        int lh = View.MeasureSpec.makeMeasureSpec(self_h - h2, View.MeasureSpec.EXACTLY);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mMsgScrollView.getLayoutParams();
        lp.height = lh;
        mMsgScrollView.setLayoutParams(lp);

        mFloatingActionButton.hide();

        mMsgScrollView.setVisibility(View.VISIBLE);
        mMapAdapter.animate().translationY(h2 /2 - self_h/2).setDuration(dura).start();
        mMsgScrollView.animate().translationY(-self_h+ h2).setDuration(dura).start();

        MyUtils.setEditTextEditable(mMsgTitle,false);
        MyUtils.setEditTextEditable(mMsgText,false);
    }

    @Override
    public void downPointShower() {
        mMsgScrollView.setScrollY(0);

        if (!isUped)
            return;
        isUped = false;

        mFloatingActionButton.show();

        mMapAdapter.animate().translationY(0).setDuration(dura).start();
        mMsgScrollView.animate().translationY(0).setDuration(dura).start();
        MyUtils.setGoneAfterAnimate(mMsgScrollView,mMsgScrollView.animate());

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

        int lh = View.MeasureSpec.makeMeasureSpec(self_h - h2, View.MeasureSpec.EXACTLY);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mMsgScrollView.getLayoutParams();
        lp.height = lh;
        mMsgScrollView.setLayoutParams(lp);

        mMsgScrollView.setVisibility(View.VISIBLE);
        mMapAdapter.animate().translationY(h2 /2 - self_h/2).setDuration(dura).start();
        mMsgScrollView.animate().translationY(-self_h+ h2).setDuration(dura).start();

        MyUtils.setEditTextEditable(mMsgTitle,true);
        MyUtils.setEditTextEditable(mMsgText,true);

        mFloatingActionButton.hide();

        mToolbar.setVisibility(View.VISIBLE);
        mToolbar.setAlpha(0);
        mToolbar.animate().alpha(1).setDuration(dura).start();

        layoutagaint();
    }

    @Override
    public void downPointEditer() {
        if (!isEditing)
            return;
        isEditing = false;

        mMapAdapter.animate().translationY(0).setDuration(dura).start();
        mMsgScrollView.animate().translationY(0).setDuration(dura).start();
        MyUtils.setGoneAfterAnimate(mMsgScrollView,mMsgScrollView.animate());

        mFloatingActionButton.show();

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

        mPresenter.sendNewpoint(
                mMsgTitle.getText().toString(),
                mMsgText.getText().toString(),
                albumFullName,
                center,
                hasAlbum
        );
    }

    @Override
    public void showComment(List<UserComment> comments){
        mCommentData.clear();
        mCommentData.addAll(comments);
        mCommentAdapter.notifyDataSetChanged();
    }

    public void updateComment(UserLikeCommentResult mVar) {
        for (UserComment uc:mCommentData) {
            if (uc.commentID.equals(mVar.commentID)) {
                uc.commentLikeNum = mVar.commentLikeNum;
                if (mVar.isLike != MyUM.islikecomment(mVar.commentID))
                    MyUM.likecomment(mVar.commentID,mVar.isLike);
                mCommentAdapter.notifyDataSetChanged();
            }
        }
    }


    private void layoutagaint() {
        mHeadView.measure(0,0);
        mMsgScrollView.requestLayout();
    }
}
