package com.example.user.photocollecting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.user.photocollecting.Util.BitmapUtils;
import com.example.user.photocollecting.Util.FileComparator;
import com.example.user.photocollecting.adapter.LibraryListViewAdapter;
import com.example.user.photocollecting.entity.Goods;
import com.example.user.photocollecting.view.DetailActivity;
import com.example.user.photocollecting.view.RecordVideoActivity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ListView mListView;
    private List<Goods> mGoodsList = new ArrayList<Goods>();
    private LibraryListViewAdapter  mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        initData();
    }

    private  void initView(){
        mListView = (ListView)findViewById(R.id.listview);
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
            //刷新数据
            mGoodsList.clear();
            initData();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera) {
            startActivityForResult(new Intent(this, RecordVideoActivity.class), 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
