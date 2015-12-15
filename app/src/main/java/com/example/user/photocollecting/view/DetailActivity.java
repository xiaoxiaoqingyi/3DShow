package com.example.user.photocollecting.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LruCache;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.photocollecting.R;
import com.example.user.photocollecting.Util.BitmapUtils;
import com.example.user.photocollecting.Util.FileComparator;
import com.example.user.photocollecting.Util.Utils;
import com.example.user.photocollecting.adapter.LibraryListViewAdapter;
import com.example.user.photocollecting.entity.Goods;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<Integer, Bitmap> mMemoryCache;

    private List<String> fileLists = new ArrayList<String>();
    /**
     * 默认显示第一张图片
     */
    private int current_image = 0;
    /**
     * X轴坐标
     */
    private float X = 0;

    private ImageView img;
    /**
     * 间距，每滑动这个间距就换一张图片
     */
    private final static int interval = 30;

    private int down_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initBitmapCache();
        initView();
        initData();
    }

    private void initView(){
        img = (ImageView)findViewById(R.id.img);
    }

    private void initData(){
        String path = getIntent().getStringExtra("path");
        img.setImageBitmap(BitmapUtils.getBitmapFromFilePath(path, Utils.getDisplayWidth(this)/2, Utils.getDisplayWHHeigth(this)/2));
        final String dirName = getIntent().getStringExtra("name");
        new Thread(){
            @Override
            public void run() {
                super.run();
                getFileList(dirName);
            }
        }.start();
    }

    private void getFileList(String name){
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordVideo/";// 存放照片的文件夹
        File savedir = new File(savePath + name);
        if(savedir.exists()){
            File[] fileList = savedir.listFiles();
            for (int i=0; i< fileList.length; i++){
                fileLists.add(fileList[i].getAbsolutePath());
                mMemoryCache.put(i, BitmapUtils.getBitmapFromFile(fileList[i], Utils.getDisplayWidth(this)/2, Utils.getDisplayWHHeigth(this)/2));
            }
//            Collections.sort(mGoodsList, new FileComparator());//通过重写Comparator实现时间排序
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                X = event.getX();
                down_page = current_image;
                break;
            case MotionEvent.ACTION_MOVE:
                slidingHander(event.getX());
                break;
            case MotionEvent.ACTION_UP:
                down_page = current_image;
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 处理滑动时的X坐标改变
     * @param offset_X
     */
    private void slidingHander(float offset_X){
        if((offset_X- X)/interval > 1){
            current_image = down_page - (int)(offset_X- X)/interval;
            if(current_image < 0 ){
                current_image = current_image % fileLists.size() + fileLists.size();
            }

            if(mMemoryCache.get(current_image) != null){
                img.setImageBitmap(mMemoryCache.get(current_image));
            }else if(current_image < fileLists.size()){
                Bitmap bitmap = BitmapUtils.getBitmapFromFilePath(fileLists.get(current_image), Utils.getDisplayWidth(this) / 2,
                        Utils.getDisplayWHHeigth(this) / 2);
                img.setImageBitmap(bitmap);
            }

        }else if((offset_X - X )/interval < -1){
            current_image = down_page + Math.abs((int)(offset_X- X)/interval);
            if(current_image > fileLists.size() - 1){
                current_image = current_image % fileLists.size();
            }
            if(mMemoryCache.get(current_image) != null){
                img.setImageBitmap(mMemoryCache.get(current_image));
            }else if(current_image < fileLists.size()){
                Bitmap bitmap = BitmapUtils.getBitmapFromFilePath(fileLists.get(current_image), Utils.getDisplayWidth(this) / 2,
                        Utils.getDisplayWHHeigth(this) / 2);
                img.setImageBitmap(bitmap);
            }
        }
    }


    public void  initBitmapCache(){
        //获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 4;
        //给LruCache分配1/4 8M
        mMemoryCache = new LruCache<Integer, Bitmap>(mCacheSize){

            //必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

        };

    }

}
