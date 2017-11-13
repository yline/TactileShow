package com.video.lib.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * 播放媒体，包含信息，结构体
 *
 * @author yline 2017/11/10 -- 11:56
 * @version 1.0.0
 */
public class MediaPartModel implements Serializable{
    public final static int MEDIA_PART_TYPE_RECORD = 0; // 拍摄

    private int index; // 索引

    private String mediaPath; // 视频路径
    private String audioPath; // 音频路径
    private String tempMediaPath; // 临时视频路径
    private String tempAudioPath; // 临时音频路径
    private String thumbPath; // 截图路径
    private String tempPath; // 存放导入的视频和图片

    private int type; // 类型
    private int cutStartTime; // 剪切视频（开始时间）
    private int cutEndTime; // 剪切视频（结束时间）
    private int duration; // 分段长度
    private int position; // 总时长中的具体位置

    private int speed; // 0.2倍速-3倍速（取值2~30）
    private int cameraId; // 摄像头
    private int yuvWidth; // 视频尺寸
    private int yuvHeight; // 视频高度

    // 非序列化变量
    private transient long startTime;
    private transient long endTime;
    private transient FileOutputStream mCurrentOutputVideo;
    private transient FileOutputStream mCurrentOutputAudio;
    private transient volatile boolean recording;

    public MediaPartModel() {
        this.type = MEDIA_PART_TYPE_RECORD;
        this.speed = 10;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getTempMediaPath() {
        return tempMediaPath;
    }

    public void setTempMediaPath(String tempMediaPath) {
        this.tempMediaPath = tempMediaPath;
    }

    public String getTempAudioPath() {
        return tempAudioPath;
    }

    public void setTempAudioPath(String tempAudioPath) {
        this.tempAudioPath = tempAudioPath;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCutStartTime() {
        return cutStartTime;
    }

    public void setCutStartTime(int cutStartTime) {
        this.cutStartTime = cutStartTime;
    }

    public int getCutEndTime() {
        return cutEndTime;
    }

    public void setCutEndTime(int cutEndTime) {
        this.cutEndTime = cutEndTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration > 0 ? duration : (int) (System.currentTimeMillis() - startTime);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public int getYuvWidth() {
        return yuvWidth;
    }

    public void setYuvWidth(int yuvWidth) {
        this.yuvWidth = yuvWidth;
    }

    public int getYuvHeight() {
        return yuvHeight;
    }

    public void setYuvHeight(int yuvHeight) {
        this.yuvHeight = yuvHeight;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public FileOutputStream getmCurrentOutputVideo() {
        return mCurrentOutputVideo;
    }

    public void setmCurrentOutputVideo(FileOutputStream mCurrentOutputVideo) {
        this.mCurrentOutputVideo = mCurrentOutputVideo;
    }

    public FileOutputStream getmCurrentOutputAudio() {
        return mCurrentOutputAudio;
    }

    public void setmCurrentOutputAudio(FileOutputStream mCurrentOutputAudio) {
        this.mCurrentOutputAudio = mCurrentOutputAudio;
    }

    public void delete() {
        deleteFile(mediaPath);
        deleteFile(audioPath);
        deleteFile(thumbPath);
        deleteFile(tempMediaPath);
        deleteFile(tempAudioPath);
    }

    private static boolean deleteFile(String f) {
        if (f != null && f.length() > 0) {
            return deleteFile(new File(f));
        }
        return false;
    }

    private static boolean deleteFile(File f) {
        if (f != null && f.exists() && !f.isDirectory()) {
            return f.delete();
        }
        return false;
    }

    /**
     * 写入音频数据
     */
    public void writeAudioData(byte[] buffer) throws IOException {
        if (mCurrentOutputAudio != null) {
            mCurrentOutputAudio.write(buffer);
        }
    }

    /**
     * 写入视频数据
     */
    public void writeVideoData(byte[] buffer) throws IOException {
        if (mCurrentOutputVideo != null) {
            mCurrentOutputVideo.write(buffer);
        }
    }

    public void prepare() {
        try {
            mCurrentOutputVideo = new FileOutputStream(mediaPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mCurrentOutputAudio = new FileOutputStream(audioPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mCurrentOutputVideo != null) {
            try {
                mCurrentOutputVideo.flush();
                mCurrentOutputVideo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCurrentOutputVideo = null;
        }

        if (mCurrentOutputAudio != null) {
            try {
                mCurrentOutputAudio.flush();
                mCurrentOutputAudio.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCurrentOutputAudio = null;
        }
    }
}
