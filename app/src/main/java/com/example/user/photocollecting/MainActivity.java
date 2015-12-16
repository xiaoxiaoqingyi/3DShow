package com.example.user.photocollecting;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.user.photocollecting.Util.BitmapUtils;
import com.example.user.photocollecting.Util.Constants;
import com.example.user.photocollecting.Util.FileComparator;
import com.example.user.photocollecting.Util.Utils;
import com.example.user.photocollecting.adapter.LibraryListViewAdapter;
import com.example.user.photocollecting.entity.Goods;
import com.example.user.photocollecting.service.CaptureFrameService;
import com.example.user.photocollecting.view.RecordVideoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ListView mListView;
    private List<Goods> mGoodsList = new ArrayList<Goods>();
    private LibraryListViewAdapter  mAdapter;
    private MyBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        initData();
        registerBroadCast();
    }

    private  void initView(){
        mListView = (ListView)findViewById(R.id.listview);
    }

    private void registerBroadCast(){
        broadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_CAPTURE_FRAME);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void initData(){
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordVideo/";// 存放照片的文件夹
        File savedir = new File(savePath);
        if(savedir.exists()){
            File[] fileList = savedir.listFiles();
            for (int i=0; i< fileList.length; i++){
                if(fileList[i].isDirectory()){
                    Goods goods = new Goods();
                    goods.setName(fileList[i].getName());
                    goods.setLastModified(fileList[i].lastModified());

                    if(fileList[i].listFiles().length > 0){
                        goods.setImgFile(fileList[i].listFiles()[0]);
                        goods.setBitmap(BitmapUtils.getBitmapFromFile(goods.getImgFile(),400,400));
                    }
                    mGoodsList.add(goods);
                }
            }
            Collections.sort(mGoodsList, new FileComparator());//通过重写Comparator实现时间排序
        }

        mAdapter = new LibraryListViewAdapter(MainActivity.this, mGoodsList, mListView);
        mListView.setAdapter(mAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == 0){

            String filePath = data.getStringExtra("filePath");
            String dirName = Utils.getDateNumber();

            Intent service = new Intent(MainActivity.this, CaptureFrameService.class);
            service.putExtra("filePath", filePath);
            service.putExtra("dirName", dirName);
            startService(service);

            Goods goods = new Goods();
            goods.setName(dirName);
            goods.setIsProcessing(true);
            mGoodsList.add(0, goods);
            mAdapter.setmGoodsList(mGoodsList);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_camera) {
            startActivityForResult(new Intent(this, RecordVideoActivity.class), 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                String dirName = intent.getStringExtra("name");
                boolean isProgress = intent.getBooleanExtra("isProgress", false);
                int progress = intent.getIntExtra("progress",0);
                for (Goods goods : mGoodsList){
                    if(goods.getName().equals(dirName)){
                        goods.setIsProcessing(isProgress);
                        goods.setProgress(progress);
                        if(!isProgress){ //说明已经捕获最后一张帧了
                            String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + "/RecordVideo/" + dirName;// 存放照片的文件夹
                            File savedir = new File(savePath);
                            goods.setImgFile(savedir.listFiles()[0]);
                            goods.setBitmap(BitmapUtils.getBitmapFromFile(goods.getImgFile(),400,400));
                        }
                        mAdapter.setmGoodsList(mGoodsList);
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    }

}
