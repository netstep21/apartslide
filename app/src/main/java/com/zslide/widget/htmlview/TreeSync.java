package com.zslide.widget.htmlview;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.zslide.utils.DisplayUtil;
import com.zslide.widget.htmlview.dom.Node;
import com.zslide.widget.htmlview.dom.Text;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Synchronizes the physical view hierarchy to the DOM.
 */
class TreeSync {

    static void syncContainer(HtmlViewGroup physicalContainer, Hv2DomContainer logicalContainer, boolean clear) {
        if (logicalContainer.syncState == Hv2DomContainer.SyncState.SYNCED) {
            return;
        }
        logicalContainer.syncState = Hv2DomContainer.SyncState.SYNCED;
        if (clear) {
            physicalContainer.removeAllViews();
        }
        HtmlTextView pendingText = null;
        Node child = logicalContainer.getFirstChild();
        while (child != null) {
            pendingText = syncChild(physicalContainer, child, pendingText);
            child = child.getNextSibling();
        }
    }

    static HtmlTextView syncChild(HtmlViewGroup physicalContainer, Node childNode, HtmlTextView pendingText) {
        if (childNode instanceof Text) {
            Hv2DomText text = (Hv2DomText) childNode;
            if (pendingText == null) {
                pendingText = new HtmlTextView(physicalContainer.htmlView);
                physicalContainer.addView(pendingText);
            }
            pendingText.append(text.getData());
        } else {
            Hv2DomElement element = (Hv2DomElement) childNode;
            switch (((Hv2DomContainer) childNode).componentType) {
                case IMAGE:
                case TEXT: {
                    if (pendingText == null) {
                        pendingText = new HtmlTextView(physicalContainer.htmlView);
                        physicalContainer.addView(pendingText);
                    }
                    pendingText = syncTextElement(((Hv2DomElement) childNode), physicalContainer, pendingText);
                    break;
                }
                case LOGICAL_CONTAINER:
                    syncContainer(physicalContainer, element, false);
                    break;
                case PHYSICAL_CONTAINER: {
                    HtmlViewGroup childGroup = (HtmlViewGroup) element.getView();
                    physicalContainer.addView(childGroup);
                    ((HtmlViewGroup.LayoutParams) childGroup.getLayoutParams()).element = element;
                    syncContainer(childGroup, element, true);
                    break;
                }
                case LEAF_COMPONENT: {
                    View childView = element.getView();
                    physicalContainer.addView(childView);
                    ((HtmlViewGroup.LayoutParams) childView.getLayoutParams()).element = element;

                    if (childView instanceof Spinner) {
                        syncSelect((Spinner) childView, element);
                    }

                    break;
                }
                default:
                    // No action needed.
            }
        }
        return pendingText;
    }


    static void syncSelect(Spinner spinner, Hv2DomElement element) {
        ArrayAdapter<String> options = new ArrayAdapter<String>(spinner.getContext(),
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(options);

        Hv2DomElement child = element.getFirstElementChild();
        while (child != null) {
            if (child.getLocalName().equals("option")) {
                boolean selected = child.getAttribute("selected") != null;
                String content = child.getTextContent();
                options.add(content);
                if (child.getAttribute("selected") != null) {
                    spinner.setSelection(options.getCount() - 1);
                }
                child = child.getNextElementSibling();
            }
        }
    }

    static HtmlTextView syncTextElement(Hv2DomElement element, HtmlViewGroup physicalContainer, HtmlTextView htmlTextView) {
        element.sections = new SpanCollection(element, htmlTextView);

        if (element.getLocalName().equals("img")) {
            ImageView imageView = new ImageView(physicalContainer.getContext());
            imageView.setMinimumWidth(DisplayUtil.getScreenWidth(physicalContainer.getContext()));
            physicalContainer.addView(imageView);
            String src = element.getAttribute("src");
            if (src != null) {
                DrawableTypeRequest<String> request = Glide.with(physicalContainer.getContext()).load(src);
                if (src.endsWith("gif")) {
                    request.asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(imageView);
                } else {
                    request.into(imageView);
                }
            }
        } else if (element.getLocalName().equals("br")) {
            htmlTextView.hasLineBreaks = true;
            htmlTextView.append("\u200b");
            htmlTextView.pendingBreakPosition = htmlTextView.content.length() - 1;
        } else {
            Hv2DomNode child = element.getFirstChild();
            while (child != null) {
                if (child instanceof Hv2DomText) {
                    htmlTextView.append(((Hv2DomText) child).text);
                } else {
                    Hv2DomElement childElement = (Hv2DomElement) child;
                    if (childElement.componentType == Hv2DomContainer.ComponentType.TEXT ||
                            childElement.componentType == Hv2DomContainer.ComponentType.IMAGE ||
                            childElement.componentType == Hv2DomContainer.ComponentType.LOGICAL_CONTAINER) {
                        syncTextElement(((Hv2DomElement) child), physicalContainer, htmlTextView);
                    } else if (childElement.componentType == Hv2DomContainer.ComponentType.VIDEO) {
                        // TODO:
                    } else {
                        // Physical container or leaf component.
                        syncChild(physicalContainer, child, null);
                        htmlTextView = new HtmlTextView(physicalContainer.htmlView);
                        reopenText(element, htmlTextView);
                    }
                }
                child = child.getNextSibling();
            }
        }
        element.sections.end = htmlTextView.content.length();
        return htmlTextView;
    }

    static void reopenText(Hv2DomElement element, HtmlTextView htmlTextView) {
        element.sections.end = element.sections.htmlTextView.content.length();
        SpanCollection next = new SpanCollection(element, htmlTextView);
        next.previous = element.sections;
        element.sections = next;
        if (element.parentNode.componentType == Hv2DomContainer.ComponentType.TEXT) {
            reopenText(((Hv2DomElement) element.parentNode), htmlTextView);
        }
    }

}