package de.ovgu.featureide.fm.ui.utils;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A wrapper around a TreeItem with the ability to hide itself
 *
 * @author Robert Lag
 */
public class TreeItemData {

    private TreeItem treeItemParent;
    private Object data;
    private String text;
    private boolean checked;
    private boolean grayed;
    private Color foreground;
    private Font font;
    private Image image;

    public TreeItemData(TreeItem treeItemParent) {
        this.treeItemParent = treeItemParent;
    }

    public TreeItem getTreeItemParent() {
        return treeItemParent;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean getGrayed() {
        return grayed;
    }

    public void setGrayed(boolean grayed) {
        this.grayed = grayed;
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "TreeItemData{" +
                "treeItemParent=" + treeItemParent +
                ", data=" + data +
                ", text='" + text + '\'' +
                ", checked=" + checked +
                ", grayed=" + grayed +
                ", foreground=" + foreground +
                ", font=" + font +
                ", image=" + image +
                '}';
    }
}
