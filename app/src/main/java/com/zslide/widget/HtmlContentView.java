package com.zslide.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zslide.widget.htmlview.HtmlProcessor;
import com.zslide.widget.htmlview.HtmlView;

import java.io.StringReader;

/**
 * Created by chulwoo on 2016. 11. 16..
 */

public class HtmlContentView extends FrameLayout {

    private HtmlView contentView;

    public HtmlContentView(Context context) {
        super(context);
    }

    public HtmlContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HtmlContentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setHtml(String html) {
        removeAllViews();
        contentView = new HtmlView(getContext());
        addView(contentView);
        HtmlProcessor processor = new HtmlProcessor();
        processor.parse(new StringReader(html), contentView);
    }
}
