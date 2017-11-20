package com.video.lib;

import com.video.lib.manager.MediaRecorderBase;

import java.util.List;
import java.util.Locale;

/**
 * 命令行，生成
 * @author yline 2017/11/17 -- 15:27
 * @version 1.0.0
 */
public class FfmpegCommandManager {

    /**
     * @return ffmpeg -i path -vcodec copy -acodec copy -ss startStr -t endStr output
     */
    public static String getCommandCutTime(String path, String startStr, String endStr, String output){
        StringBuilder sb = new StringBuilder("ffmpeg");
        sb.append(" -i");
        sb.append(" " + path);
        sb.append(" -vcodec");
        sb.append(" copy");
        sb.append(" -acodec");
        sb.append(" copy");
        sb.append(" -ss");
        sb.append(" " + startStr);
        sb.append(" -t");
        sb.append(" " + endStr);
        sb.append(" " + output);
        return sb.toString();
    }

    /**
     * ffmpeg -i videoPath -i imagePath -filter_complex overlay=0:0 -vcodec libx264 -profile:v baseline -preset ultrafast -b:v 3000k -g 30 -f mp4 outPath
     */
    public static String getCommandEditVideo(String path, String imagePath, String mergeVideo){
        StringBuilder sb = new StringBuilder();
        sb.append("ffmpeg");
        sb.append(" -i");
        sb.append(" " + path);
        sb.append(" -i");
        sb.append(" " + imagePath);
        sb.append(" -filter_complex");
        sb.append(" overlay=0:0");
        sb.append(" -vcodec libx264 -profile:v baseline -preset ultrafast -b:v 3000k -g 25");
        sb.append(" -f mp4");
        sb.append(" " + mergeVideo);
        return sb.toString();
    }

    /**
     * ffmpeg -i 2x.mp4 -filter_complex "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]" -map "[v]" -map "[a]" output3.mp4
     */
    public static String getCommandEditVideoSpeed(String path, String outPut, float speed){
        String filter = String.format(Locale.getDefault(), "[0:v]setpts=%f*PTS[v];[0:a]atempo=%f[a]", 1 / speed, speed);
        StringBuilder sb = new StringBuilder("ffmpeg");
        sb.append(" -i");
        sb.append(" " + path);
        sb.append(" -filter_complex");
        sb.append(" " + filter);
        sb.append(" -map");
        sb.append(" [v]");
        sb.append(" -map");
        sb.append(" [a]");
        sb.append(" -y");
        sb.append(" " + outPut);
        return sb.toString();
    }

    public static String getCommandMainMp4ToTs(String path, String output) {
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

        return sb.toString();
    }

    public static String getCommandMainTsToMp4(List<String> path, String output) {
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

        return sb.toString();
    }

    public static String getCutSizeCutVideo(String outPut, String path, int cropWidth, int cropHeight, int x, int y) {
        //./ffmpeg -i 2x.mp4 -filter_complex "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]" -map "[v]" -map "[a]" output3.mp4
        String filter = String.format(Locale.getDefault(), "crop=%d:%d:%d:%d", cropWidth, cropHeight, x, y);
        StringBuilder sb = new StringBuilder("ffmpeg");
        sb.append(" -i");
        sb.append(" " + path);
        sb.append(" -vf");
        sb.append(" " + filter);
        sb.append(" -acodec");
        sb.append(" copy");
        sb.append(" -b:v");
        int rate = (int) (MediaRecorderBase.VIDEO_BITRATE_HIGH * 1.5f);
        sb.append(" " + rate + "k");
        sb.append(" -y");
        sb.append(" " + outPut);

        return sb.toString();
    }
}
