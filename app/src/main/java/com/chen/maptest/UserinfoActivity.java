package com.chen.maptest;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.maptest.MyModel.UserID;
import com.chen.maptest.MyModel.UserIDResult;
import com.chen.maptest.MyModel.Userinfo;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MyView.OutlineProvider;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserinfoActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.username)
    public EditText mUsername;

    @BindView(R.id.userdes)
    public EditText mUserdes;

    @BindView(R.id.usericon)
    public ImageView mUsericon;

    private Menu mMenu;



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
            public void onClick(View view) {
                UserinfoActivity.this.finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.complete:
                        Userinfo ui = new Userinfo();
                        ui.userID = GlobalVar.mUserinfo.userID;
                        ui.userName = mUsername.getText().toString();
                        ui.userDes = mUserdes.getText().toString();
                        ui.userIcon = "no-icon";

                        Myserver.getApi().updateuser(ui)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new MyAction1<UserIDResult>() {
                                    @Override
                                    public void call() {
                                        GlobalVar.mUserinfo = mVar.userinfo;
                                        initUserView();
                                    }
                                });
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
        });

        OutlineProvider.setOutline(mUsericon,OutlineProvider.SHAPE_OVAL);


    }


    private String genUserID(){
        return UUID.randomUUID().toString();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initUserView();
    }

    private void initUserView(){
        if (GlobalVar.mUserinfo==null)
            return;
        mUsername.setText(GlobalVar.mUserinfo.userName);
        mUserdes.setText(GlobalVar.mUserinfo.userDes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userinfo_toolbar_menu, menu);
        mMenu = menu;
        mMenu.findItem(R.id.complete).setVisible(false);
        return true;
    }



    @OnClick(R.id.usericon)
    public void usericonClick(){
    }

    @OnTextChanged(value={R.id.userdes,R.id.username})
    public void afterTextChanged(CharSequence s, int start, int before, int count) {
        if (mMenu!=null)
            mMenu.findItem(R.id.complete).setVisible(true);
    }
}
