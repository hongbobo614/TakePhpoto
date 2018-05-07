package com.hongbo.my_takephoto.observable;


import com.hongbo.my_takephoto.model.LocalMedia;
import com.hongbo.my_takephoto.model.LocalMediaFolder;

import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.observable
 * email：893855882@qq.com
 * data：17/1/16
 */
public interface SubjectListener {
    void add(ObserverListener observerListener);

    void notifyFolderObserver(List<LocalMediaFolder> folders);

    void notifySelectLocalMediaObserver(List<LocalMedia> medias);

    void remove(ObserverListener observerListener);
}
