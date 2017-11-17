package com.yline.record.module.main;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.video.lib.FfmpegManager;
import com.video.lib.manager.MediaRecorderNativeCut;
import com.video.lib.model.MediaObject;
import com.video.lib.model.MediaPartModel;
import com.yixia.videoeditor.adapter.UtilityAdapter;
import com.yline.base.BaseActivity;
import com.yline.record.IApplication;
import com.yline.record.R;
import com.yline.record.module.editor.EditVideoActivity;
import com.yline.record.view.AbstractViewHolder;
import com.yline.record.view.FocusSurfaceView;
import com.yline.record.view.MediaTextureView;
import com.yline.record.view.RecordedButton;
import com.yline.record.viewhelper.DialogHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 仿新版微信录制视频
 * 基于ffmpeg视频编译
 *
 * @author yline 2017/11/14 -- 14:06
 * @version 1.0.0
 */
public class MainActivity extends BaseActivity {
    private static final int REQUEST_KEY = 100;
    private static final int HANDLER_RECORD = 200;
    private static final int HANDLER_EDIT_VIDEO = 201;

    private MediaRecorderNativeCut mMediaRecorder;
    private MediaObject mMediaObject;
    private RecordedButton rb_start;
    private RelativeLayout rl_bottom;
    private TextView tv_hint;
    private MediaTextureView vv_play;

    //最大录制时间
    private int maxDuration = 8000;
    //本次段落是否录制完成
    private boolean isRecordedOver;
    private List<Integer> cameraTypeList = new ArrayList<>();

    // View
    private DialogHelper mDialogHelper;
    private AbstractViewHolder mViewHolder;

    private MainRecordManager mRecordManager;
    private MainTextureManager mTextureManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initView();

        mRecordManager = new MainRecordManager(findViewById(R.id.main_rl_record));
        mRecordManager.setOnRecordClickCallback(new MainRecordManager.OnMainRecordClickCallback() {
            @Override
            public boolean onRecordFlashSwitch() {
                return mMediaRecorder.changeFlash(MainActivity.this);
            }

            @Override
            public void onRecordCameraSwitch() {
                mMediaRecorder.switchCamera();

                mRecordManager.setFlashImageClose();
            }

            @Override
            public int onRecordDeleteClick() {
                ImageView backImageView = mViewHolder.get(R.id.main_iv_back);

                if (rb_start.isDeleteMode()) {//判断是否要删除视频段落
                    MediaPartModel lastPart = mMediaObject.getPart(mMediaObject.getMediaParts().size() - 1);
                    mMediaObject.removePart(lastPart, true);
                    rb_start.setProgress(mMediaObject.getDuration());
                    rb_start.deleteSplit();
                    if (cameraTypeList.size() > 0) {
                        cameraTypeList.remove(cameraTypeList.size() - 1);
                    }
                    changeButton(mMediaObject.getMediaParts().size() > 0);
                    backImageView.setImageResource(R.mipmap.video_delete);
                } else if (mMediaObject.getMediaParts().size() > 0) {
                    rb_start.setDeleteMode(true);
                    backImageView.setImageResource(R.mipmap.video_delete_click);
                }
                return 0;
            }

            @Override
            public void onRecordFinishClick() {
                videoFinish();
            }
        });

        mTextureManager = new MainTextureManager(findViewById(R.id.main_rl_texture));
        mTextureManager.setOnMainTextureCallback(new MainTextureManager.OnMainTextureCallback() {
            @Override
            public void onTextureFinishClick() {
                rb_start.setDeleteMode(false);
                Intent intent = new Intent(MainActivity.this, EditVideoActivity.class);
                intent.putExtra("path", IApplication.VIDEO_PATH + "/finish.mp4");
                startActivityForResult(intent, REQUEST_KEY);
            }

            @Override
            public void onTextureCloseClick() {
                initMediaRecorderState();
            }
        });

        vv_play = (MediaTextureView) findViewById(R.id.vv_play);

        tv_hint = (TextView) findViewById(R.id.tv_hint);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);

        rb_start = (RecordedButton) findViewById(R.id.rb_start);
        rb_start.setMax(maxDuration);
        rb_start.setOnGestureListener(new RecordedButton.OnGestureListener() {
            @Override
            public void onLongClick() {
                isRecordedOver = false;
                mMediaRecorder.onRecordStart();
                rb_start.setSplit();
                myHandler.sendEmptyMessageDelayed(HANDLER_RECORD, 100);
                cameraTypeList.add(mMediaRecorder.getCameraType());
            }

            @Override
            public void onClick() {

            }

            @Override
            public void onLift() {
                isRecordedOver = true;
                mMediaRecorder.onRecordStop();
                changeButton(mMediaObject.getMediaParts().size() > 0);
            }

            @Override
            public void onOver() {
                isRecordedOver = true;
                rb_start.closeButton();
                mMediaRecorder.onRecordStop();
                videoFinish();
            }
        });
    }

    private void initView() {
        mViewHolder = new AbstractViewHolder(this);
        mDialogHelper = new DialogHelper(this);

        FocusSurfaceView surfaceView = findViewById(R.id.main_focus_surface_view);

        mMediaRecorder = new MediaRecorderNativeCut(surfaceView.getHolder());

        //设置缓存文件夹
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(FfmpegManager.getCachePath(), key);
        //准备
        mMediaRecorder.prepare();
        //滤波器相关
        UtilityAdapter.freeFilterParser();
        UtilityAdapter.initFilterParser();

        surfaceView.setTouchFocus(mMediaRecorder);
    }

    private void changeButton(boolean flag) {
        if (flag) {
            tv_hint.setVisibility(View.VISIBLE);
            rl_bottom.setVisibility(View.VISIBLE);
        } else {
            tv_hint.setVisibility(View.GONE);
            rl_bottom.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化视频拍摄状态
     */
    private void initMediaRecorderState() {
        vv_play.setVisibility(View.GONE);
        vv_play.pause();

        rb_start.setVisibility(View.VISIBLE);
        mTextureManager.setRelativeVisibility(View.GONE);
        changeButton(false);
        tv_hint.setVisibility(View.VISIBLE);

        LinkedList<MediaPartModel> list = new LinkedList<>();
        list.addAll(mMediaObject.getMediaParts());

        for (MediaPartModel part : list) {
            mMediaObject.removePart(part, true);
        }

        rb_start.setProgress(mMediaObject.getDuration());
        rb_start.cleanSplit();
    }

    private void videoFinish() {
        changeButton(false);
        rb_start.setVisibility(View.GONE);

        mDialogHelper.show();

        myHandler.sendEmptyMessage(HANDLER_EDIT_VIDEO);
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_RECORD://拍摄视频的handler
                    if (!isRecordedOver) {
                        if (rl_bottom.getVisibility() == View.VISIBLE) {
                            changeButton(false);
                        }
                        rb_start.setProgress(mMediaObject.getDuration());
                        myHandler.sendEmptyMessageDelayed(HANDLER_RECORD, 30);
                    }
                    break;
                case HANDLER_EDIT_VIDEO://合成视频的handler
                    int progress = UtilityAdapter.FilterParserAction("", UtilityAdapter.PARSERACTION_PROGRESS);
                    mDialogHelper.setText("视频编译中 " + progress + "%");
                    if (progress == 100) {
                        syntVideo();
                    } else if (progress == -1) {
                        mDialogHelper.dismiss();
                        Toast.makeText(getApplicationContext(), "视频合成失败", Toast.LENGTH_SHORT).show();
                    } else {
                        sendEmptyMessageDelayed(HANDLER_EDIT_VIDEO, 30);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 合成视频
     */
    private void syntVideo() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                mDialogHelper.setText("视频合成中 ");
            }

            @Override
            protected String doInBackground(Void... params) {

                List<String> pathList = new ArrayList<>();
                for (int x = 0; x < mMediaObject.getMediaParts().size(); x++) {
                    MediaPartModel mediaPart = mMediaObject.getMediaParts().get(x);

                    String mp4Path = IApplication.VIDEO_PATH + "/" + x + ".mp4";
                    List<String> list = new ArrayList<>();
                    list.add(mediaPart.getMediaPath());
                    tsToMp4(list, mp4Path);
                    pathList.add(mp4Path);
                }

                List<String> tsList = new ArrayList<>();
                for (int x = 0; x < pathList.size(); x++) {
                    String path = pathList.get(x);
                    String ts = IApplication.VIDEO_PATH + "/" + x + ".ts";
                    mp4ToTs(path, ts);
                    tsList.add(ts);
                }

                String output = IApplication.VIDEO_PATH + "/finish.mp4";
                boolean flag = tsToMp4(tsList, output);
                if (!flag) {
                    output = "";
                }
                deleteDirRoom(new File(IApplication.VIDEO_PATH), output);
                return output;
            }

            @Override
            protected void onPostExecute(String result) {
                mDialogHelper.dismiss();
                if (!TextUtils.isEmpty(result)) {
                    mTextureManager.setRelativeVisibility(View.VISIBLE);
                    vv_play.setVisibility(View.VISIBLE);

                    vv_play.setVideoPath(result);
                    vv_play.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            vv_play.setLooping(true);
                            vv_play.start();
                        }
                    });

                    vv_play.setLooping(true);
                    vv_play.start();
                } else {
                    Toast.makeText(getApplicationContext(), "视频合成失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    /**
     * 删除文件夹下所有文件, 只保留一个
     *
     * @param fileName 保留的文件名称
     */
    private static void deleteDirRoom(File dir, String fileName) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                deleteDirRoom(f, fileName);
            }
        } else if (dir.exists()) {
            if (!dir.getAbsolutePath().equals(fileName)) {
                dir.delete();
            }
        }
    }

    public void mp4ToTs(String path, String output) {
        //./ffmpeg -i 0.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts ts0.ts
        StringBuilder sb = new StringBuilder("ffmpeg");
        sb.append(" -i");
        sb.append(" " + path);
        sb.append(" -c");
        sb.append(" copy");
        sb.append(" -bsf:v");
        sb.append(" h264_mp4toannexb");
        sb.append(" -f");
        sb.append(" mpegts");
        sb.append(" " + output);

        int i = UtilityAdapter.FFmpegRun("", sb.toString());
    }

    public boolean tsToMp4(List<String> path, String output) {
        //ffmpeg -i "concat:ts0.ts|ts1.ts|ts2.ts|ts3.ts" -c copy -bsf:a aac_adtstoasc out2.mp4

        StringBuilder sb = new StringBuilder("ffmpeg");
        sb.append(" -i");
        String concat = "concat:";
        for (String part : path) {
            concat += part;
            concat += "|";
        }
        concat = concat.substring(0, concat.length() - 1);
        sb.append(" " + concat);
        sb.append(" -c");
        sb.append(" copy");
        sb.append(" -bsf:a");
        sb.append(" aac_adtstoasc");
        sb.append(" -y");
        sb.append(" " + output);

        int i = UtilityAdapter.FFmpegRun("", sb.toString());
        return i == 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaRecorder.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaRecorder.stopPreview();
        mRecordManager.setFlashImageClose();
    }

    @Override
    public void onBackPressed() {
        if (rb_start.getSplitCount() == 0) {
            super.onBackPressed();
        } else {
            initMediaRecorderState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMediaObject.cleanTheme();
        mMediaRecorder.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_KEY) {
                initMediaRecorderState();
            }
        }
    }
}
