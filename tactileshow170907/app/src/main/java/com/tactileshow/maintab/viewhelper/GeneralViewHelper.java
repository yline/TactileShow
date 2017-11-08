package com.tactileshow.maintab.viewhelper;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tactileshow.main.R;
import com.tactileshow.util.macro;

import java.io.IOException;

/**
 * 一般信息
 *
 * @author yline 2017/9/24 -- 20:05
 * @version 1.0.0
 */
public class GeneralViewHelper {
    private Context context;
    private View view;

    private Vibrator vibrator;
    private MediaPlayer player = new MediaPlayer();
    private AudioManager audioManager;

    private TextView tvTemp, tvPress, tvGerm;
    private ImageView ivHand, ivNeedle, ivAlert, ivGerm, ivFire;

    public GeneralViewHelper(Context context) {
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(R.layout.view_tab_general, null);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        initRing();

        tvTemp = view.findViewById(R.id.general_temp);
        tvPress = view.findViewById(R.id.general_press);
        tvGerm = view.findViewById(R.id.general_germ);

        ivGerm = view.findViewById(R.id.general_img_germ);
        ivHand = view.findViewById(R.id.general_img_hand);
        ivNeedle = view.findViewById(R.id.general_img_needle);
        ivAlert = view.findViewById(R.id.general_img_alert);
        ivFire = view.findViewById(R.id.general_img_fire);

        ivGerm.setVisibility(View.INVISIBLE);
        ivNeedle.setVisibility(View.INVISIBLE);
        ivAlert.setVisibility(View.INVISIBLE);
        ivFire.setVisibility(View.INVISIBLE);

        setTemp(20.2);
        setHum(30);
        setGerm(20.2);
    }

    private void initRing() {
        try {
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            player.setDataSource(context, ringUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private void startVibrate() {
        if (macro.SETTINGS_VIBRA) {
            vibrator.vibrate(macro.VIBRATION_MODE, -1);
        }
    }

    public void setTemp(double temp) {
        tvTemp.setText(parseTemp(temp));
    }

    public void setHum(double hum) {
        tvPress.setText(parsePress(hum));
    }

    public void setGerm(double germ) {
        tvGerm.setText(parseGerm(germ));
    }

    private void startRing() {
        if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0 && macro.SETTINGS_SOUND) {
            player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            player.setLooping(false);
            try {
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.start();
        }
    }

    private String parseTemp(double tp) {
        if (tp > macro.SETTING_TEMP_RANGE[2]) {
            tvTemp.setTextColor(Color.RED);
            ivFire.setVisibility(View.VISIBLE);
            startVibrate();
            startRing();
            return "过高";
        } else if (tp > macro.SETTING_TEMP_RANGE[1]) {
            tvTemp.setTextColor(Color.GREEN);
            ivFire.setVisibility(View.INVISIBLE);
            return "舒适";
        } else if (tp > macro.SETTING_TEMP_RANGE[0]) {
            tvTemp.setTextColor(Color.argb(100, 57, 174, 204));
            ivFire.setVisibility(View.INVISIBLE);
            return "偏冷";
        } else {
            tvTemp.setTextColor(Color.BLUE);
            ivFire.setVisibility(View.INVISIBLE);
            return "过低";
        }
    }

    private String parsePress(double press) {
        if (press > macro.SETTING_PRESS_RANGE[2]) {
            tvPress.setTextColor(Color.BLUE);
            ivNeedle.setVisibility(View.VISIBLE);
            startVibrate();
            startRing();
            return "严重挤压，请离开";
        } else if (press > macro.SETTING_PRESS_RANGE[2]) {
            tvPress.setTextColor(Color.GREEN);
            ivNeedle.setVisibility(View.INVISIBLE);
            return "较重挤压";
        } else if (press > macro.SETTING_PRESS_RANGE[2]) {
            tvPress.setTextColor(Color.MAGENTA);
            ivNeedle.setVisibility(View.INVISIBLE);
            return "轻微挤压";
        } else {
            tvPress.setTextColor(Color.YELLOW);
            ivNeedle.setVisibility(View.INVISIBLE);
            return "无挤压";
        }
    }

    private String parseGerm(double germ) {
        if (germ >= 38 && germ <= 42) {
            tvGerm.setTextColor(Color.RED);
            ivGerm.setVisibility(View.VISIBLE);
            return "可能出现炎症";
        } else {
            tvGerm.setTextColor(Color.GREEN);
            ivGerm.setVisibility(View.INVISIBLE);
            return "没有出现炎症";
        }
    }

    public View getView() {
        return view;
    }
}
