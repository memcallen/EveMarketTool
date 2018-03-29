/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evemarginfinder;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.function.Supplier;

/**
 * TODO add author
 *
 * @author
 */
public class OptimizedScrollPaneLayout implements LayoutManager {

    private Supplier<Rectangle> get_bounds;

    public OptimizedScrollPaneLayout(Supplier<Rectangle> get_bounds) {
        this.get_bounds = get_bounds;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        //do nothing
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        //do nothing
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        int size = 0;
        int x = 0;
        for (Component c : parent.getComponents()) {
            Dimension d = c.getPreferredSize();
            if (d.width > x) {
                x = d.width;
            }
            size += d.height;
        }
        return new Dimension(x, size);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        int size = 0;
        int x = 0;
        for (Component c : parent.getComponents()) {
            Dimension d = c.getPreferredSize();
            if (d.width > x) {
                x = d.width;
            }
            size += d.height;
        }
        return new Dimension(x, size);
    }

    @Override
    public void layoutContainer(Container parent) {
        Rectangle bounds = get_bounds.get();
        int y = 0;
        if (bounds == null) {
            for (Component c : parent.getComponents()) {
                c.setLocation(0, y);
                Dimension size = c.getPreferredSize();
                c.setSize(size);
                y += size.height;
            }
        } else {
            for (Component c : parent.getComponents()) {
                Dimension psize = c.getPreferredSize();
                if (bounds.contains(0, y) || bounds.contains(psize.width, y + psize.height)) {
                    c.setLocation(0, y);
                    c.setSize(psize);
                    c.setVisible(true);
                } else {
                    c.setVisible(false);
                }
                y += c.getPreferredSize().height;
            }
        }
    }

}
