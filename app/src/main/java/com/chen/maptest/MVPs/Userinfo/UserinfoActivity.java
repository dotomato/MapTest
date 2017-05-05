package com.chen.maptest.MVPs.Userinfo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chen.maptest.GlobalConst;
import com.chen.maptest.GlobalVar;
import com.chen.maptest.Manager.MyUM;
import com.chen.maptest.NetDataType.Userinfo2;
import com.chen.maptest.NetDataType.UserinfoResult;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.Manager.MyUpyunManager;
import com.chen.maptest.R;
import com.chen.maptest.ComViews.OutlineProvider;
import com.chen.maptest.Utils.ImageWrap;
import com.chen.maptest.Utils.MyUtils;
import com.yalantis.ucrop.UCrop;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import java.io.File;

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

    private Boolean iconChange;
    private Uri tempIconUri;
    private Boolean change;
    private Userinfo2 tempUserinfo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_act);
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


    private void initUserView(){
        if (!MyUM.isinited())
            return;
        tempUserinfo2 = MyUtils.pojoCopy(MyUM.getui2());
        ishuman =false;
        mUsername.setText(tempUserinfo2.userinfo.userName);
        mUserdes.setText(tempUserinfo2.userinfo.userDes);
        ishuman =true;
        ImageWrap.iconjust(this, tempUserinfo2.userinfo.userIcon,mUsericon);
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
                MyUM.createNewUser(this, new MyUM.UserInitFinish() {
                    @Override
                    public void OnUserInitFinish() {
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
                        GlobalVar.mUserd.ui2.userinfo = mVar.userinfo;
                        awareUserinfoUpdate();
                        UserinfoActivity.this.finish();
                    }
                });
    }

    private void awareUserinfoUpdate() {
        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Intent intent = new Intent(GlobalConst.UPDATE_USERINFO_VIEW);
        LocalBroadcastManager.getInstance(UserinfoActivity.this).sendBroadcast(intent);
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
                            .withMaxResultSize(256, 256)
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
