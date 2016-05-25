package com.example.user.photocollecting.view;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.user.photocollecting.MainActivity;
import com.example.user.photocollecting.R;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RecordVideoActivity extends AppCompatActivity implements View.OnClickListener
                             , SurfaceHolder.Callback, MediaRecorder.OnErrorListener{

    private ProgressBar mProgressbar;
    private SurfaceView mSurfaceView;
    private MediaRecorder mMediaRecorder;// 录制视频的类
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Timer mTimer;// 计时器
    private boolean isOpenCamera = true;// 是否一开始就打开摄像头
    public final static int mRecordMaxTime = 20;// 一次拍摄最长时间
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口
    private int mTimeCount;// 时间计数
    private File mVecordFile = null;// 文件
    private int mWidth = 0;// 视频分辨率宽度
    private int mHeight = 0;// 视频分辨率高度
    private boolean isStarting = false;
    List<int[]> mFpsRange;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        initView();
    }

    private void initView() {
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressbar.setMax(mRecordMaxTime);
        findViewById(R.id.btn_start).setOnClickListener(this);
        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();// 取得holder
        mSurfaceHolder.addCallback(this); // holder加入回调接口
        mSurfaceHolder.setKeepScreenOn(true);
    }

    /**
     * 初始化摄像头
     */
    private void initCamera(int width, int height) {
        if (mCamera != null) {
            freeCameraResource();
        }
        try {
            mCamera = Camera.open();
            if (mCamera == null)
                return;

            mCamera.setDisplayOrientation(90);//摄像头默认是横向，需要调整角度变成竖直
            mCamera.setPreviewDisplay(mSurfaceHolder);
            Camera.Parameters parameters = mCamera.getParameters();// 获得相机参数
            mWidth = width;
            mHeight = height;

            parameters.setPreviewSize(height, width); // 设置预览图像大小
            parameters.set("orientation", "portrait");
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.getParameters().getSupportedPreviewSizes();
            mCamera.getParameters().getSupportedPictureSizes();

            mCamera.setParameters(parameters);// 设置相机参数
            mCamera.startPreview();// 开始预览
            mCamera.unlock();//解锁，赋予录像权限

        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    /**
     * 释放摄像头资源
     */
    private void freeCameraResource() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 录制前，初始化
     */
    private void initRecord() {
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.reset();
            if(mCamera != null)
                mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setOnErrorListener(this);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源

            CamcorderProfile mProfile = null;
            if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
                mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);

            }else if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)){
                mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
            }else {
                mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            }
//            mMediaRecorder.setProfile(mProfile);//不知道为什么，直接这么设置，不起作用，要下面set 才行
            mMediaRecorder.setOutputFormat(mProfile.fileFormat);// 视频输出格式
            mMediaRecorder.setVideoFrameRate(mProfile.fileFormat);// 设置录制的视频帧率
            mMediaRecorder.setVideoSize(mHeight, mWidth);;// 设置分辨率：
            mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);// 设置帧频率，然后就清晰了
            mMediaRecorder.setVideoEncoder(mProfile.videoCodec);// 视频录制格式
            //该设置是为了抽取视频的某些帧，真正录视频的时候，不要设置该参数
            mMediaRecorder.setCaptureRate(mFpsRange.get(0)[0]);//获取最小的每一秒录制的帧数

            mMediaRecorder.setOutputFile(mVecordFile.getAbsolutePath());

            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录制视频
     */
    public void startRecord(final OnRecordFinishListener onRecordFinishListener) {
        isStarting = true;
        this.mOnRecordFinishListener = onRecordFinishListener;
        createRecordDir();
        try {
            initRecord();
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mTimeCount++;
                    mProgressbar.setProgress(mTimeCount);// 设置进度条
                    if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop();
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止拍摄
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        freeCameraResource();

    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        mProgressbar.setProgress(0);
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放资源
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    /**
     * 创建目录与文件
     */
    private void createRecordDir() {
        File FileDir = new File(Environment.getExternalStorageDirectory() + File.separator+"RecordVideo/");
        if (!FileDir.exists()) {
            FileDir.mkdirs();
        }
        // 创建文件
        try {
            mVecordFile = new File(FileDir.getAbsolutePath()+"/test.mp4");
            Log.d("Path:", mVecordFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    OnRecordFinishListener recordFinishListener = new OnRecordFinishListener() {
        @Override
        public void onRecordFinish() {
            Intent intent = new Intent(RecordVideoActivity.this, MainActivity.class);
            intent.putExtra("filePath", mVecordFile.getAbsolutePath());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
       if(!isStarting){
           finish();
       }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                if(!isStarting)
                   startRecord(recordFinishListener);
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (mCamera != null) {
            try {
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(mSurfaceHolder);
                Camera.Parameters parameters = mCamera.getParameters();// 获得相机参数
                mWidth = width;
                mHeight = height;

                parameters.setPreviewSize(height, width); // 设置预览图像大小
                parameters.set("orientation", "portrait");
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes.contains("continuous-video")) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
               mFpsRange =  parameters.getSupportedPreviewFpsRange();

                mCamera.setParameters(parameters);// 设置相机参数
                mCamera.startPreview();// 开始预览
                mCamera.unlock();

            }catch (Exception io){
                io.printStackTrace();
            }

        }

        initCamera(width,height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        freeCameraResource();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 录制完成回调接口
     */
    public interface OnRecordFinishListener {
         void onRecordFinish();
    }

}
