package com.zslide.widget.htmlview;

import android.util.Log;

import com.zslide.widget.htmlview.css.CssStylableElement;
import com.zslide.widget.htmlview.css.CssStyleSheet;
import com.zslide.widget.htmlview.dom.Node;
import com.zslide.widget.htmlview.dom.Text;
import com.zslide.widget.htmlview.kxml.HtmlParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

/**
 * Uses a HtmlParser to generate a widget tree that corresponds to the HTML code.
 * <p>
 * Can be re-used, but is not thread safe.
 */
public class HtmlProcessor {
    private static final String TAG = "HtmlProcessor";
    private static final LinkedHashMap<String, Hv2DomContainer.ComponentType> ELEMENT_TYPES = new LinkedHashMap<>();

    static {
        ELEMENT_TYPES.put("a", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("b", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("big", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("br", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("del", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("em", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("font", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("head", Hv2DomContainer.ComponentType.INVISIBLE);
        ELEMENT_TYPES.put("html", Hv2DomContainer.ComponentType.LOGICAL_CONTAINER);
        ELEMENT_TYPES.put("i", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("img", Hv2DomContainer.ComponentType.IMAGE);
        ELEMENT_TYPES.put("input", Hv2DomContainer.ComponentType.LEAF_COMPONENT);
        ELEMENT_TYPES.put("ins", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("link", Hv2DomContainer.ComponentType.INVISIBLE);
        ELEMENT_TYPES.put("meta", Hv2DomContainer.ComponentType.INVISIBLE);
        ELEMENT_TYPES.put("script", Hv2DomContainer.ComponentType.INVISIBLE);
        ELEMENT_TYPES.put("select", Hv2DomContainer.ComponentType.LEAF_COMPONENT);
        ELEMENT_TYPES.put("small", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("span", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("strike", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("strong", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("style", Hv2DomContainer.ComponentType.INVISIBLE);
        ELEMENT_TYPES.put("sub", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("sup", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("tbody", Hv2DomContainer.ComponentType.LOGICAL_CONTAINER);
        ELEMENT_TYPES.put("thead", Hv2DomContainer.ComponentType.LOGICAL_CONTAINER);
        ELEMENT_TYPES.put("title", Hv2DomContainer.ComponentType.INVISIBLE);
        ELEMENT_TYPES.put("tt", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("tr", Hv2DomContainer.ComponentType.LOGICAL_CONTAINER);
        ELEMENT_TYPES.put("u", Hv2DomContainer.ComponentType.TEXT);
        ELEMENT_TYPES.put("video", Hv2DomContainer.ComponentType.VIDEO);
    }

    private HtmlParser parser;
    private HtmlView htmlView;
    private Hv2DomDocument document;

    static Hv2DomContainer.ComponentType getElementType(String name) {
        Hv2DomContainer.ComponentType result = ELEMENT_TYPES.get(name);
        return result == null ? Hv2DomContainer.ComponentType.PHYSICAL_CONTAINER : result;
    }

    public void parse(Reader reader, HtmlView htmlView) {
        this.htmlView = htmlView;
        try {
            if (parser == null) {
                parser = new HtmlParser();
            }
            parser.setInput(reader);
            parser.next();
            document = htmlView.getDocument();

            parseContainerContent(document);

            TreeSync.syncContainer(htmlView, document, true);

            CssStyleSheet styleSheet = htmlView.getStyleSheet();
            Node child = document.getFirstChild();
            while (child != null) {
                if (child instanceof CssStylableElement) {
                    styleSheet.apply((CssStylableElement) child, null);
                }
                child = child.getNextSibling();
            }

        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse the text content of an element.
     * Precondition: behind the opening tag
     * Postcondition: on the closing tag
     */
    private String parseTextContent() throws IOException, XmlPullParserException {
        StringBuilder sb = new StringBuilder();
        while (parser.getEventType() != XmlPullParser.END_TAG) {
            switch (parser.getEventType()) {
                case XmlPullParser.START_TAG:
                    parser.next();
                    sb.append(parseTextContent());
                    parser.next();
                    break;

                case XmlPullParser.TEXT:
                    sb.append(parser.getText());
                    parser.next();
                    break;

                default:
                    throw new RuntimeException("Unexpected event: " + parser.getPositionDescription());
            }
        }
        return sb.toString();
    }


    private String normalizeText(String s, boolean preserveLeadingSpace) {
        boolean spaceSeen = !preserveLeadingSpace;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c <= ' ') {
                if (!spaceSeen) {
                    sb.append(' ');
                    spaceSeen = true;
                }
            } else {
                sb.append(c);
                spaceSeen = false;
            }
        }
        return sb.toString();
    }

    private void parseContainerContent(Node container) throws IOException, XmlPullParserException {
        // System.out.println("parseContainerContent " + name);
        while (parser.getEventType() != XmlPullParser.END_DOCUMENT
                && parser.getEventType() != XmlPullParser.END_TAG) {
            switch (parser.getEventType()) {
                case XmlPullParser.START_TAG: {
                    String childName = parser.getName();
                    if (childName.equals("link")) {
                        if ("stylesheet".equals(parser.getAttributeValue("rel"))) {
                            String href = parser.getAttributeValue("href");
                            if (href != null) {
                                try {
                                    htmlView.requestStyleSheet(htmlView,
                                            htmlView.createUri(parser.getAttributeValue("href")));
                                } catch (URISyntaxException e) {
                                    Log.e(TAG, "Error resolving stylesheet URL " + href, e);
                                }
                            }
                        }
                        parser.next();
                        parseTextContent();
                        parser.next();
                    } else if (childName.equals("style")) {
                        parser.next();
                        String styleText = parseTextContent();
                        htmlView.getStyleSheet().read(styleText, htmlView.getBaseUri(), null, null, null);
                        parser.next();
                    } else {
                        Hv2DomElement viewElement = htmlView.getDocument().createElement(parser.getName());
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            viewElement.setAttribute(parser.getAttributeName(i), parser.getAttributeValue(i));
                        }
                        container.appendChild(viewElement);
                        parser.next();
                        parseContainerContent(viewElement);
                        parser.next();
                    }
                    break;
                }
                case XmlPullParser.TEXT:
                    boolean textContainer = (container instanceof Hv2DomElement) &&
                            ((Hv2DomElement) container).componentType == Hv2DomContainer.ComponentType.TEXT;
                    boolean preceedingText = container.getLastChild() != null && (container.getLastChild() instanceof Text ||
                            (container.getLastChild() instanceof Hv2DomElement && ((Hv2DomElement) container.getLastChild()).componentType ==
                                    Hv2DomContainer.ComponentType.TEXT));
                    boolean preceedingBr = container.getLastChild() instanceof Hv2DomElement && ((Hv2DomElement) container.getLastChild()).getLocalName().equals("br");

                    String text = normalizeText(parser.getText(), !preceedingBr && (textContainer || preceedingText));
                    if (text.length() > 0) {
                        container.appendChild(document.createTextNode(text));
                    }
                    parser.next();
                    break;

                default:
                    throw new RuntimeException("Unexpected token: " + parser.getPositionDescription());
            }
        }
    }
}