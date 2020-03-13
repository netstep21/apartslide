package com.zslide.models;

import java.io.Serializable;

/**
 * Created by chulwoo on 16. 8. 9..
 */
public interface ShareableItem extends Serializable {

    String getTitle();

    String getMessage();

    String getRedirectUrl();

    String getThumbnailUrl();

    int getThumbnailWidth();

    int getThumbnailHeight();
}
