package com.yline.record.module.main;

import android.view.View;
import android.widget.ImageView;

import com.yline.record.R;
import com.yline.record.view.AbstractViewHolder;

/**
 * 负责 录制 部分
 *
 * @author yline 2017/11/17 -- 10:07
 * @version 1.0.0
 */
public class MainRecordManager {
    //    private final static int MAX_DURATION = 8000;
//    private static final int HANDLER_RECORD = 200;
//    private static final int HANDLER_EDIT_VIDEO = 201;
//
    private AbstractViewHolder mViewHolder;
    //    private RecordedButton mRecordButton;
//
//    private List<Integer> mCameraTypeList;
    private OnMainRecordClickCallback mRecordClickCallback;

    //    private OnMainRecordProgressCallback mRecordProgressCallback;
//    private OnMainCompileProgressCallback mCompileProgressCallback;
//
//    private boolean isRecordedOver; // 本次段落是否录制完成
//    private RecordHandler mRecordHandler;
//
    public MainRecordManager(View parentView) {
        this.mViewHolder = new AbstractViewHolder(parentView);
//        this.mRecordButton = mViewHolder.get(R.id.main_record_button);
//        this.mCameraTypeList = new ArrayList<>();
//
        initViewClick();
//
//        mRecordHandler = new RecordHandler();
    }

    private void initViewClick() {
        // 切换闪光灯
        mViewHolder.setOnClickListener(R.id.iv_change_flash, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mRecordClickCallback) {
                    if (mRecordClickCallback.onRecordFlashSwitch()) {
                        mViewHolder.setImageResource(R.id.iv_change_flash, R.mipmap.video_flash_open);
                    } else {
                        mViewHolder.setImageResource(R.id.iv_change_flash, R.mipmap.video_flash_close);
                    }
                }
            }
        });

        // 切换摄像头视角
        mViewHolder.setOnClickListener(R.id.main_iv_change_camera, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mRecordClickCallback) {
                    mRecordClickCallback.onRecordCameraSwitch();
                }
            }
        });

        // 取消
        mViewHolder.setOnClickListener(R.id.main_iv_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView backImageView = mViewHolder.get(R.id.main_iv_back);

                if (null != mRecordClickCallback) {
                    mRecordClickCallback.onRecordDeleteClick();
                }
            }
        });

        // 结束
        mViewHolder.setOnClickListener(R.id.main_iv_finish, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mRecordClickCallback) {
                    mRecordClickCallback.onRecordFinishClick();
                }
            }
        });
//
//        // 录制视频
//        mRecordButton.setMax(MAX_DURATION);
//        mRecordButton.setOnGestureListener(new RecordedButton.OnGestureListener() {
//            @Override
//            public void onLongClick() {
//                isRecordedOver = false;
//                mRecordButton.setSplit();
//
//                mRecordHandler.sendEmptyMessageDelayed(HANDLER_RECORD, 100);
//                if (null != mRecordProgressCallback) {
//                    mRecordProgressCallback.onRecordStart();
//                }
//            }
//
//            @Override
//            public void onClick() {
//
//            }
//
//            @Override
//            public void onLift() {
//                isRecordedOver = true;
//
//                if (null != mRecordProgressCallback) {
//                    mRecordProgressCallback.onRecordStop();
//                }
//            }
//
//            @Override
//            public void onOver() {
//                isRecordedOver = true;
//                mRecordButton.closeButton();
//
//                if (null != mRecordProgressCallback) {
//                    mRecordProgressCallback.onRecordStop();
//                }
//
//                if (null != mRecordClickCallback) {
//                    mRecordClickCallback.onRecordFinishClick();
//                }
//
//                // 开始编译
//                mRecordHandler.sendEmptyMessage(HANDLER_EDIT_VIDEO);
//            }
//        });
    }

    //
//    public void setVisibility(int visibility) {
//        mViewHolder.setVisibility(visibility);
//    }
//
//    public void initRecordButton(int duration) {
//        mRecordButton.setProgress(duration);
//        mRecordButton.cleanSplit();
//    }
//
//    public void setRecordDeleteMode(boolean isDeleteMode) {
//        mRecordButton.setDeleteMode(isDeleteMode);
//    }
//
//    public void setRecordProgress(int count) {
//        mRecordButton.setProgress(count);
//    }
//
//    public int getRecordSplitCount() {
//        return mRecordButton.getSplitCount();
//    }
//
    public void setFlashImageClose() {
        mViewHolder.setImageResource(R.id.iv_change_flash, R.mipmap.video_flash_close);
    }

    //
//    private class RecordHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == HANDLER_RECORD) { // 拍摄视频的handler,更新进度
//                if (!isRecordedOver) {
//                    if (null != mRecordProgressCallback) {
//                        mRecordProgressCallback.onRecordUpdate();
//                    }
//                    mRecordHandler.sendEmptyMessageDelayed(HANDLER_RECORD, 30);
//                }
//            } else if (msg.what == HANDLER_EDIT_VIDEO) {
//                int progress = UtilityAdapter.FilterParserAction("", UtilityAdapter.PARSERACTION_PROGRESS);
//                if (progress == 100) {
//                    if (null != mCompileProgressCallback) {
//                        mCompileProgressCallback.onCompileFinish(progress);
//                    }
//                } else if (progress == -1) {
//                    if (null != mCompileProgressCallback) {
//                        mCompileProgressCallback.onCompileFailure(progress);
//                    }
//                } else {
//                    if (null != mCompileProgressCallback) {
//                        mCompileProgressCallback.onCompileUpdate(progress);
//                    }
//                    sendEmptyMessageDelayed(HANDLER_EDIT_VIDEO, 30);
//                }
//            }
//        }
//    }
//
    public void setOnRecordClickCallback(OnMainRecordClickCallback recordCallback) {
        this.mRecordClickCallback = recordCallback;
    }

    // 界面点击 回调
    public interface OnMainRecordClickCallback {
        /**
         * 切换 闪光灯
         *
         * @return 成功{true}
         */
        boolean onRecordFlashSwitch();

        /**
         * 切换摄像头
         */
        void onRecordCameraSwitch();

        /**
         * 点击删除
         *
         * @return 剩余可删除的量 的个数
         */
        int onRecordDeleteClick();

        /**
         * 点击结束
         */
        void onRecordFinishClick();
    }
//
//
//    public void setOnRecordProgressCallback(OnMainRecordProgressCallback recordProgressCallback) {
//        this.mRecordProgressCallback = recordProgressCallback;
//    }
//
//    public interface OnMainRecordProgressCallback {
//        /**
//         * 开始录制
//         */
//        void onRecordStart();
//
//        /**
//         * 录制结束
//         */
//        void onRecordStop();
//
//        /**
//         * 录制视频，录制个数
//         */
//        void onRecordUpdate();
//    }
//
//    public void setOnCompileProgressCallback(OnMainCompileProgressCallback progressCallback) {
//        this.mCompileProgressCallback = progressCallback;
//    }
//
//    /* 编译处理 回调 */
//
//    public interface OnMainCompileProgressCallback {
//        /**
//         * 编译失败
//         *
//         * @param progress 当前进度
//         */
//        void onCompileFailure(int progress);
//
//        /**
//         * 编译结束
//         *
//         * @param progress 当前进度
//         */
//        void onCompileFinish(int progress);
//
//        /**
//         * 正在编译
//         *
//         * @param progress 当前进度
//         */
//        void onCompileUpdate(int progress);
//    }
}
