/*
 * The MIT License
 *
 * Copyright 2018 azalac0020.
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import javax.swing.Icon;

/**
 *
 * @author azalac0020
 */
public class TreeIcon implements Icon {

    private int width, height;
    private HideableTreeNode node;
    private int[] x_s = new int[3], y_s = new int[3];

    private static Point2D[] open = {
        new Point2D.Double(0.15, 0.5),
        new Point2D.Double(0.85, 0.5),
        new Point2D.Double(0.5, 0.85)
    };

    private static Point2D[] close = {
        new Point2D.Double(0.5, 0.85),
        new Point2D.Double(0.5, 0.15),
        new Point2D.Double(0.85, 0.5)
    };

    public TreeIcon(int width, int height, HideableTreeNode node) {
        this.width = width;
        this.height = height;
        this.node = node;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.gray);

        //draw nothing for leaves
        if (!node.isLeaf()) {
            if (node.actual_visible) {
                translate(open, x_s, y_s, x, y, width, height);
            } else {
                translate(close, x_s, y_s, x, y, width, height);
            }
            
            g.fillPolygon(x_s, y_s, 3);
        }
    }

    /**
     * Translates a set of points with domain 0..1 to an integer location
    */
    private static void translate(Point2D[] points, int[] x_s, int[] y_s, int x, int y, int width, int height) {
        if (points.length != x_s.length || x_s.length != y_s.length) {
            throw new IllegalArgumentException("Input arrays not of same size"
                    + "(p:" + points.length + ", x:" + x_s.length + ", y:"
                    + y_s.length + ")");
        }

        for (int i = 0; i < points.length; i++) {
            x_s[i] = (int) (x + width * points[i].getX());
            y_s[i] = (int) (y + height * points[i].getY());
        }
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

}
