package com.hongbo.my_takephoto.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongbo.my_takephoto.Constant.Constant;
import com.hongbo.my_takephoto.R;
import com.hongbo.my_takephoto.Utils.RecycleViewDivider;
import com.hongbo.my_takephoto.Utils.ToolbarUtil;
import com.hongbo.my_takephoto.Utils.Utils;
import com.hongbo.my_takephoto.adapter.PictureAlbumDirectoryAdapter;
import com.hongbo.my_takephoto.model.FunctionConfig;
import com.hongbo.my_takephoto.model.LocalMedia;
import com.hongbo.my_takephoto.model.LocalMediaFolder;
import com.hongbo.my_takephoto.model.LocalMediaLoader;
import com.hongbo.my_takephoto.model.PictureConfig;
import com.hongbo.my_takephoto.observable.ImagesObservable;
import com.hongbo.my_takephoto.observable.ObserverListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */
public class PictureAlbumDirectoryActivity extends PictureBaseActivity implements View.OnClickListener, PictureAlbumDirectoryAdapter.OnItemClickListener, ObserverListener {

    private List<LocalMediaFolder> folders = new ArrayList<>();
    private PictureAlbumDirectoryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tv_empty;
    private RelativeLayout rl_picture_title;
    private TextView picture_tv_title, picture_tv_right;
    private List<LocalMedia> selectMedias = new ArrayList<>();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.ACTION_AC_FINISH)) {
                finish();
                overridePendingTransition(0, R.anim.slide_bottom_out);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_activity_album);
        registerReceiver(receiver, Constant.ACTION_AC_FINISH);
        if (selectMedias == null)
            selectMedias = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        rl_picture_title = (RelativeLayout) findViewById(R.id.rl_picture_title);
        picture_tv_title = (TextView) findViewById(R.id.picture_tv_title);
        picture_tv_right = (TextView) findViewById(R.id.picture_tv_right);
        tv_empty.setOnClickListener(this);
        ImagesObservable.getInstance().add(this);
        picture_tv_title.setText(getString(R.string.select_photo));

        ToolbarUtil.setColorNoTranslucent(this, backgroundColor);
        rl_picture_title.setBackgroundColor(backgroundColor);
        picture_tv_right.setText(getString(R.string.cancel));
        picture_tv_right.setOnClickListener(this);
        adapter = new PictureAlbumDirectoryAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecycleViewDivider(
                mContext, LinearLayoutManager.HORIZONTAL, Utils.dip2px(this, 0.5f), ContextCompat.getColor(this, R.color.line_color)));
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        initData();
    }

    /**
     * 初始化数据
     */
    protected void initData() {
        folders = ImagesObservable.getInstance().readLocalFolders();
        ImagesObservable.getInstance().saveLocalFolders(folders);
        Log.i("folders3",folders.toString()+"");
        if (this.folders.size() > 0) {
            tv_empty.setVisibility(View.GONE);
            adapter.bindFolderData(this.folders);
            notifyDataCheckedStatus(selectMedias);
        } else {
            tv_empty.setVisibility(View.VISIBLE);
            switch (type) {
                case LocalMediaLoader.TYPE_IMAGE:
                    tv_empty.setText(getString(R.string.no_photo));
                    break;
            }
        }
    }


    /**
     * 设置选中状态
     */
    private void notifyDataCheckedStatus(List<LocalMedia> medias) {
        try {
            // 获取选中图片
            if (medias == null) {
                medias = new ArrayList<>();
            }

            List<LocalMediaFolder> folders = adapter.getFolderData();
            for (LocalMediaFolder folder : folders) {
                // 只重置之前有选中过的文件夹，因为有可能也取消选中的
                if (folder.isChecked()) {
                    folder.setCheckedNum(0);
                    folder.setChecked(false);
                }
            }

            if (medias.size() > 0) {
                for (LocalMediaFolder folder : folders) {
                    int num = 0;// 记录当前相册下有多少张是选中的
                    List<LocalMedia> images = folder.getImages();
                    for (LocalMedia media : images) {
                        String path = media.getPath();
                        for (LocalMedia m : medias) {
                            if (path.equals(m.getPath())) {
                                num++;
                                folder.setChecked(true);
                                folder.setCheckedNum(num);
                            }
                        }
                    }
                }
            }
            adapter.bindFolderData(folders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_empty) {
            startEmptyImageActivity();
        } else if (id == R.id.picture_tv_right){
            finish();
            overridePendingTransition(0, R.anim.slide_bottom_out);
        }
    }

    /**
     *
     */
    private void startEmptyImageActivity() {
        List<LocalMedia> images = new ArrayList<>();
        String title = "";
        switch (type) {
            case LocalMediaLoader.TYPE_IMAGE:
                title = getString(R.string.lately_image);
                break;
        }
        startImageGridActivity(title, images);
    }

    @Override
    public void onItemClick(String folderName, List<LocalMedia> images) {
        if (images != null && images.size() > 0) {
            startImageGridActivity(folderName, images);
        }
    }


    private void startImageGridActivity(String folderName, final List<LocalMedia> images) {
        if (Utils.isFastDoubleClick()) {
            return;
        }
        Intent intent = new Intent();
        List<LocalMediaFolder> folders = adapter.getFolderData();
        ImagesObservable.getInstance().saveLocalMedia(images);
        ImagesObservable.getInstance().saveLocalFolders(folders);
        intent.putExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) selectMedias);
        intent.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
        intent.putExtra(FunctionConfig.FOLDER_NAME, folderName);
        intent.putExtra(FunctionConfig.EXTRA_IS_TOP_ACTIVITY, true);
        intent.setClass(mContext, PictureImageGridActivity.class);
        startActivityForResult(intent, FunctionConfig.REQUEST_IMAGE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        clearData();
    }

    protected void clearData() {
        // 释放静态变量
        PictureConfig.getPictureConfig().resultCallback = null;
        PictureConfig.pictureConfig = null;
        ImagesObservable.getInstance().remove(this);
        ImagesObservable.getInstance().clearLocalFolders();
        ImagesObservable.getInstance().clearLocalMedia();
        ImagesObservable.getInstance().clearSelectedLocalMedia();
    }

    @Override
    public void observerUpFoldersData(List<LocalMediaFolder> folders) {
        this.folders = folders;
        Log.i("folders1",folders.toString()+"");
        adapter.bindFolderData(folders);
        initData();
    }

    @Override
    public void observerUpSelectsData(List<LocalMedia> selectMedias) {
        folders = ImagesObservable.getInstance().readLocalFolders();
        Log.i("folders2",folders.toString()+"");
        this.selectMedias = selectMedias;
        if (folders != null && folders.size() > 0)
            adapter.bindFolderData(folders);
        if (selectMedias == null)
            selectMedias = new ArrayList<>();
        notifyDataCheckedStatus(selectMedias);
        if (tv_empty.getVisibility() == View.VISIBLE && adapter.getFolderData().size() > 0)
            tv_empty.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FunctionConfig.REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<LocalMedia> result = (List<LocalMedia>) data.getSerializableExtra(FunctionConfig.EXTRA_RESULT);
                setResult(RESULT_OK, new Intent().putExtra(FunctionConfig.EXTRA_RESULT, (Serializable) result));
                finish();
            }
        }
    }


}
