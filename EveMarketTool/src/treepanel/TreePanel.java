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
import java.awt.LayoutManager;
import javax.swing.JPanel;
import treepanel.TreeNode.NodeIteratorInfo;

/**
 * I didn't use this, but it is very easy to use this in your own projects
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class TreePanel extends JPanel{
    
    private TreeNode<Component> root;
    private final TreePanelLayout layout;
    
    public TreePanel(TreeNode<Component> root) {
        layout = new TreePanelLayout(root);
        super.setLayout(layout);
        setRoot(root);
        UpdateChildren();
    }
    
    public final void setRoot(TreeNode<Component> root) {
        this.root = root;
        layout.setRoot(root);
    }
    
    public final void UpdateChildren() {
        this.removeAll();
        
        for(NodeIteratorInfo<Component> comp : root) {
            this.add(comp.value());
        }
    }
    
    @Override
    @Deprecated
    public final void setLayout(LayoutManager layout) {
        super.setLayout(layout);
    }
    
}
