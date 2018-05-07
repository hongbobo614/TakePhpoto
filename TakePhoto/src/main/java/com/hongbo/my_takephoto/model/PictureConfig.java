package com.hongbo.my_takephoto.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.hongbo.my_takephoto.R;
import com.hongbo.my_takephoto.Utils.Utils;
import com.hongbo.my_takephoto.view.PictureAlbumDirectoryActivity;
import com.hongbo.my_takephoto.view.PictureImageGridActivity;

import java.util.List;


/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.util
 * email：893855882@qq.com
 * data：17/1/5
 */
public class PictureConfig {

    public static FunctionConfig config;
    public static PictureConfig pictureConfig;


    public static PictureConfig getPictureConfig() {
        if (pictureConfig == null) {
            pictureConfig = new PictureConfig();
        }

        return pictureConfig;
    }

    public PictureConfig() {
        super();
    }


    public OnSelectResultCallback resultCallback;

    public OnSelectResultCallback getResultCallback() {
        return resultCallback;
    }

    public static void init(FunctionConfig functionConfig) {
        config = functionConfig;
    }


    /**
     * 启动相册
     */
    public void openPhoto(Context mContext, OnSelectResultCallback resultCall) {
        if (Utils.isFastDoubleClick()) {
            return;
        }
        if (config == null) {
            config = new FunctionConfig();
        }
        // 这里仿ios微信相册启动模式
        Intent intent1 = new Intent(mContext, PictureAlbumDirectoryActivity.class);
        Intent intent2 = new Intent(mContext, PictureImageGridActivity.class);
        Intent[] intents = new Intent[2];
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intents[0] = intent1;
        intents[1] = intent2;
        intent1.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
        intent2.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
        mContext.startActivities(intents);
//        ((Activity) mContext).overridePendingTransition(R.anim.slide_bottom_in, 0);
        ((Activity) mContext).overridePendingTransition(R.anim.fade, R.anim.hold);
        // 绑定图片接口回调函数事件
        resultCallback = resultCall;
    }

    /**
     * start to camera、preview、crop
     */
    public void startOpenCamera(Context mContext, OnSelectResultCallback resultCall) {
        if (config == null) {
            config = new FunctionConfig();
        }
        Intent intent = new Intent(mContext, PictureImageGridActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
//        intent.putExtra(FunctionConfig.FUNCTION_TAKE, true);
        intent.putExtra(FunctionConfig.FUNCTION_TAKE, false);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_bottom_in, R.anim.hold);
        // 绑定图片接口回调函数事件
        resultCallback = resultCall;
    }


    /**
     * 处理结果
     */
    public interface OnSelectResultCallback {
        /**
         * 处理成功
         *
         * @param resultList
         */
        public void onSelectSuccess(List<LocalMedia> resultList);

    }
}
