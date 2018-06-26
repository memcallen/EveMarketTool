/*
 * The MIT License
 *
 * Copyright 2018 memcallen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package treepanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.LinkedList;
import java.util.List;
import javax.swing.UIManager;
import treepanel.TreeNode.NodeIteratorInfo;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class TreePanelLayout implements LayoutManager {

    private TreeNode<Component> root;
    private int indent = (int) UIManager.get("Tree.leftChildIndent");
    private int horizontal_padding = 5, vertical_padding = (int) UIManager.get("Tree.rowHeight");

    private List<Runnable> listeners = new LinkedList<>();

    public TreePanelLayout() {
        this(null);
    }

    public TreePanelLayout(TreeNode root) {
        this.root = root;
    }

    @Override
    public void addLayoutComponent(String string, Component cmpnt) {
        // unused
    }

    @Override
    public void removeLayoutComponent(Component cmpnt) {
        // unused
    }

    @Override
    public Dimension minimumLayoutSize(Container cntnr) {
        return preferredLayoutSize(cntnr);
    }

    @Override
    public Dimension preferredLayoutSize(Container cntnr) {

        if (root == null) {
            return new Dimension(0, 0);
        }

        int width = 0, height = 0;
        int y = vertical_padding;

        for (NodeIteratorInfo<Component> info : root) {

            if (HideableTreeNode.IsVisible(info.node)) {
                Component comp = info.value();

                Dimension size = comp.getPreferredSize();

                if (size == null) {
                    size = comp.getSize();
                }

                if (size == null) {
                    throw new NullPointerException("Component had invalid size");
                }

                int comp_width = indent * info.node_depth + size.width + horizontal_padding;

                if (comp_width > width) {
                    width = comp_width;
                }

                height += size.height + vertical_padding * 2;
            }
        }

        return new Dimension(width + horizontal_padding, height);
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void layoutContainer(Container cntnr) {

        if (root == null) {
            return;
        }

        int y = vertical_padding;

        for (NodeIteratorInfo<Component> info : root) {

            if (HideableTreeNode.IsVisible(info.node)) {
                Component comp = info.value();
                info.value().setVisible(true);

                Dimension size = comp.getPreferredSize();

                if (size == null) {
                    size = comp.getSize();
                }

                if (size == null) {
                    throw new NullPointerException("Component " + comp.getName() + " had invalid size");
                }

                comp.setLocation(indent * info.node_depth + horizontal_padding, y);
                comp.setSize(size);

                y += size.height + vertical_padding * 2;
            } else {
                info.value().setVisible(false);
            }

        }

        try {
            listeners.stream().forEach(Runnable::run);
        } catch (Exception e) {
            System.err.println("Caught exception during TreePanelLayout events:");
            e.printStackTrace();
        }

    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public void addLayoutEventListener(Runnable r) {
        listeners.add(r);
    }

}
