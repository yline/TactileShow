package com.record.lib.temp.camera;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;

import com.video.lib.manager.AudioRecordThread;
import com.video.lib.model.MediaPartModel;
import com.video.lib.FfmpegManager;

/**
 * 视频录制：边录制边底层处理视频（旋转和裁剪）
 *
 * @author yixia.com
 */
public class MediaRecorderNative extends MediaRecorderBase implements MediaRecorder.OnErrorListener {

    /**
     * 视频后缀
     */
    private static final String VIDEO_SUFFIX = ".ts";

    public MediaRecorderNative(SurfaceHolder surfaceHolder) {
        super(surfaceHolder);
    }

    @Override
    public MediaPartModel onRecordStart() {
        MediaPartModel result = null;

        if (mMediaObject != null) {
            mRecording = true;
            result = mMediaObject.buildMediaPart(mCameraId, VIDEO_SUFFIX);

            String cmd = String.format("filename = %s; ", result.getMediaPath());
            FfmpegManager.setParserActionState(cmd, FfmpegManager.PARSER_ACTION_START);
            if (mAudioRecorder == null) {
                mAudioRecorder = new AudioRecordThread(this);
                mAudioRecorder.start();
            }
        }

        return result;
    }

    @Override
    public void onRecordStop() {
        FfmpegManager.setParserActionState("", FfmpegManager.PARSER_ACTION_STOP);
        super.onRecordStop();
    }

    /**
     * 数据回调
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mRecording) {
            FfmpegManager.executeRenderVideoData(data);
        }
        super.onPreviewFrame(data, camera);
    }

    /**
     * 预览成功，设置视频输入输出参数
     */
    @Override
    protected void onStartPreviewSuccess() {
        FfmpegManager.setInputSetting(MediaRecorderBase.VIDEO_WIDTH, MediaRecorderBase.VIDEO_HEIGHT, mCameraId);
        FfmpegManager.setOutputSetting(MediaRecorderBase.VIDEO_WIDTH, MediaRecorderBase.VIDEO_HEIGHT, mFrameRate, FfmpegManager.OUTPUT_FORMAT_MASK_MP4);
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null) {
                mr.reset();
            }
        } catch (IllegalStateException e) {
            Log.w("Yixia", "onRecordStop", e);
        } catch (Exception e) {
            Log.w("Yixia", "onRecordStop", e);
        }
        if (mOnErrorListener != null) {
            mOnErrorListener.onVideoError(what, extra);
        }
    }

    /**
     * 接收音频数据，传递到底层
     */
    @Override
    public void onRecordAudioReceiving(byte[] sampleBuffer, int len) {
        if (mRecording && len > 0) {
            FfmpegManager.executeRenderAudioData(sampleBuffer);
        }
    }
}
