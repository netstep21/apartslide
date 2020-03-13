package com.zslide.managers;

import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;

import com.zslide.models.ZummaStore;

/**
 * Created by chulwoo on 16. 8. 2..
 * <p>
 * 메모리 캐시
 * 목록에서 object의 각 수치들이 동일하게 보일 수 있도록 관리해야 한다.
 * <p>
 * 줌마가게: 좋아요 여부/좋아요 수
 * 줌마캐스트: 북마크 여부/북마크 수/댓글 수가
 * <p>
 * TODO: 더 괜찮은 방법이 없을지 생각해 봐야 함
 */
public class ItemCache {

    private static volatile ItemCache instance;

    private LongSparseArray<ZummaStore> zummaStoreCaches;

    private ItemCache() {
        zummaStoreCaches = new LongSparseArray<>();
    }

    public static ItemCache getInstance() {
        if (instance == null) {
            synchronized (ItemCache.class) {
                if (instance == null) {
                    instance = new ItemCache();
                }
            }
        }

        return instance;
    }

    public void set(ZummaStore... zummaStores) {
        for (ZummaStore zummaStore : zummaStores) {
            zummaStoreCaches.put(zummaStore.getId(), zummaStore);
        }
    }

    public boolean contains(@NonNull ZummaStore zummaStore) {
        return zummaStoreCaches.get(zummaStore.getId()) != null;
    }

    public ZummaStore getZummaStore(long id) {
        return zummaStoreCaches.get(id);
    }

}
