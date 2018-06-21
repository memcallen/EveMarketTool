package treepanel;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class HideableTreeNode extends TreeNode<Component> {

    public boolean visible = true, // if this component is visible
            actual_visible = true; // false if any parents are invisible, true otherwise

    private final JPanel panel = new JPanel();
    private final JLabel icon = new JLabel();
    private final Component comp;

    private final Icon closed, opened, leaf;

    public HideableTreeNode(Component comp) {
        super(comp);

        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setState(!visible);
            }
        });

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        panel.add(icon);
        panel.add(comp);
        
        this.comp = comp;

        closed = (Icon) UIManager.get("Tree.closedIcon");
        opened = (Icon) UIManager.get("Tree.openIcon");
        leaf = (Icon) UIManager.get("Tree.leafIcon");

        UpdateIcon();

    }

    public HideableTreeNode(Component comp, boolean visible) {
        this(comp);

        this.visible = visible;
    }

    public Component GetComp() {
        return comp;
    }
    
    @Override
    public Component GetValue() {
        return panel;
    }

    @Override
    public TreeNode<Component> Child(Component value) {
        HideableTreeNode node = new HideableTreeNode(value);

        AddChild(this, node);

        UpdateIcon();

        return node;
    }

    public static void AddChildSorted(HideableTreeNode parent, HideableTreeNode child, 
            Comparator<TreeNode<Component>> compare) {
        parent.AddChildSorted((TreeNode<Component>)child, compare);
    }

    /**
     * Update this node's visibility, and calls {@link UpdateState} on all
     * children
     *
     * @param visible if this node is visible
     */
    public void setState(boolean visible) {
        this.visible = visible;

        // update the visibility
        for (NodeIteratorInfo<Component> info : this) {
            info.node.UpdateState();
        }

    }

    public void UpdateIcon() {

        if (isLeaf()) {
            icon.setIcon(leaf);
        } else {
            icon.setIcon(visible ? opened : closed);
        }

        icon.setSize(icon.getIcon().getIconWidth(), icon.getIcon().getIconHeight());

    }

    /**
     * Updates the visibility of this node, based on the parents' visibility
     */
    @Override
    public void UpdateState() {

        HideableTreeNode hparent = AsHideable(parent);
        boolean parent_visible = true;

        //if the parent is valid, check its visibility
        if (hparent != null) {
            parent_visible = hparent.actual_visible;
        }

        //if the parent or this node isn't visible, this node isn't visible
        actual_visible = parent_visible && visible;

        //update the components eventually
        UpdateIcon();
        SwingUtilities.invokeLater(panel::revalidate);
    }

    public static HideableTreeNode AsHideable(TreeNode<Component> node) {
        if (node == null) {
            return null;
        }

        if (!HideableTreeNode.class.isAssignableFrom(node.getClass())) {
            return null;
        }

        return (HideableTreeNode) node;
    }

    public static boolean IsVisible(TreeNode<Component> node) {
        HideableTreeNode hnode = AsHideable(node.parent);

        return hnode != null ? hnode.actual_visible : true;

    }

}
