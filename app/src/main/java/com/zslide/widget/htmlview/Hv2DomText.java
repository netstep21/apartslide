package com.zslide.widget.htmlview;

import com.zslide.widget.htmlview.dom.Text;

class Hv2DomText extends Hv2DomNode implements Text {
    String text;

    Hv2DomText(Hv2DomDocument ownerDocument, String text) {
        super(ownerDocument);
        this.text = text;
    }

    @Override
    public String getData() {
        return text;
    }

    @Override
    public Hv2DomNode getFirstChild() {
        return null;
    }

    @Override
    public Hv2DomNode getLastChild() {
        return null;
    }

    @Override
    public String getTextContent() {
        return text;
    }
}