package com.wuzch.mymenu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/12/2.
 */

public class LeftMenu extends Fragment {
    private Button btn_text;
    private Context context;

    public LeftMenu(Context context){
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.left_menu_layout_fragment,container,false);
        btn_text= (Button) view.findViewById(R.id.btn_text);
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast imageToast=Toast.makeText(context,"带有图形的Toast",Toast.LENGTH_LONG);
                ImageView imageView=new ImageView(context);
                imageView.setImageResource(R.mipmap.ic_launcher);
                imageToast.setView(imageView);
                imageToast.show();
            }
        });
        return view;
    }
}
