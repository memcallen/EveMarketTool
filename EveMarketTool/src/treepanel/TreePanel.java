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
