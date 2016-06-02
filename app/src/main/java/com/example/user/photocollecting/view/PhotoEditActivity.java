package com.example.user.photocollecting.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.user.photocollecting.R;
import com.example.user.photocollecting.Util.BitmapUtils;
import com.example.user.photocollecting.Util.PhotoEnhance;
import com.example.user.photocollecting.Util.Utils;


public class PhotoEditActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private ImageView img;
    private SeekBar saturationSeekBar, brightnessSeekBar, contrastSeekBar;
    private PhotoEnhance pe;
    private Bitmap bitmapSrc;
    private int pregress = 0;
    private Bitmap bit = null;


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
        bitmapSrc = BitmapUtils.getBitmapFromFilePath(path);
        img.setImageBitmap(bitmapSrc);

        saturationSeekBar = (SeekBar) findViewById(R.id.saturation);
        saturationSeekBar.setMax(255);
        saturationSeekBar.setProgress(128);
        saturationSeekBar.setOnSeekBarChangeListener(this);

        brightnessSeekBar = (SeekBar) findViewById(R.id.brightness);
        brightnessSeekBar.setMax(255);
        brightnessSeekBar.setProgress(128);
        brightnessSeekBar.setOnSeekBarChangeListener(this);

        contrastSeekBar = (SeekBar) findViewById(R.id.contrast);
        contrastSeekBar.setMax(255);
        contrastSeekBar.setProgress(128);
        contrastSeekBar.setOnSeekBarChangeListener(this);

        pe = new PhotoEnhance(bitmapSrc);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            recycle();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        pregress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        int type = 0;

        switch (seekBar.getId())
        {
            case R.id.saturation :
                pe.setSaturation(pregress);
                type = pe.Enhance_Saturation;

                break;
            case R.id.brightness :
                pe.setBrightness(pregress);
                type = pe.Enhance_Brightness;

                break;

            case R.id.contrast :
                pe.setContrast(pregress);
                type = pe.Enhance_Contrast;

                break;

            default :
                break;
        }

        bit = pe.handleImage(type);
        img.setImageBitmap(bit);
    }


    private void recycle()
    {
        if (bitmapSrc != null)
        {
            bitmapSrc.recycle();
            bitmapSrc = null;
        }

        if (bit != null)
        {
            bit.recycle();
            bit = null;
        }
    }
}
