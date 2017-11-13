package com.record.lib.temp.ffmpeg.record;

import com.video.lib.model.MediaPartModel;

/**
 * 视频录制接口
 *
 * @author yixia.com
 */
public interface MediaRecordCallback {

    /**
     * 开始录制
     *
     * @return 录制失败返回null
     */
    MediaPartModel onRecordStart();

    /**
     * 停止录制
     */
    void onRecordStop();

    /**
     * 音频错误
     *
     * @param what    错误类型
     * @param message
     */
    void onRecordAudioError(int what, String message);

    /**
     * 接收音频数据
     *
     * @param sampleBuffer 音频数据
     * @param len
     */
    void onRecordAudioReceiving(byte[] sampleBuffer, int len);
}
