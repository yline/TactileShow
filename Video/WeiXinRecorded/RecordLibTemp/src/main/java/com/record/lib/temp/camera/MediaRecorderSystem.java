package com.record.lib.temp.camera;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;

import com.record.lib.temp.camera.model.MediaPartModel;
import com.record.lib.temp.ffmpeg.FileUtils;
import com.video.lib.FfmpegManager;

import java.io.IOException;

/**
 * 使用系统MediaRecorder录制，适合低端机
 *
 * @author yixia.com
 */
public class MediaRecorderSystem extends MediaRecorderBase implements MediaRecorder.OnErrorListener {

    /**
     * 系统MediaRecorder对象
     */
    private MediaRecorder mMediaRecorder;

    public MediaRecorderSystem(SurfaceHolder surfaceHolder) {
        super(surfaceHolder);
    }

    /**
     * 开始录制
     */
    @Override
    public MediaPartModel onRecordStart() {
        if (mMediaObject != null && mSurfaceHolder != null && !mRecording) {
            MediaPartModel result = mMediaObject.buildMediaPart(mCameraId, ".mp4");

            try {
                if (mMediaRecorder == null) {
                    mMediaRecorder = new MediaRecorder();
                    mMediaRecorder.setOnErrorListener(this);
                } else {
                    mMediaRecorder.reset();
                }

                // Step 1: Unlock and set camera to MediaRecorder
                camera.unlock();
                mMediaRecorder.setCamera(camera);
                mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

                // Step 2: Set sources
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//before setOutputFormat()
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//before setOutputFormat()

                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                //设置视频输出的格式和编码
                CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
                //                mMediaRecorder.setProfile(mProfile);
                mMediaRecorder.setVideoSize(640, 480);//after setVideoSource(),after setOutFormat()
                mMediaRecorder.setAudioEncodingBitRate(44100);
                if (mProfile.videoBitRate > 2 * 1024 * 1024) {
                    mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
                } else {
                    mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
                }
                mMediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);//after setVideoSource(),after setOutFormat()

                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//after setOutputFormat()
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//after setOutputFormat()

                //mMediaRecorder.setVideoEncodingBitRate(800);

                // Step 4: Set output file
                mMediaRecorder.setOutputFile(result.getMediaPath());

                // Step 5: Set the preview output
                //				mMediaRecorder.setOrientationHint(90);//加了HTC的手机会有问题

                Log.e("Yixia", "OutputFile:" + result.getMediaPath());

                mMediaRecorder.prepare();
                mMediaRecorder.start();
                mRecording = true;
                return result;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.e("Yixia", "startRecord", e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Yixia", "startRecord", e);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Yixia", "startRecord", e);
            }
        }
        return null;
    }

    /**
     * 停止录制
     */
    @Override
    public void onRecordStop() {
        long endTime = System.currentTimeMillis();
        if (mMediaRecorder != null) {
            //设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                Log.w("Yixia", "onRecordStop", e);
            } catch (RuntimeException e) {
                Log.w("Yixia", "onRecordStop", e);
            } catch (Exception e) {
                Log.w("Yixia", "onRecordStop", e);
            }
        }

        if (camera != null) {
            try {
                camera.lock();
            } catch (RuntimeException e) {
                Log.e("Yixia", "onRecordStop", e);
            }
        }

        // 判断数据是否处理完，处理完了关闭输出流
        if (mMediaObject != null) {
            MediaPartModel part = mMediaObject.getCurrentPart();
            if (part != null && part.isRecording()) {
                part.setRecording(false);
                part.setEndTime(endTime);
                part.setDuration((int) (part.getEndTime() - part.getStartTime()));
                part.setCutStartTime(0);
                part.setCutEndTime(part.getDuration());
            }
        }
        mRecording = false;
    }

    /**
     * 释放资源
     */
    @Override
    public void release() {
        super.release();
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                Log.w("Yixia", "onRecordStop", e);
            } catch (Exception e) {
                Log.w("Yixia", "onRecordStop", e);
            }
        }
        mMediaRecorder = null;
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
     * 不需要视频数据回调
     */
    @Override
    protected void setPreviewCallback() {
        //super.setPreviewCallback();
    }

    /**
     * 合并视频文件
     */
    @Override
    protected void concatVideoParts() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                String cmd = "";
                int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                //将mp4转成ts
                for (int i = 0, j = mMediaObject.getMedaParts().size(); i < j; i++) {
                    MediaPartModel part = mMediaObject.getMedaParts().get(i);
                    if (FileUtils.checkFile(part.getMediaPath())) {
                        String ts = part.getMediaPath().replace(".mp4", ".ts");
                        FileUtils.deleteFile(ts);//删除
                        cameraId = part.getCameraId();

                        cmd = String.format(FfmpegManager.COMMAND_MODIFY_SUFFIX, FfmpegManager.getLogPathCommand(), part.getMediaPath(), ts);
                        if (FfmpegManager.executeCommand("", cmd) == FfmpegManager.COMMAND_RESULT_SUCCESS) {
                            part.setMediaPath(ts); //修改后缀名
                            continue;
                        }
                    }
                    //文件不存在或者转码失败，直接跳过
                    part.setMediaPath("");
                }

                //处理翻转信息
                String vf = cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? "transpose=1" : "transpose=2,hflip";
                //合并ts流
                cmd = String.format(FfmpegManager.COMMAND_MERGE_VIDEO, FfmpegManager.getLogPathCommand(), mMediaObject.getConcatYUV(), vf, FfmpegManager.COMMAND_PARAM_VIDEO_CODE, mMediaObject.getOutputTempVideoPath());
                return FfmpegManager.executeCommand("", cmd) == FfmpegManager.COMMAND_RESULT_SUCCESS;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    mEncodeHanlder.sendEmptyMessage(MESSAGE_ENCODE_COMPLETE);
                } else {
                    mEncodeHanlder.sendEmptyMessage(MESSAGE_ENCODE_ERROR);
                }
            }
        }.execute();
    }
}
