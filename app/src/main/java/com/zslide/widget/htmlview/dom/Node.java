package com.zslide.widget.htmlview.dom;

public interface Node {
    Node getParentNode();

    Element getParentElement();

    Document getOwnerDocument();

    Node getFirstChild();

    Node getLastChild();

    Node getNextSibling();

    Node getPreviousSibling();

    Node appendChild(Node node);

    String getTextContent();
}