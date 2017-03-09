package com.chen.maptest;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chen.maptest.MyModel.Userinfo;
import com.chen.maptest.MyModel.Userinfo2;
import com.chen.maptest.MyModel.Userinfo2Result;
import com.chen.maptest.MyModel.UserinfoResult;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MyUpyun.MyUpyunManager;
import com.chen.maptest.MyView.OutlineProvider;
import com.chen.maptest.Utils.MyUtils;
import com.chen.maptest.Utils.UserIconWarp;
import com.yalantis.ucrop.UCrop;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialMenuInflater;

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

    private Userinfo2 tempUserinfo2;

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

        Drawable drawable = MaterialDrawableBuilder.with(this)
                .setIcon(MaterialDrawableBuilder.IconValue.ARROW_LEFT)
                .setColor(Color.WHITE)
                .setSizeDp(25)
                .build();
        mToolbar.setNavigationIcon(drawable);

        OutlineProvider.setOutline(mUsericon,OutlineProvider.SHAPE_OVAL);

        initUserView();
        iconChange = false;
        change = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MaterialMenuInflater
                .with(this)
                .setDefaultColor(Color.WHITE)
                .inflate(R.menu.userinfo_toolbar_menu, menu);
        mMenu = menu;
        setMenuComplete(false);
        change = false;
        return true;
    }

    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.complete:
                if (iconChange)
                    MyUpyunManager.getIns().upload_image("UserIcon",tempIconUri,this);
                else {
                    updateUserinfo();
                }
                break;
            case R.id.useruuid:
                Userinfo nuid = new Userinfo();
                nuid.userDes="please give me a new ID!";
                Myserver.getApi().newuser(nuid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyAction1<Userinfo2Result>() {
                            @Override
                            public void call() {
                                SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("userID", mVar.userinfo.userID);
                                editor.putString("userID2", mVar.userID2);
                                editor.apply();

                                GlobalVar.mUserinfo2.userinfo = mVar.userinfo;
                                GlobalVar.mUserinfo2.userID2 = mVar.userID2;
                                initUserView();
                                setMenuComplete(false);
                                awareUserinfoUpdate();
                                Toast.makeText(UserinfoActivity.this,"更换了uuid",Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
        }
        return true;
    }

    private void setMenuComplete(boolean b) {
        mMenu.findItem(R.id.complete).setVisible(b);
    }

    private void updateUserinfo(){
        Myserver.getApi().updateuser(tempUserinfo2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserinfoResult>() {
                    @Override
                    public void call() {
                        GlobalVar.mUserinfo2.userinfo = mVar.userinfo;
                        awareUserinfoUpdate();
                        UserinfoActivity.this.finish();
                    }
                });
    }

    private void awareUserinfoUpdate() {
        Intent intent = new Intent(GlobalConst.UPDATE_USERINFO_VIEW);
        LocalBroadcastManager.getInstance(UserinfoActivity.this).sendBroadcast(intent);
    }

    private void initUserView(){
        if (GlobalVar.mUserinfo2==null)
            return;
        tempUserinfo2 = MyUtils.pojoCopy(GlobalVar.mUserinfo2);
        ishuman =false;
        mUsername.setText(tempUserinfo2.userinfo.userName);
        mUserdes.setText(tempUserinfo2.userinfo.userDes);
        ishuman =true;
        UserIconWarp.just(this, tempUserinfo2.userinfo.userIcon,mUsericon);
    }


    @Override
    protected void onResume(){
        super.onResume();
    }


    private boolean ishuman=false;
    @OnTextChanged(value={R.id.userdes,R.id.username})
    public void afterTextChanged(CharSequence s, int start, int before, int count) {
        if (mMenu!=null)
            setMenuComplete(true);
        if (!ishuman)
            return;
        tempUserinfo2.userinfo.userName = mUsername.getText().toString();
        tempUserinfo2.userinfo.userDes = mUserdes.getText().toString();
        change=true;
    }


    @OnClick(R.id.usericon)
    public void usericonClick(){
        MyUtils.pickFromGallery(this,ALBUMREQ, "选择头像");
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
                            .withMaxResultSize(128, 128)
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
                setMenuComplete(true);
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
        tempUserinfo2.userinfo.userIcon=url;
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
