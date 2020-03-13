package com.zslide.widget.htmlview;

import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.widget.htmlview.css.CssProperty;
import com.zslide.widget.htmlview.css.CssStyleDeclaration;
import com.zslide.widget.htmlview.css.CssUnit;

import java.util.ArrayList;


public class HtmlTextView extends TextView {

    static final String TAG = "HtmlTextView";

    CssStyleDeclaration computedStyle;
    SpannableStringBuilder content = new SpannableStringBuilder("");
    HtmlView htmlView;
    ArrayList<SpanCollection> images;
    boolean hasLineBreaks = false;
    int pendingBreakPosition = -1;

    public HtmlTextView(HtmlView pageContext) {
        super(pageContext.getContext());
        // this.setSingleLine(false);
        this.htmlView = pageContext;
        /*setTypeface(TypefaceUtils.load(getResources().getAssets(), getContext().getString(R.string.font_path)));*/
        int padding = getResources().getDimensionPixelSize(R.dimen.spacing_large);
        int lineSpacing = getResources().getDimensionPixelSize(R.dimen.spacing_smaller);
        setPadding(padding, 0, padding, lineSpacing);
        setLineSpacing(lineSpacing, 1);
        setLinkTextColor(ContextCompat.getColorStateList(getContext(), R.color.colors_link_text));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (images != null) {
            for (SpanCollection image : images) {
                image.updateStyle();
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    void append(String text) {
        if (pendingBreakPosition != -1) {
            content.replace(pendingBreakPosition, pendingBreakPosition + 1, "\n");
            pendingBreakPosition = -1;
        }
        // System.out.println("AppendText '" + text + "' to '" + getText() + "'");
        content.append(text);
        setText(content);
    }


    public void setComputedStyle(CssStyleDeclaration computedStyle) {
        this.computedStyle = computedStyle;
        // System.out.println("applyRootStyle to '" + content + "': " + computedStyle);
        float scale = htmlView.scale;
        setTextSize(TypedValue.COMPLEX_UNIT_PX, this.computedStyle.get(CssProperty.FONT_SIZE, CssUnit.PX) * scale);
        setTextColor(this.computedStyle.getColor(CssProperty.COLOR));
        //setTypeface(TypefaceUtils.load(getResources().getAssets(), getContext().getString(R.string.font_path)));
        setPaintFlags((getPaintFlags() & HtmlView.PAINT_MASK) | CssConversion.getPaintFlags(this.computedStyle));
        switch (this.computedStyle.getEnum(CssProperty.TEXT_ALIGN)) {
            case RIGHT:
                setGravity(Gravity.RIGHT);
                break;
            case CENTER:
                setGravity(Gravity.CENTER);
                break;
            default:
                setGravity(Gravity.LEFT);
                break;
        }
    }


}