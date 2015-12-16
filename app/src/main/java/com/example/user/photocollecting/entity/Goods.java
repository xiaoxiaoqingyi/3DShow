package com.example.user.photocollecting.entity;

import android.graphics.Bitmap;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/2.
 */
public class Goods implements Serializable{

    private File imgFile;
    private String name;
    private long lastModified;
    private Bitmap bitmap;
    private boolean isProcessing;
    private int progress;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void setIsProcessing(boolean isProcessing) {
        this.isProcessing = isProcessing;
    }

    public File getImgFile() {
        return imgFile;
    }

    public void setImgFile(File imgFile) {
        this.imgFile = imgFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
