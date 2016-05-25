
package com.example.user.photocollecting.view;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.user.photocollecting.R;
import com.example.user.photocollecting.Util.BitmapUtils;
import com.example.user.photocollecting.Util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnimationListActivity extends AppCompatActivity {


    private ImageView img;
    private List<String> fileLists = new ArrayList<String>();
    private AnimationDrawable animationDrawable = new AnimationDrawable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_list);
        initView();
        initData();
    }

    private void initView(){
        img = (ImageView)findViewById(R.id.img);


    }

    private void initData(){
        String path = getIntent().getStringExtra("path");
        img.setImageBitmap(BitmapUtils.getBitmapFromFilePath(path, Utils.getDisplayWidth(this) / 2, Utils.getDisplayWHHeigth(this) / 2));
        final String dirName = getIntent().getStringExtra("name");
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
                getFileList(dirName);
//            }
//        }.start();
    }

    private void getFileList(String name){
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordVideo/";// 存放照片的文件夹
        File savedir = new File(savePath + name);
        if(savedir.exists()){
            File[] fileList = savedir.listFiles();
            for (int i=0; i< fileList.length; i++){
                if(i == 30)
                    break;
                fileLists.add(fileList[i].getAbsolutePath());
                animationDrawable.addFrame(new BitmapDrawable(fileList[i].getAbsolutePath()), 100);
            }
        }
        img.setImageDrawable(animationDrawable);
        animationDrawable.start();

    }
}
