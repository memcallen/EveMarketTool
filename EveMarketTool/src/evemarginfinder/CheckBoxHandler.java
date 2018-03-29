/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evemarginfinder;

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
import javax.swing.JPanel;

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
    public static ItemGroup[] item_groups;
    public static HashMap<Integer, ItemGroup> itemgroup_lookup_id = new HashMap<>();
    public static Map.Entry<Integer, String>[] items = null;

    public static JCheckBox[] ItemGroups_CheckBoxes = null;
    public static JCheckBox[] Items_CheckBoxes = null;
    public static java.awt.GridLayout ItemGroups_Layout = new java.awt.GridLayout(0, 1);
    public static java.awt.GridLayout Items_Layout = new java.awt.GridLayout(0, 1);

    //the select queues
    public static ConcurrentLinkedQueue<Integer> item_queue = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Integer> group_queue = new ConcurrentLinkedQueue<>();

    //The deselect queues
    public static ConcurrentLinkedQueue<Integer> item_queue_des = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Integer> group_queue_des = new ConcurrentLinkedQueue<>();

    //the selections
    public final static List<Integer> item_selection = Collections.synchronizedList(new ArrayList<>());
    public final static List<Integer> group_selection = Collections.synchronizedList(new ArrayList<>());

    private static volatile boolean initialized = false;
    private static boolean items_changed = false;
    private static int[] id_cache;
    private static int[] group_cache;
    //</editor-fold>

    private static Thread main;

    private static boolean main_initialized = false;

    private static void checkSelections() {
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

    private static void getGroupItems(int start, IntStream.Builder items, IntStream.Builder groups) {

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

    public static int[] getItems() {

        if (id_cache == null) {
            return null;
        }

        //id_cache is only used to store the ids, it is never returned
        int[] array = new int[id_cache.length];
        System.arraycopy(id_cache, 0, array, 0, id_cache.length);
        return array;
    }

    public static int[] getGroups() {

        if (group_cache == null) {
            return null;
        }

        int[] array = new int[group_cache.length];
        System.arraycopy(group_cache, 0, array, 0, group_cache.length);
        return array;
    }

    public static void deselectAll() {

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

        while (!main_initialized){
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

    public static void initialize() {

        if (main != null) {
            if (main.isAlive()) {
                main.interrupt();
            }
        }

        main = new CheckBoxHandler();

        main.setName("CheckBox-Checker");

        main.setDaemon(true);

        main.start();

    }

    private static boolean contains(int[] array, int number) {
        for (int i : array) {
            if (i == number) {
                return true;
            }
        }
        return false;
    }

    private static void updateCaches() {

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

    }

    private static void updateCheckBoxes() {

        for (int i = 0; i < ItemGroups_CheckBoxes.length; i++) {
            ItemGroups_CheckBoxes[i].setSelected(contains(group_cache, item_groups[i].id));
        }

        for (int i = 0; i < Items_CheckBoxes.length; i++) {
            Items_CheckBoxes[i].setSelected(contains(id_cache, items[i].getKey()));
        }

        ItemGroups_CheckBoxes[0].getParent().repaint();
        Items_CheckBoxes[0].getParent().repaint();

    }

    public static void initializeCheckBoxes(JPanel item_group_panel, JPanel item_panel) {

        ConsoleFrame.log("Checkboxes - Initing stuff");

        item_group_panel.setLayout(ItemGroups_Layout);
        item_panel.setLayout(Items_Layout);

        ConsoleFrame.log("Checkboxes - Doing Groups");

        //Groups
        ItemGroups_CheckBoxes = new JCheckBox[item_groups.length];

        ItemGroups_Layout.setRows(item_groups.length + 1);

        int perc = item_groups.length / 20;

        long pre = System.currentTimeMillis();

        for (int i = 0; i < item_groups.length; i++) {
            ItemGroup curr = item_groups[i];

            JCheckBox box = new JCheckBox(curr.name);
            ItemGroups_CheckBoxes[i] = box;

            if (curr.superparent == -1) {
                box.setToolTipText(String.format("Id: %d, Is Super", curr.id));
            } else {
                box.setToolTipText(
                        String.format("Id: %d, Super: %s", curr.id,
                                itemgroup_lookup_id.get(curr.superparent).name
                        ));
            }

            box.addItemListener(new CheckBoxListener(curr.id, group_queue, group_queue_des));

            item_group_panel.add(box);

            if (i % perc == 0) {
                ConsoleFrame.log("Checkboxes - Group:" + Math.round(i / perc * 100) / 100 * 5 + "%");
            }

        }

        ConsoleFrame.log("Checkboxes - Group:100%");

        ConsoleFrame.log("Created Checkboxes:Groups in " + (System.currentTimeMillis() - pre) + " millis");

        ConsoleFrame.log("Checkboxes - Doing Items");

        //Items
        Items_CheckBoxes = new JCheckBox[items.length];

        Items_Layout.setRows(items.length + 1);

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

        ConsoleFrame.log("Created Checkboxes:Items in " + (System.currentTimeMillis() - pre) + " millis");

        main_initialized = true;

    }

    public static void setData(ItemGroup[] item_groups, Map.Entry<Integer, String>[] items,
            HashMap<Integer, ItemGroup> group_lookup) {
        CheckBoxHandler.item_groups = item_groups;
        CheckBoxHandler.items = items;
        CheckBoxHandler.itemgroup_lookup_id = group_lookup;
    }

}
