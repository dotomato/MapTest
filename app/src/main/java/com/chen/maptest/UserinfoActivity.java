package com.chen.maptest;

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

import com.chen.maptest.MyView.OutlineProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

public class UserinfoActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userinfo_toolbar_menu, menu);
        mMenu = menu;
        return true;
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
                        break;
                    case R.id.useruuid:
                        break;
                }
                return true;
            }
        });

        OutlineProvider.setOutline(mUsericon,OutlineProvider.SHAPE_OVAL);
    }

    @OnClick(R.id.usericon)
    public void usericonClick(){
        mMenu.findItem(R.id.complete).setVisible(false);
    }

    @OnTextChanged(value={R.id.userdes,R.id.username})
    public void afterTextChanged(CharSequence s, int start, int before, int count) {
        mMenu.findItem(R.id.complete).setVisible(true);
    }
}
