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
package evemarginfinder;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import treepanel.HideableTreeNode;
import treepanel.TreeNode;
import treepanel.TreeNode.NodeIteratorInfo;
import treepanel.TreePanelLayout;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class CheckBoxHandler extends Thread {

    public static class CheckBoxListener implements ItemListener {

        private final int id;
        private final ConcurrentLinkedQueue<Integer> queue;
        private final ConcurrentLinkedQueue<Integer> des;

        /**
         * @param id The Id of this JCheckBox
         * @param queue The queue to add this id to on click
         * @param des The deselect queue to add this is to on click
         */
        public CheckBoxListener(int id, ConcurrentLinkedQueue<Integer> queue,
                ConcurrentLinkedQueue<Integer> des) {
            this.id = id;
            this.queue = queue;
            this.des = des;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {

            //when the checkbox state gets changed through setSelected, this listener gets called
            //this line prevents unnecessary recursion
            if (!((JCheckBox) e.getItem()).hasFocus()) {
                return;
            }

            if (e.getStateChange() == ItemEvent.DESELECTED) {
                des.add(id);
            } else {
                queue.add(id);
            }
        }

    }

    //<editor-fold defaultstate="collapsed" desc="Variable Definitions">
    //'final' definitions
    public ItemGroup[] item_groups;
    public HashMap<Integer, ItemGroup> itemgroup_lookup_id = new HashMap<>();
    public Map.Entry<Integer, String>[] items = null;

    //layout stuff
    public TreePanelLayout ItemGroups_Layout = new TreePanelLayout();
    public HideableTreeNode ItemGroups_Root;
    public HashMap<HideableTreeNode, ItemGroup> TreeNode_Lookup2 = new HashMap<>();
    public HashMap<ItemGroup, HideableTreeNode> TreeNode_Lookup = new HashMap<>();
    public JCheckBox[] ItemGroups_CheckBoxes = null;
    public JCheckBox[] Items_CheckBoxes = null;
    public java.awt.GridLayout Items_Layout = new java.awt.GridLayout(0, 1);
    public JTextField SelectedCount;

    //the select queues
    public ConcurrentLinkedQueue<Integer> item_queue = new ConcurrentLinkedQueue<>();
    public ConcurrentLinkedQueue<Integer> group_queue = new ConcurrentLinkedQueue<>();

    //The deselect queues
    public ConcurrentLinkedQueue<Integer> item_queue_des = new ConcurrentLinkedQueue<>();
    public ConcurrentLinkedQueue<Integer> group_queue_des = new ConcurrentLinkedQueue<>();

    //the selections
    public final List<Integer> item_selection = Collections.synchronizedList(new ArrayList<>());
    public final List<Integer> group_selection = Collections.synchronizedList(new ArrayList<>());

    //selection info
    private volatile boolean initialized = false;
    private boolean items_changed = false;
    private int[] id_cache;
    private int[] group_cache;
    //</editor-fold>

    private boolean main_initialized = false;

    public CheckBoxHandler(ItemGroup[] item_groups, Map.Entry<Integer, String>[] items,
            HashMap<Integer, ItemGroup> group_lookup) {

        setName("CheckBox-Checker");

        setDaemon(true);

        this.item_groups = item_groups;
        this.items = items;
        this.itemgroup_lookup_id = group_lookup;
    }

    private void checkSelections() {
        //adds items
        if (!item_queue.isEmpty()) {
            while (!item_queue.isEmpty()) {
                item_selection.add(item_queue.poll());
            }
            items_changed = true;
        }

        //adds groups
        if (!group_queue.isEmpty()) {
            Integer id;
            while (!group_queue.isEmpty()) {
                group_selection.add(group_queue.poll());
            }
            items_changed = true;
        }

        //removes items
        if (!item_queue_des.isEmpty()) {
            while (!item_queue_des.isEmpty()) {
                item_selection.remove(item_queue_des.poll());
            }
            items_changed = true;
        }

        //removes groups
        if (!group_queue_des.isEmpty()) {
            while (!group_queue_des.isEmpty()) {
                group_selection.remove(group_queue_des.poll());
            }
            items_changed = true;
        }

    }

    private void getGroupItems(int start, IntStream.Builder items, IntStream.Builder groups) {

        LinkedList<Integer> groupq = new LinkedList<>();

        groupq.push(start);

        while (!groupq.isEmpty()) {

            int id = groupq.pollFirst();
            groups.accept(id);
            ItemGroup group = itemgroup_lookup_id.get(id);

            for (Integer item : group.items) {
                items.accept(item);
            }

            for (Integer child : group.children) {
                groupq.push(child);
            }

        }

    }

    public int[] getItems() {

        if (id_cache == null) {
            return null;
        }

        //id_cache is only used to store the ids, it is never returned
        int[] array = new int[id_cache.length];
        System.arraycopy(id_cache, 0, array, 0, id_cache.length);
        return array;
    }

    /**
     * The fast version of {@link getItems}. DO NOT EDIT THE RETURNED ARRAY
     *
     * @return The id cache array
     */
    public int[] getItemsConst() {
        return id_cache;
    }

    public int[] getGroups() {

        if (group_cache == null) {
            return null;
        }

        int[] array = new int[group_cache.length];
        System.arraycopy(group_cache, 0, array, 0, group_cache.length);
        return array;
    }

    public void deselectAll() {

        if (items_changed) {
            updateCaches();
        }

        if (id_cache != null) {
            for (int id : id_cache) {
                item_queue_des.add(id);
            }
        }

        if (group_cache != null) {
            for (int group : group_cache) {
                group_queue_des.add(group);
            }
        }

    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {

        while (!main_initialized) {
            Thread.yield();
        }

        while (true) {

            checkSelections();

            if (items_changed) {

                updateCaches();

                updateCheckBoxes();

                items_changed = false;

            }

        }
    }

    private static boolean contains(int[] array, int number) {
        for (int i : array) {
            if (i == number) {
                return true;
            }
        }
        return false;
    }

    private void updateCaches() {

        IntStream.Builder item_stream = IntStream.builder();

        IntStream.Builder group_stream = IntStream.builder();

        for (Integer i : item_selection) {
            item_stream.accept(i);
        }

        for (Integer i : group_selection) {
            getGroupItems(i, item_stream, group_stream);
        }

        id_cache = item_stream.build().distinct().toArray();
        Arrays.sort(id_cache);

        group_cache = group_stream.build().distinct().toArray();
        Arrays.sort(group_cache);

        SelectedCount.setText(Integer.toString(id_cache.length + group_cache.length));

    }

    private void updateCheckBoxes() {

        for (int i = 0; i < ItemGroups_CheckBoxes.length; i++) {
            ItemGroups_CheckBoxes[i].setSelected(contains(group_cache, item_groups[i].id));
        }

        for (int i = 0; i < Items_CheckBoxes.length; i++) {
            Items_CheckBoxes[i].setSelected(contains(id_cache, items[i].getKey()));
        }

        ItemGroups_CheckBoxes[0].getParent().repaint();
        Items_CheckBoxes[0].getParent().repaint();

    }

    /**
     * Finds the next item group tree-wise after <after>
     *
     * @param substring The text to find in the itemgroup name
     * @param after The itemgroup to continue after, or null to start at the
     * beginning
     * @return The next itemgroup, or null at the end of the list
     * @throws IllegalArgumentException Not an actual exception, just a means of
     * giving the caller an error message which is not likely to be thrown
     * within the body of the function
     */
    public ItemGroup findGroup(String substring, ItemGroup after) throws IllegalArgumentException {

        //if after isnt null, it will look for the group
        boolean found = after == null;
        boolean anyfound = false;

        for (NodeIteratorInfo<Component> info : ItemGroups_Root) {

            ItemGroup ig = TreeNode_Lookup2.get(info.node);

            if (ig == null) {
                continue;
            }

            // this is a weird if layout, but it works
            if (ig.name.toLowerCase().contains(substring)) {
                anyfound = true;

                if (ig == after) {
                    found = true;
                    continue;
                }

                if (found) {
                    return ig;
                }
            }

        }

        throw new IllegalArgumentException(anyfound ? "Reached End Of Groups" : "No Matches Found");
    }

    /**
     * Sets a node's parents visible, and returns the node's component
     * information
     *
     * @param group The itemgroup to look for
     * @return The node, or null if no group found
     */
    public HideableTreeNode showBoxesTo(ItemGroup group) {

        HideableTreeNode group_node = TreeNode_Lookup.get(group);

        if (group_node == null) {
            return null;
        }

        HideableTreeNode retnode = group_node;

        while (group_node != null) {
            group_node.setState(true);
            if (group_node.parent instanceof HideableTreeNode) {
                group_node = (HideableTreeNode) group_node.parent;
            } else {
                break;
            }
        }

        return retnode;
    }

    //<editor-fold desc="Initialization Stuff" defaultstate="collapsed">
    public void initializeCheckBoxes(JPanel item_group_panel, JPanel item_panel) {

        ConsoleFrame.log("Checkboxes - Initing stuff");

        item_group_panel.setLayout(ItemGroups_Layout);
        item_panel.setLayout(Items_Layout);

        ConsoleFrame.log("Checkboxes - Initializing Groups");

        //Groups
        ItemGroups_CheckBoxes = new JCheckBox[item_groups.length];

        int perc = item_groups.length / 20;

        long pre = System.currentTimeMillis();

        for (int i = 0; i < item_groups.length; i++) {
            ItemGroup curr = item_groups[i];

            JCheckBox box = new JCheckBox(curr.name);
            ItemGroups_CheckBoxes[i] = box;

            if (curr.superparent == -1) {
                box.setToolTipText(String.format("Id: %d, Is Super", curr.id));
            } else {
                String name = itemgroup_lookup_id.get(curr.superparent).name;
                box.setToolTipText(String.format("Id: %d, Super: %s", curr.id, name));
            }

            HideableTreeNode node = new HideableTreeNode(box, false);
            TreeNode_Lookup.put(curr, node);
            TreeNode_Lookup2.put(node, curr);

            box.addItemListener(new CheckBoxListener(curr.id, group_queue, group_queue_des));

            if (i % perc == 0) {
                ConsoleFrame.log("Checkboxes - Group:" + Math.round(i / perc * 100) / 100 * 5 + "%");
            }

        }

        ConsoleFrame.log("Checkboxes - Group:100%");

        ConsoleFrame.log("Initialized Checkboxes-Groups in " + (System.currentTimeMillis() - pre) + " millis");

        buildCheckBoxTree(item_group_panel);

        ItemGroups_Layout.setRoot(ItemGroups_Root);

        ConsoleFrame.log("Checkboxes - Creating Items");

        //Items
        Items_CheckBoxes = new JCheckBox[items.length];

        Items_Layout.setRows(items.length);

        perc = items.length / 20;

        pre = System.currentTimeMillis();

        for (int i = 0; i < items.length; i++) {
            JCheckBox box = new JCheckBox(items[i].getValue());
            Items_CheckBoxes[i] = box;

            box.setToolTipText(items[i].getKey().toString());

            box.addItemListener(new CheckBoxListener(items[i].getKey(), item_queue, item_queue_des));

            item_panel.add(box);

            if (i % perc == 0) {
                ConsoleFrame.log("Checkboxes - Item:" + Math.round(i / perc * 100) / 100 * 5 + "%");
            }

        }

        ConsoleFrame.log("Checkboxes - Item:100%");

        ConsoleFrame.log("Created Checkboxes-Items in " + (System.currentTimeMillis() - pre) + " millis");

        main_initialized = true;

    }

    public void buildCheckBoxTree(JPanel tree_panel) {

        ConsoleFrame.log("Checkboxes - Configuring Groups");

        ItemGroups_Root = new HideableTreeNode(new JLabel("Root"));

        long pre = System.currentTimeMillis();

        tree_panel.add(ItemGroups_Root.GetValue());

        TreeNode_Lookup.entrySet().stream().forEach((e) -> {
            ItemGroup curr = e.getKey();
            HideableTreeNode node = e.getValue();

            // The parent for the current node
            HideableTreeNode parent = curr.issuper ? ItemGroups_Root
                    : TreeNode_Lookup.get(itemgroup_lookup_id.get(curr.parent));

            // sorts alphabetically, but doesn't group branches together
            parent.AddChildSorted(node, CheckBoxHandler::CompareCheckbox);

            tree_panel.add(node.GetValue());
        });

        TreeNode_Lookup.values().stream().filter(TreeNode::isBranch)
                .forEach((n) -> TreeNode.GroupChildren(n, CheckBoxHandler::MapCheckbox));

        // updates all nodes and their icons
        ItemGroups_Root.setState(true);

        ConsoleFrame.log("Checkboxes - Finished Configuring Groups (" + (System.currentTimeMillis() - pre) + " ms)");

    }

    public static int CompareCheckbox(TreeNode<Component> a, TreeNode<Component> b) {
        JCheckBox ca = (JCheckBox) ((HideableTreeNode) a).GetComp();
        JCheckBox cb = (JCheckBox) ((HideableTreeNode) b).GetComp();
        return ca.getText().toLowerCase().compareTo(cb.getText().toLowerCase());
    }

    public static int MapCheckbox(TreeNode<Component> a) {
        return a.isLeaf() ? 1 : 0;
    }

    public void setNumCounter(JTextField counter) {
        SelectedCount = counter;
    }

    //</editor-fold>
}
