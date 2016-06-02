package com.example.user.photocollecting.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.user.photocollecting.R;
import com.example.user.photocollecting.Util.BitmapUtils;
import com.example.user.photocollecting.Util.Utils;


public class PhotoEditActivity extends AppCompatActivity {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        init();

    }

    private void init(){
        img = (ImageView)findViewById(R.id.img);
        String path = getIntent().getStringExtra("path");
        img.setImageBitmap(BitmapUtils.getBitmapFromFilePath(path, Utils.getDisplayWidth(this) / 2, Utils.getDisplayWHHeigth(this) / 2));
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

}
