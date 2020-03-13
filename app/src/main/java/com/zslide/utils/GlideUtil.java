package com.zslide.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by jdekim43 on 2016. 4. 11..
 */
public class GlideUtil {

    public static BitmapTransformation scalingBelowMaxSize(Context context, int max) {
        return new BitmapTransformation(context) {

            @Override
            protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
                int width = toTransform.getWidth();
                int height = toTransform.getHeight();
                if (height >= max) {
                    width = max * toTransform.getWidth() / toTransform.getHeight();
                    height = max;
                }
                if (width >= max) {
                    height = max * toTransform.getHeight() / toTransform.getWidth();
                    width = max;
                }

                ZLog.i(GlideUtil.class, "scaling image : " +
                        toTransform.getWidth() + "*" + toTransform.getHeight() +
                        " to " + width + "*" + height);
                if (width != toTransform.getWidth() || height != toTransform.getHeight()) {
                    return Bitmap.createScaledBitmap(toTransform, width, height, false);
                }
                return toTransform;
            }

            @Override
            public String getId() {
                return "resizing";
            }
        };
    }
}
