package com.zslide.widget.htmlview.dom;

public interface CSSStyleDeclaration {
    String getPropertyValue(String name);

    void setProperty(String name, String value);
}