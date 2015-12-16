package com.example.user.photocollecting.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.example.user.photocollecting.Util.BitmapUtils;
import com.example.user.photocollecting.Util.Constants;
import com.example.user.photocollecting.Util.Utils;

import java.io.File;
import java.util.logging.LogRecord;

public class CaptureFrameService extends Service {

    private MediaMetadataRetriever retriever = null;
    private String fileLength;
    private File mFile;

    public CaptureFrameService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(intent != null){
            final String dirName = intent.getStringExtra("dirName");
            final String filePath = intent.getStringExtra("filePath");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getFrameFromVideo(filePath, dirName);
                }
            }).start();

        }

    }

    /**
     * 获取视频关键帧
     */
    public void getFrameFromVideo(String filePath, String dirName){

        mFile = new File(filePath);
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        fileLength = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        long lengthLong = Long.parseLong(fileLength) *1000/ (Constants.Num_Frame - 1);
        String prefixName = Utils.randomCapital(5);//图片名前缀
        int k = 0;
        for (long i = 0; i< Long.parseLong(fileLength)*1000+lengthLong; i=i+lengthLong){
            k++;
            Bitmap bitmap = retriever.getFrameAtTime(i);
            if(bitmap != null){
                Matrix matrix = new Matrix();
                matrix.reset();
                matrix.setRotate(90);//图片默认是横盘的，转90度变竖屏
                if(bitmap.getWidth() >bitmap.getHeight()){
                    bitmap = Bitmap.createBitmap(bitmap,(bitmap.getWidth() - bitmap.getHeight())/2,0, bitmap.getHeight(), bitmap.getHeight(),matrix, true);
                }else{
                    bitmap = Bitmap.createBitmap(bitmap,0,(bitmap.getHeight()-bitmap.getWidth())/2, bitmap.getWidth(), bitmap.getWidth(),matrix, true);
                }
                BitmapUtils.saveBitmap(bitmap, dirName, prefixName + k + ".jpg");
            }

            if(k < 50){
                sendMessageForProgress(dirName,k,true);
            }else {
                sendMessageForProgress(dirName,k,false);
            }

        }
        //删除视频文件
        mFile.delete();
        //停止service
        stopSelf();

    }

    private void sendMessageForProgress(String dirName, int progress, boolean isProgress){
        Intent intent = new Intent(Constants.ACTION_CAPTURE_FRAME);
        intent.putExtra("name", dirName);
        intent.putExtra("isProgress", isProgress);
        intent.putExtra("progress", progress);
        sendBroadcast(intent);
    }


}
