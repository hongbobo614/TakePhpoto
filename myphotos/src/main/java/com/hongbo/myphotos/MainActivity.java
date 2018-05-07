package com.hongbo.myphotos;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hongbo.my_takephoto.model.FunctionConfig;
import com.hongbo.my_takephoto.model.LocalMedia;
import com.hongbo.my_takephoto.model.PictureConfig;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView mIv;
    private Button mBt;
    private Context mContext;

    private boolean isCompress = true;
    private int maxSelectNum =9;// 图片最大可选数量
    private List<LocalMedia> selectMedia = new ArrayList<>();
    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBt = (Button) findViewById(R.id.bt);
        mIv = (ImageView) findViewById(R.id.iv);
        mTv = (TextView) findViewById(R.id.tv);
        mContext = this;
        mBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionConfig config = new FunctionConfig();
                config.setCompress(isCompress);     //是否亚索
                config.setMaxSelectNum(maxSelectNum);
                config.setImageSpanCount(3);
                // 先初始化参数配置，在启动相册
                PictureConfig.init(config);
                PictureConfig.getPictureConfig().startOpenCamera(mContext,resultCallback);
            }
        });


    }

    /**
     * 图片回调方法
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            selectMedia = resultList;
            Log.i("callBack_result", selectMedia.size() + "**" + selectMedia.toString());

            if (selectMedia != null) {
                LocalMedia localMedia = selectMedia.get(0);
                String compressPath = localMedia.getCompressPath();
                Picasso.with(mContext).load(new File(compressPath)).into(mIv);
                mTv.setText(String.format("Size : %s", getReadableFileSize(new File(compressPath).length())));
            }
        }
    };

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
