package com.tactileshow.maintab.viewhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.tactileshow.main.R;

/**
 * 人体图
 * @author yline 2017/9/24 -- 20:05
 * @version 1.0.0
 */
public class BodyViewHelper {
    private View view;

    private ImageView ivHead, ivBody, iv_l_thigh, iv_r_thigh, iv_l_arm, iv_r_arm;

    private BodyDialogHelper dialogHelper;

    private OnBodyClickListener onBodyClickListener;

    public BodyViewHelper(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_tab_body, null);
        dialogHelper = new BodyDialogHelper(context);

        ivHead = (ImageView) view.findViewById(R.id.bodymap_iv_head);
        ivBody = (ImageView) view.findViewById(R.id.bodymap_iv_body);
        iv_l_arm = (ImageView) view.findViewById(R.id.bodymap_iv_l_arm);
        iv_r_arm = (ImageView) view.findViewById(R.id.bodymap_iv_r_arm);
        iv_l_thigh = (ImageView) view.findViewById(R.id.bodymap_iv_l_thigh);
        iv_r_thigh = (ImageView) view.findViewById(R.id.bodymap_iv_r_thigh);

        ivHead.setAlpha(0.3f);
        ivBody.setAlpha(0.3f);
        iv_l_arm.setAlpha(1.0f);
        iv_r_arm.setAlpha(0.3f);
        iv_l_thigh.setAlpha(0.3f);
        iv_r_thigh.setAlpha(0.3f);

        initViewClick();
    }

    private void initViewClick() {

        ivHead.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHelper.show();
            }
        });
        ivBody.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHelper.show();
            }
        });

        iv_l_arm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onBodyClickListener) {
                    onBodyClickListener.onBodyClick(BodyType.LeftArm);
                }
            }
        });
        iv_r_arm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHelper.show();
            }
        });

        iv_l_thigh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHelper.show();
            }
        });
        iv_r_thigh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogHelper.show();
            }
        });
    }

    public View getView() {
        return view;
    }

    public void setOnBodyClickListener(OnBodyClickListener onBodyClickListener) {
        this.onBodyClickListener = onBodyClickListener;
    }

    public interface OnBodyClickListener {
        void onBodyClick(BodyType bodyType);
    }

    public enum BodyType {
        Head, Body, LeftThigh, RightThigh, LeftArm, RightArm;
    }
}
