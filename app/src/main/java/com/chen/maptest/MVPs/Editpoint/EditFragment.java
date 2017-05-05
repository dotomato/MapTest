package com.chen.maptest.MVPs.Editpoint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.chen.maptest.ComViews.EdittextSizeChangeEvent;
import com.chen.maptest.R;
import com.chen.maptest.Utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by chen on 17-5-5.
 * Copyright *
 */

public class EditFragment extends Fragment implements EditContract.View, AdapterView.OnItemClickListener, TextWatcher, EdittextSizeChangeEvent.SizeChangeCallback {


    public static final int ALBUMREQ = 11;
    private View mView;
    private Unbinder unbinder;
    private Toolbar mToolbar;
    private EditContract.Presenter mPresenter;

    @BindView(R.id.msgTitle)
    public EditText mMsgTitle;

    @BindView(R.id.msgText)
    public EdittextSizeChangeEvent mMsgText;

    @BindView(R.id.albumView)
    public GridView mAlbumView;

    @BindView(R.id.progressBar)
    public ProgressBar mProgressBar;

    public List<String> mAlbumUrls;
    private AlbumGridViewAdapter mAlbumGridViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        mView = inflater.inflate(R.layout.edit_frag, container, false);
        unbinder = ButterKnife.bind(this, mView);

        initLayout();
//
//        mView.post(new Runnable() {
//            @Override
//            public void run() {
//                MyUtils.pickFromGallery(getActivity(), ALBUMREQ, "选择图片");
//            }
//        });

        return mView;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initLayout() {
        mAlbumUrls = new ArrayList<>();
        mAlbumGridViewAdapter = new AlbumGridViewAdapter(getContext());
        mAlbumView.setAdapter(mAlbumGridViewAdapter);
        mAlbumView.setOnItemClickListener(this);

        mMsgText.addTextChangedListener(this);
        mMsgText.setSizeChangeCallback(this);
    }


    @Override
    public void setPresenter(EditContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void addAlbum(String msgAlbumUrl) {
        mAlbumGridViewAdapter.addData(msgAlbumUrl);
        mAlbumGridViewAdapter.notifyDataSetChanged();
        mView.requestLayout();
    }

    @Override
    public void setContent(String msgTitle, String msgText, List<String> albumUrls) {
        mMsgText.setText(msgText);
        mMsgTitle.setText(msgTitle);
        changeAlbumUrl(albumUrls);
    }

    private void changeAlbumUrl(List<String> albumUrls) {
        mAlbumUrls.clear();
        mAlbumUrls.addAll(albumUrls);
        for (String au:mAlbumUrls){

        }
    }

    @Override
    public String getMsgTitle() {
        return mMsgTitle.getText().toString();
    }

    @Override
    public String getMsgText() {
        return mMsgText.getText().toString();
    }

    @Override
    public List<String> getAlbumUrls() {
        return mAlbumUrls;
    }

    @Override
    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
        if (progress==0)
            mProgressBar.setVisibility(View.INVISIBLE);
        else
            mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void finish() {

    }

    public void commit() {
        mPresenter.commit(getMsgTitle(),getMsgText(),getAlbumUrls());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == mAlbumGridViewAdapter.getCount()-1 ){
            MyUtils.pickFromGallery(getActivity(), ALBUMREQ, "选择图片");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void SizeChangeCallback(int w, int h) {
        mView.requestLayout();
        Log.i("aaaaaaaaa","bbbbbbbbbbbbb");
    }
}
