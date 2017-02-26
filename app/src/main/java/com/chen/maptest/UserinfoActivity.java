package com.chen.maptest;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chen.maptest.MyModel.UserID;
import com.chen.maptest.MyModel.UserIDResult;
import com.chen.maptest.MyModel.Userinfo;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MyUpyun.MyUpyunManager;
import com.chen.maptest.MyView.OutlineProvider;
import com.chen.maptest.Utils.MyUtils;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserinfoActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener, MyUpyunManager.UploadProgress {

    private static final int ALBUMREQ = 1;

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.username)
    public EditText mUsername;

    @BindView(R.id.userdes)
    public EditText mUserdes;

    @BindView(R.id.usericon)
    public ImageView mUsericon;

    private Menu mMenu;

    private Userinfo tempUserinfo;

    private Boolean iconChange;
    private Uri tempIconUri;
    private Boolean change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);

        init();
    }

    private void init(){
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {tryExit();
            }
        });
        mToolbar.setOnMenuItemClickListener(this);

        OutlineProvider.setOutline(mUsericon,OutlineProvider.SHAPE_OVAL);

        Gson gson = new Gson();
        String st = gson.toJson(GlobalVar.mUserinfo);
        tempUserinfo = gson.fromJson(st,Userinfo.class);

        initUserView();
        iconChange = false;
        change = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userinfo_toolbar_menu, menu);
        mMenu = menu;
        mMenu.findItem(R.id.complete).setVisible(false);
        change = false;
        return true;
    }

    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.complete:
                if (iconChange)
                    MyUpyunManager.getIns().upload_image(tempIconUri,this);
                else {
                    updateUserinfo();
                }
                break;
            case R.id.useruuid:
                Toast.makeText(UserinfoActivity.this,"更换了uuid",Toast.LENGTH_SHORT).show();
                SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                String userID = genUserID();
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("userID", userID);
                editor.apply();

                UserID nuid = new UserID();
                nuid.userID=userID;
                Myserver.getApi().newuser(nuid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyAction1<UserIDResult>() {
                            @Override
                            public void call() {
                                SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("userID", mVar.userinfo.userID);
                                editor.apply();

                                GlobalVar.mUserinfo = mVar.userinfo;
                                initUserView();
                            }
                        });
                break;
        }
        return true;
    }

    private void updateUserinfo(){
        Myserver.getApi().updateuser(tempUserinfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserIDResult>() {
                    @Override
                    public void call() {
                        GlobalVar.mUserinfo = mVar.userinfo;
                    }
                });
    }

    private void initUserView(){
        if (GlobalVar.mUserinfo==null)
            return;
        mUsername.setText(GlobalVar.mUserinfo.userName);
        mUserdes.setText(GlobalVar.mUserinfo.userDes);
    }


    private String genUserID(){
        return UUID.randomUUID().toString();
    }


    @Override
    protected void onResume(){
        super.onResume();
    }


    @OnTextChanged(value={R.id.userdes,R.id.username})
    public void afterTextChanged(CharSequence s, int start, int before, int count) {
        if (mMenu!=null)
            mMenu.findItem(R.id.complete).setVisible(true);
        tempUserinfo.userName = mUsername.getText().toString();
        tempUserinfo.userDes = mUserdes.getText().toString();
        change=true;
    }


    @OnClick(R.id.usericon)
    public void usericonClick(){
        MyUtils.pickFromGallery(this,ALBUMREQ);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case ALBUMREQ:
                if (data != null) {
                    Uri imageUri = data.getData();
                    Uri mDestinationUri = Uri.fromFile(new File(getCacheDir(), "UserIcon.jpeg"));

                    UCrop.Options options = new UCrop.Options();
                    options.setCircleDimmedLayer(true);

                    UCrop.of(imageUri, mDestinationUri)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(512, 512)
                            .withOptions(options)
                            .start(this);
                }
                break;

            case (UCrop.REQUEST_CROP):
                final Uri resultUri = UCrop.getOutput(data);
                mUsericon.setImageURI(resultUri);
                iconChange = true;
                tempIconUri = resultUri;
                change=true;
                mMenu.findItem(R.id.complete).setVisible(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgress(float progress) {

    }

    @Override
    public void onComplete(boolean isSuccess,String url) {
        Log.d("MyUpyunManager",url);
        tempUserinfo.userIcon=url;
        updateUserinfo();
    }

    @Override
    public void onBackPressed() {
        tryExit();
    }

    public void tryExit(){
        if (change) {
            new AlertDialog.Builder(this).setMessage("要保存吗？")
                    .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateUserinfo();
                            finish();
                        }
                    })
                    .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNeutralButton("取消", null)
                    .show();//在按键响应事件中显示此对话框
        } else{
            finish();
        }
    }


}
