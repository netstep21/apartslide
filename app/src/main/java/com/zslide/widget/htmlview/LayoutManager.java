package com.zslide.widget.htmlview;

/**
 * Interface for the di
 */
public interface LayoutManager {
    /**
     * The implementation is expected to call htmlLayout.setMeasuredSize() (which just forwards the
     * call to the protected method setMeasuredDimension().
     */
    void onMeasure(HtmlViewGroup htmlLayout, int widthSpec, int heightSpec);
}