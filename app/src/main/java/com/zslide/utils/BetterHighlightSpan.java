package com.zslide.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

public class BetterHighlightSpan extends ReplacementSpan {

    private int backgroundColor;
    private float lineSpacing;

    public BetterHighlightSpan(int backgroundColor, float lineSpacing) {
        super();
        this.backgroundColor = backgroundColor;
        this.lineSpacing = lineSpacing;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end));
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                     Paint paint) {
        int oldColor = paint.getColor();
        RectF rect = new RectF(x, top, x + paint.measureText(text, start, end), bottom - lineSpacing);
        paint.setColor(backgroundColor);
        canvas.drawRect(rect, paint);
        paint.setColor(oldColor);
        canvas.drawText(text, start, end, x, y, paint);
    }
}