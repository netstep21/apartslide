package com.zslide.widget.htmlview;

import com.zslide.widget.htmlview.dom.Document;
import com.zslide.widget.htmlview.dom.Element;
import com.zslide.widget.htmlview.dom.Node;

class Hv2DomDocument extends Hv2DomContainer implements Document {
    protected final HtmlView htmlContext;

    protected Hv2DomDocument(HtmlView context) {
        super(null, ComponentType.PHYSICAL_CONTAINER);
        this.htmlContext = context;
    }

    public Hv2DomElement createElement(String name) {
        return new Hv2DomElement(this, name, null);
    }

    @Override
    public Hv2DomText createTextNode(String text) {
        return new Hv2DomText(this, text);
    }

    @Override
    public Element getDocumentElement() {
        Node result = getFirstChild();
        while (result != null && !(result instanceof Element)) {
            result = result.getNextSibling();
        }
        return (Element) result;
    }

}