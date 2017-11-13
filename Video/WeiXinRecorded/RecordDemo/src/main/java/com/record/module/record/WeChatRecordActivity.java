package com.record.module.record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.record.R;
import com.record.RecordApplication;
import com.record.lib.temp.camera.MediaRecorderBase;
import com.record.lib.temp.camera.MediaRecorderNative;
import com.record.module.record.view.CircleTextView;
import com.record.module.record.view.HintTextHelper;
import com.record.module.record.view.RecordProgressView;
import com.record.module.record.view.WeChatProgressDialogHelper;
import com.video.lib.FfmpegManager;
import com.video.lib.model.MediaObject;
import com.video.lib.model.MediaPartModel;

import java.io.File;

/**
 * 这是关键内容
 *
 * @author yline 2017/11/8 -- 19:40
 * @version 1.0.0
 */
public class WeChatRecordActivity extends Activity {
    public static final String VIDEO_PATH = "video_path";
    private final static float OffsetDuration = 25.0f; // 手指取消录制的偏移距离

    // 录制视频类
    private MediaRecorderNative mMediaRecorder; // 录制视频 帮助类
    private MediaObject mMediaObject; // 视频信息
    private SurfaceView mSurfaceView; // 录制视频，对象

    // 简单的帮助类
    private HintTextHelper mHintHelper;
    private CircleTextView mCirclePressTextView;
    private RecordProgressView mRecorderProgress;
    private WeChatProgressDialogHelper mProgressDialogHelper;

    public static void launcherForResult(Activity activity, int requestCode) {
        if (null != activity) {
            Intent intent = new Intent(activity, WeChatRecordActivity.class);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        setContentView(R.layout.activity_wechat_record);

        initView();
        initData();
    }

    private void initView() {
        mSurfaceView = findViewById(R.id.wechat_record_surfaceView);
        mRecorderProgress = findViewById(R.id.wechat_record_progress);
        mCirclePressTextView = findViewById(R.id.wechat_record_press);

        TextView hintTextView = findViewById(R.id.wechat_record_hint);
        mProgressDialogHelper = new WeChatProgressDialogHelper(this);
        mHintHelper = new HintTextHelper(hintTextView);

        initViewClick();
    }

    private void initViewClick() {
        mCirclePressTextView.setOnTouchListener(new View.OnTouchListener() {
            private float sStartY;
            private float sMoveY;
            private boolean sIsCancelRecord; // 上滑取消，tag
            private boolean sIsRecord;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mMediaRecorder == null) {
                    return false;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sStartY = event.getY();

                        sIsCancelRecord = false;
                        mCirclePressTextView.startRecordAnimator();
                        mHintHelper.startRecord();

                        boolean isSuccess = startRecord();
                        if (isSuccess) {
                            mRecorderProgress.startAnimation();
                            sIsRecord = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int durationMove = mMediaObject.getDuration();
                        if (durationMove >= RecordProgressView.MaxTime) {
                            mCirclePressTextView.stopRecordAnimator();
                            mHintHelper.setVisibility(View.INVISIBLE);
                            mRecorderProgress.stopAnimation();
                            sIsRecord = false;
                            stopRecord();
                            return true;
                        }
                        sMoveY = event.getY();

                        float moveDuration = sMoveY - sStartY;
                        if ((moveDuration > 2.0f) && Math.abs(moveDuration) > OffsetDuration) {
                            sIsCancelRecord = false;
                            mHintHelper.slideCancelRecord();
                        }
                        if ((moveDuration < 2.0f) && (Math.abs(moveDuration) > OffsetDuration)) {
                            sIsCancelRecord = true;
                            mHintHelper.releaseRecord();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // 操作结束
                        mCirclePressTextView.stopRecordAnimator();
                        mHintHelper.setVisibility(View.INVISIBLE);
                        mRecorderProgress.stopAnimation();
                        sIsRecord = false;
                        stopRecord();

                        // 上滑取消，则不开始解码
                        if (sIsCancelRecord) {
                            mHintHelper.setVisibility(sIsRecord ? View.VISIBLE : View.INVISIBLE);
                            cancelRecord();
                            return true;
                        }

                        // 时间太短，则不开始解码
                        int duration = mMediaObject.getDuration();
                        if (duration < RecordProgressView.MinTime) {
                            mHintHelper.recordTooShort();
                            cancelRecord();

                            RecordApplication.getHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mHintHelper.setVisibility(sIsRecord ? View.VISIBLE : View.INVISIBLE);
                                }
                            }, 1000);
                            return true;
                        }

                        startEncode();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    private void initData() {
        // 初始化
        FfmpegManager.setParserActionState("", FfmpegManager.PARSER_ACTION_INIT);

        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else {
            mMediaRecorder.prepare();
        }
    }

    /**
     * 用户操作，以开始录制视频
     *
     * @return 是否启动成功
     */
    private boolean startRecord() {
        if (null != mMediaRecorder) {
            MediaPartModel mediaPart = mMediaRecorder.onRecordStart();
            FfmpegManager.v("startRecord", "mediaPart = " + mediaPart);
            return (null != mediaPart);
        }
        return false;
    }

    /**
     * 取消录制视频
     */
    private void cancelRecord() {
        if (mMediaObject != null) {
            mMediaObject.removeAllPart();
        }
    }

    /**
     * 停止录制视频
     */
    private void stopRecord() {
        if (null != mMediaRecorder) {
            mMediaRecorder.onRecordStop();
        }
    }

    /**
     * 停止录制，并且未取消录制；开始编码
     */
    private void startEncode() {
        if (null != mMediaRecorder) {
            mMediaRecorder.startEncoding();
        }
    }

    /**
     * 初始化拍摄SDK
     */
    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderNative(mSurfaceView.getHolder());
        mMediaRecorder.setOnPreparedListener(new MediaRecorderBase.OnPreparedListener() {
            @Override
            public void onPrepared() {
                FfmpegManager.v("onPrepared", "");
            }
        });
        mMediaRecorder.setOnErrorListener(new MediaRecorderBase.OnErrorListener() {
            @Override
            public void onVideoError(int what, int extra) {
                FfmpegManager.v("onVideoError", "what = " + what + ", extra = " + extra);
            }

            @Override
            public void onAudioError(int what, String message) {
                FfmpegManager.v("onAudioError", "what = " + what + ", message = " + message);
            }
        });
        mMediaRecorder.setOnEncodeListener(new MediaRecorderBase.OnEncodeListener() {
            @Override
            public void onEncodeStart() {
                FfmpegManager.v("onEncodeStart", "");
                mProgressDialogHelper.show("", "正在处理中...");
            }

            @Override
            public void onEncodeProgress(int progress) {
                FfmpegManager.v("onEncodeProgress", "progress = " + progress);
            }

            @Override
            public void onEncodeComplete() {
                FfmpegManager.v("onEncodeComplete", "");

                mProgressDialogHelper.dismiss();

                String outputVideoPath = mMediaObject.getOutputVideoPath();
                Intent data = new Intent();
                data.putExtra(VIDEO_PATH, outputVideoPath);
                setResult(RESULT_OK, data);

                FfmpegManager.v("onEncodeComplete", "outputVideoPath = " + outputVideoPath);
                finish();
            }

            @Override
            public void onEncodeError() {
                FfmpegManager.v("onEncodeError", "");

                mProgressDialogHelper.dismiss();
                Toast.makeText(WeChatRecordActivity.this, "视频转码失败", Toast.LENGTH_SHORT).show();
            }
        });

        File f = new File(FfmpegManager.getCachePath());
        if (!checkFile(f)) {
            f.mkdirs();
        }

        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(FfmpegManager.getCachePath(), key);
        mMediaRecorder.prepare();
    }

    /**
     * 检测文件是否可用
     */
    private static boolean checkFile(File f) {
        if (f != null && f.exists() && f.canRead() && (f.isDirectory() || (f.isFile() && f.length() > 0))) {
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mMediaRecorder != null) {
            mMediaRecorder.onRecordStop();
        }
        FfmpegManager.setParserActionState("", FfmpegManager.PARSER_ACTION_FREE);
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mProgressDialogHelper.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (mMediaObject != null) {
            mMediaObject.delete();
        }
        finish();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }
}
