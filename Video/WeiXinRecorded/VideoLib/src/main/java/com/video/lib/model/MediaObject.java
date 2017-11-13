package com.video.lib.model;

import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

public class MediaObject implements Serializable {
    /**
     * 导入视频
     */
    public final static int MEDIA_PART_TYPE_IMPORT_VIDEO = 1;
    /**
     * 导入图片
     */
    public final static int MEDIA_PART_TYPE_IMPORT_IMAGE = 2;
    /**
     * 使用系统拍摄mp4
     */
    public final static int MEDIA_PART_TYPE_RECORD_MP4 = 3;
    /**
     * 默认最大时长
     */
    public final static int DEFAULT_MAX_DURATION = 10 * 1000;

    private final static int DEFAULT_VIDEO_BITRATE = 2048; // 默认码率

    /**
     * 视频最大时长，默认10秒
     */
    private int mMaxDuration;
    /**
     * 视频目录
     */
    private String mOutputDirectory;
    /**
     * 对象文件
     */
    private String mOutputObjectPath;
    /**
     * 视频码率
     */
    private int mVideoBitrate;
    /**
     * 最终视频输出路径
     */
    private String mOutputVideoPath;
    /**
     * 最终视频截图输出路径
     */
    private String mOutputVideoThumbPath;
    /**
     * 文件夹、文件名
     */
    private String mKey;
    /**
     * 当前分块
     */
    private volatile transient MediaPartModel mCurrentPart;
    /**
     * 获取所有分块
     */
    private LinkedList<MediaPartModel> mMediaList;

    private String mTsPath;

    public MediaObject(String path, String key) {
        this.mKey = key;
        this.mOutputDirectory = path;
        this.mVideoBitrate = DEFAULT_VIDEO_BITRATE;

        this.mOutputObjectPath = mOutputDirectory + File.separator + mKey + ".obj";
        this.mOutputVideoPath = mOutputDirectory + File.separator + mKey + "_real.mp4";

        this.mTsPath = mOutputDirectory + File.separator + "0.ts";
        this.mOutputVideoThumbPath = mOutputDirectory + ".jpg";

        this.mMaxDuration = DEFAULT_MAX_DURATION;

        mMediaList = new LinkedList<>();
    }

    /**
     * 获取视频码率
     */
    public int getVideoBitrate() {
        return mVideoBitrate;
    }

    /**
     * 获取视频最大长度
     */
    public int getMaxDuration() {
        return mMaxDuration;
    }

    /**
     * 设置最大时长，必须大于1秒
     */
    public void setMaxDuration(int duration) {
        if (duration >= 1000) {
            mMaxDuration = duration;
        }
    }

    /**
     * 获取视频临时文件夹
     */
    public String getOutputDirectory() {
        return mOutputDirectory;
    }

    /**
     * 获取视频临时输出播放
     */
    public String getOutputTempVideoPath() {
        return mOutputDirectory + File.separator + mKey + ".mp4";
    }

    public String getTsPath() {
        return mTsPath;
    }

    /**
     * 清空主题
     */
    public void cleanTheme() {
        if (mMediaList != null) {
            for (MediaPartModel part : mMediaList) {
                part.setCutStartTime(0);
                part.setCutEndTime(part.getDuration());
            }
        }
    }

    /**
     * 获取视频信息春促路径
     */
    public String getObjectFilePath() {
        if (TextUtils.isEmpty(mOutputObjectPath)) {
            File f = new File(mOutputVideoPath);
            String obj = mOutputDirectory + File.separator + f.getName() + ".obj";
            mOutputObjectPath = obj;
        }
        return mOutputObjectPath;
    }

    /**
     * 获取视频最终输出地址
     */
    public String getOutputVideoPath() {
        return mOutputVideoPath;
    }

    /**
     * 获取视频截图最终输出地址
     */
    public String getOutputVideoThumbPath() {
        return mOutputVideoThumbPath;
    }

    /**
     * 获取录制的总时长
     */
    public int getDuration() {
        int duration = 0;
        if (mMediaList != null) {
            for (MediaPartModel part : mMediaList) {
                duration += part.getDuration();
            }
        }
        return duration;
    }

    /**
     * 获取剪切后的总时长
     */
    public int getCutDuration() {
        int duration = 0;
        if (mMediaList != null) {
            for (MediaPartModel part : mMediaList) {
                int cut = part.getCutEndTime() - part.getCutStartTime();
                if (part.getSpeed() != 10) {
                    cut = (int) (cut * 10.0f / part.getSpeed());
                }
                duration += cut;
            }
        }
        return duration;
    }

    public void removeAllPart() {
        if (mMediaList != null) {
            mMediaList.clear();
        }
    }


    /**
     * 删除分块
     */
    public void removePart(MediaPartModel part, boolean deleteFile) {
        if (mMediaList != null) {
            mMediaList.remove(part);
        }

        if (part != null) {
            part.stop();
            // 删除文件
            if (deleteFile) {
                part.delete();
            }
            mMediaList.remove(part);
        }
    }

    /**
     * 生成分块信息，主要用于拍摄
     *
     * @param cameraId 记录摄像头是前置还是后置
     * @return
     */
    public MediaPartModel buildMediaPart(int cameraId) {
        mCurrentPart = new MediaPartModel();
        mCurrentPart.setPosition(getDuration());
        mCurrentPart.setIndex(mMediaList.size());
        mCurrentPart.setMediaPath(mOutputDirectory + File.separator + mCurrentPart.getIndex() + ".v");
        mCurrentPart.setAudioPath(mOutputDirectory + File.separator + mCurrentPart.getIndex() + ".a");
        mCurrentPart.setThumbPath(mOutputDirectory + File.separator + mCurrentPart.getIndex() + ".jpg");
        mCurrentPart.setCameraId(cameraId);
        mCurrentPart.prepare();

        mCurrentPart.setRecording(true);
        mCurrentPart.setStartTime(System.currentTimeMillis());
        mCurrentPart.setType(MEDIA_PART_TYPE_IMPORT_VIDEO);

        mMediaList.add(mCurrentPart);
        return mCurrentPart;
    }

    public MediaPartModel buildMediaPart(int cameraId, String videoSuffix) {
        mCurrentPart = new MediaPartModel();
        mCurrentPart.setPosition(getDuration());
        mCurrentPart.setIndex(mMediaList.size());
        mCurrentPart.setMediaPath(mOutputDirectory + File.separator + mCurrentPart.getIndex() + videoSuffix);
        mCurrentPart.setAudioPath(mOutputDirectory + File.separator + mCurrentPart.getIndex() + ".a");
        mCurrentPart.setThumbPath(mOutputDirectory + File.separator + mCurrentPart.getIndex() + ".jpg");
        mCurrentPart.setCameraId(cameraId);

        mCurrentPart.setRecording(true);
        mCurrentPart.setStartTime(System.currentTimeMillis());
        mCurrentPart.setType(MEDIA_PART_TYPE_IMPORT_VIDEO);

        mMediaList.add(mCurrentPart);
        return mCurrentPart;
    }

    /**
     * 生成分块信息，主要用于视频导入
     *
     * @param path
     * @param duration
     * @param type
     * @return
     */
    public MediaPartModel buildMediaPart(String path, int duration, int type) {
        mCurrentPart = new MediaPartModel();

        mCurrentPart.setPosition(getDuration());
        mCurrentPart.setIndex(mMediaList.size());
        mCurrentPart.setMediaPath(mOutputDirectory + File.separator + mCurrentPart.getIndex() + ".v");
        mCurrentPart.setAudioPath(mOutputDirectory + File.separator + mCurrentPart.getIndex() + ".a");
        mCurrentPart.setThumbPath(mOutputDirectory + File.separator + mCurrentPart.getIndex() + ".jpg");

        mCurrentPart.setDuration(duration);
        mCurrentPart.setStartTime(0);
        mCurrentPart.setCutEndTime(duration);
        mCurrentPart.setCutStartTime(0);
        mCurrentPart.setCutEndTime(duration);
        mCurrentPart.setTempPath(path);
        mCurrentPart.setType(type);

        mMediaList.add(mCurrentPart);
        return mCurrentPart;
    }

    public String getConcatYUV() {
        StringBuilder yuv = new StringBuilder();
        if (mMediaList != null && mMediaList.size() > 0) {
            if (mMediaList.size() == 1) {
                if (TextUtils.isEmpty(mMediaList.get(0).getTempMediaPath())) {
                    yuv.append(mMediaList.get(0).getMediaPath());
                } else {
                    yuv.append(mMediaList.get(0).getTempMediaPath());
                }
            } else {
                yuv.append("concat:");
                for (int i = 0, j = mMediaList.size(); i < j; i++) {
                    MediaPartModel part = mMediaList.get(i);
                    if (TextUtils.isEmpty(part.getTempMediaPath())) {
                        yuv.append(part.getMediaPath());
                    } else {
                        yuv.append(part.getTempMediaPath());
                    }
                    if (i + 1 < j) {
                        yuv.append("|");
                    }
                }
            }
        }
        return yuv.toString();
    }

    public String getConcatPCM() {
        StringBuilder yuv = new StringBuilder();
        if (mMediaList != null && mMediaList.size() > 0) {
            if (mMediaList.size() == 1) {
                if (TextUtils.isEmpty(mMediaList.get(0).getTempAudioPath())) {
                    yuv.append(mMediaList.get(0).getAudioPath());
                } else {
                    yuv.append(mMediaList.get(0).getTempAudioPath());
                }
            } else {
                yuv.append("concat:");
                for (int i = 0, j = mMediaList.size(); i < j; i++) {
                    MediaPartModel part = mMediaList.get(i);
                    if (TextUtils.isEmpty(part.getTempAudioPath())) {
                        yuv.append(part.getAudioPath());
                    } else {
                        yuv.append(part.getTempAudioPath());
                    }
                    if (i + 1 < j) {
                        yuv.append("|");
                    }
                }
            }
        }
        return yuv.toString();
    }

    /**
     * 获取当前分块
     */
    public MediaPartModel getCurrentPart() {
        if (mCurrentPart != null) {
            return mCurrentPart;
        }
        if (mMediaList != null && mMediaList.size() > 0) {
            mCurrentPart = mMediaList.get(mMediaList.size() - 1);
        }
        return mCurrentPart;
    }

    public int getCurrentIndex() {
        MediaPartModel part = getCurrentPart();
        if (part != null) {
            return part.getIndex();
        }
        return 0;
    }

    public MediaPartModel getPart(int index) {
        if (mCurrentPart != null && index < mMediaList.size()) {
            return mMediaList.get(index);
        }
        return null;
    }

    public int getPartSize() {
        return mMediaList.size();
    }

    public LinkedList<MediaPartModel> getMediaParts() {
        return mMediaList;
    }

    /**
     * 取消拍摄
     */
    public void delete() {
        if (mMediaList != null) {
            for (MediaPartModel part : mMediaList) {
                part.stop();
            }
        }
        deleteDir(mOutputDirectory);
    }

    private static void deleteDir(String f) {
        if (f != null && f.length() > 0) {
            deleteDir(new File(f));
        }
    }

    private static void deleteDir(File f) {
        if (f != null && f.exists() && f.isDirectory()) {
            for (File file : f.listFiles()) {
                if (file.isDirectory()) {
                    deleteDir(file);
                }
                file.delete();
            }
            f.delete();
        }
    }

    public LinkedList<MediaPartModel> getMedaParts() {
        return mMediaList;
    }

    /**
     * 预处理数据对象
     */
    public static void preparedMediaObject(MediaObject mMediaObject) {
        if (mMediaObject != null && mMediaObject.mMediaList != null) {
            int duration = 0;
            for (MediaPartModel part : mMediaObject.mMediaList) {
                part.setStartTime(duration);
                part.setEndTime(part.getStartTime() + part.getDuration());

                duration += part.getDuration();
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        if (mMediaList != null) {
            result.append("[" + mMediaList.size() + "]");
            for (MediaPartModel part : mMediaList) {
                result.append(part.getMediaPath() + ":" + part.getDuration() + "\n");
            }
        }
        return result.toString();
    }
}
