package com.video.lib.manager;

import android.media.AudioFormat;
import android.media.AudioRecord;

/**
 * 音频录制
 *
 * @author yline 2017/11/13 -- 11:44
 * @version 1.0.0
 */
public class AudioRecordThread extends Thread {
    public static final int AUDIO_RECORD_ERROR_UNKNOWN = 0; // 不明原因
    public static final int AUDIO_RECORD_ERROR_SAMPLERATE_NOT_SUPPORT = 1; // 采样率设置不支持
    public static final int AUDIO_RECORD_ERROR_GET_MIN_BUFFER_SIZE_NOT_SUPPORT = 2; // 最小缓存获取失败
    public static final int AUDIO_RECORD_ERROR_CREATE_FAILED = 3; // 创建AudioRecord失败

    private int mSampleRate = 44100; // 采样率
    private AudioRecord mAudioRecord = null;
    private MediaRecordCallback mMediaRecorder;

    public AudioRecordThread(MediaRecordCallback mediaRecorder) {
        this.mMediaRecorder = mediaRecorder;
    }

    /**
     * 设置采样率
     */
    public void setSampleRate(int sampleRate) {
        this.mSampleRate = sampleRate;
    }

    @Override
    public void run() {
        if (mSampleRate != 8000 && mSampleRate != 16000 && mSampleRate != 22050 && mSampleRate != 44100) {
            mMediaRecorder.onRecordAudioError(AUDIO_RECORD_ERROR_SAMPLERATE_NOT_SUPPORT, "sampleRate not support.");
            return;
        }

        final int mMinBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        if (AudioRecord.ERROR_BAD_VALUE == mMinBufferSize) {
            mMediaRecorder.onRecordAudioError(AUDIO_RECORD_ERROR_GET_MIN_BUFFER_SIZE_NOT_SUPPORT, "parameters are not supported by the hardware.");
            return;
        }

        mAudioRecord = new AudioRecord(android.media.MediaRecorder.AudioSource.MIC, mSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mMinBufferSize);
        if (null == mAudioRecord) {
            mMediaRecorder.onRecordAudioError(AUDIO_RECORD_ERROR_CREATE_FAILED, "new AudioRecord failed.");
            return;
        }

        try {
            mAudioRecord.startRecording();
        } catch (IllegalStateException e) {
            mMediaRecorder.onRecordAudioError(AUDIO_RECORD_ERROR_UNKNOWN, "startRecording failed.");
            return;
        }

        byte[] sampleBuffer = new byte[mMinBufferSize];

        try {
            while (!Thread.currentThread().isInterrupted()) {
                int result = mAudioRecord.read(sampleBuffer, 0, mMinBufferSize);
                if (result > 0) {
                    mMediaRecorder.onRecordAudioReceiving(sampleBuffer, result);
                }
            }
        } catch (Exception e) {
            mMediaRecorder.onRecordAudioError(AUDIO_RECORD_ERROR_UNKNOWN, e.getMessage());
        }

        mAudioRecord.release();
        mAudioRecord = null;
    }
}
