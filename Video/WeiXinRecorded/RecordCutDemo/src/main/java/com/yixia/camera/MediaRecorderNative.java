package com.yixia.camera;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;

import com.video.lib.FfmpegManager;
import com.video.lib.manager.AudioRecordThread;
import com.video.lib.model.MediaPartModel;

/**
 * 视频录制：边录制边底层处理视频（旋转和裁剪）
 * 
 * @author yixia.com
 *
 */
public class MediaRecorderNative extends MediaRecorderBase implements MediaRecorder.OnErrorListener {

	/** 视频后缀 */
	private static final String VIDEO_SUFFIX = ".ts";

	private int cameraState = 1;

	public MediaRecorderNative(SurfaceHolder surfaceHolder) {
		super(surfaceHolder);
	}

	/** 开始录制 */
	@Override
	public MediaPartModel onRecordStart() {
		MediaPartModel result = null;

		if (mMediaObject != null) {
			mRecording = true;
			result = mMediaObject.buildMediaPart(mCameraId, VIDEO_SUFFIX);
			String cmd = String.format("filename = \"%s\"; ", result.getMediaPath());
			cmd += String.format("addcmd = %s; ", " -vf \"transpose="+cameraState+"\" ");

			FfmpegManager.setParserActionState(cmd, FfmpegManager.PARSER_ACTION_START);
			if (mAudioRecorder == null && result != null) {
				mAudioRecorder = new AudioRecordThread(this);
				mAudioRecorder.start();
			}
		}
		return result;
	}

	/** 切换前置/后置摄像头 */
	public void switchCamera() {
		if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
			switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
			cameraState = 2;
		} else {
			switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
			cameraState = 1;
		}
	}

	/** 停止录制 */
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
            FfmpegManager.v("onError", "onRecordStop IllegalStateException");
        } catch (Exception e) {
            FfmpegManager.v("onError", "onRecordStop Exception");
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
