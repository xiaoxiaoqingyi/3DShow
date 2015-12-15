package com.example.user.photocollecting.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import com.example.user.photocollecting.Util.BitmapUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by a123 on 15/12/1.
 */
public class CaptureFrameController {

    private MediaMetadataRetriever retriever = null;
    private String fileLength;
    private File mFile;

    public CaptureFrameController(File file){
        this.mFile = file;
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());
        fileLength = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

    }

    /**
     * 获取视频关键帧
     */
    public void getFrameFromVideo(){
        long lengthLong = Long.parseLong(fileLength) *1000/ 49;
        String prefixName = randomCapital();//图片名前缀
        String photoDir = getPhotoDirName();
        int k = 0;
        for (long i = 0; i< Long.parseLong(fileLength)*1000+lengthLong; i=i+lengthLong){
            k++;
            Bitmap bitmap = retriever.getFrameAtTime(i);
            if(bitmap != null){
                Matrix matrix = new Matrix();
                matrix.reset();
                matrix.setRotate(90);
                if(bitmap.getWidth() >bitmap.getHeight()){
                    bitmap = Bitmap.createBitmap(bitmap,(bitmap.getWidth() - bitmap.getHeight())/2,0, bitmap.getHeight(), bitmap.getHeight(),matrix, true);
                }else{
                    bitmap = Bitmap.createBitmap(bitmap,0,(bitmap.getHeight()-bitmap.getWidth())/2, bitmap.getWidth(), bitmap.getWidth(),matrix, true);
                }

                BitmapUtils.saveBitmap(bitmap, photoDir, prefixName + k + ".png");
            }
        }
        //删除视频文件
        mFile.delete();

    }

    /**
     * 数字时间串
     * @return
     */
    private String getPhotoDirName(){
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 随机5个字母
     * @return
     */
    private String randomCapital(){
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuffer buffer = new StringBuffer();
        for (int i =0; i< 5; i++){
            buffer.append(chars.charAt((int)(Math.random() * 26)));
        }
       return  buffer.toString();
    }

}
