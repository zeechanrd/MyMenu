package com.wuzch.mymenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private MainUI mainUI;
    private LeftMenu leftMenu_fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainUI=new MainUI(this);
        leftMenu_fragment =new LeftMenu(this);
        setContentView(mainUI);
        getSupportFragmentManager().beginTransaction()
                .add(MainUI.LEFT_MENU_ID, leftMenu_fragment).commit();
    }
}
