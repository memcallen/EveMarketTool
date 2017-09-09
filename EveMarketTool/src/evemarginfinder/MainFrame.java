
package evemarginfinder;

import evemarginfinder.DatabaseManager.CheckBoxListener;
import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.stream.Stream;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class MainFrame extends javax.swing.JFrame {

    public class CustomCellRenderer extends DefaultTableCellRenderer {

        public double min_margin, max_cost;
        public boolean active = false;
        public Color[] colors = {Color.RED, Color.GREEN};

        public CustomCellRenderer(double min_margin, double max_cost) {
            this.min_margin = min_margin;
            this.max_cost = max_cost;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {

            Component c = super.getTableCellRendererComponent(table,
                    value, isSelected, hasFocus, row, column);

            if (active) {
                double margin = (Double) table.getValueAt(row, 1);
                double cost = (Double) table.getValueAt(row, 2);

                if (margin >= min_margin && cost <= max_cost) {
                    c.setBackground(colors[0]);
                }
            }
            return c;
        }

    }

    public Entry<Integer, String>[] ItemGroups = null;
    public Entry<Integer, String>[] Items = null;

    public JCheckBox[] ItemGroups_CheckBoxes = null;
    public JCheckBox[] Items_CheckBoxes = null;
    public java.awt.GridLayout ItemGroups_Layout = new java.awt.GridLayout(0, 1);
    public java.awt.GridLayout Items_Layout = new java.awt.GridLayout(0, 1);
    public DefaultListModel model = new DefaultListModel();
    public DefaultTableModel table_model;
    public CustomCellRenderer cell = new CustomCellRenderer(0.1, 10000000);

    public Vector<String> headers = new Vector();
    public Vector<Vector> output_table_data = new Vector();

    public int group_search_index = 0;
    public int item_search_index = 0;

    private static int sysid = 30000142;

    private static Thread current_query;

    public MainFrame(Entry<Integer, String>[] entries_groups, Entry<Integer, String>[] entries_items) {

        System.out.println("Loading laf");
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        System.out.println("Doing table headers");
        headers.add("Name");//string
        headers.add("Margin");//double
        headers.add("Cost");//double
        headers.add("Profit");//double
        headers.add("Volume");//int

        table_model = new DefaultTableModel(output_table_data, headers) {

            Class[] clazz = new Class[]{String.class, Double.class, Double.class, Double.class, Integer.class};

            @Override
            public Class<?> getColumnClass(int column) {
                return clazz[column];
            }

        };

        System.out.println("Initing components");
        initComponents();

        TableRowSorter<TableModel> sorter = new TableRowSorter(output_table.getModel());

        output_table.setRowSorter(sorter);

        ItemGroups = entries_groups;
        Items = entries_items;

        System.out.println("Initing Check Boxes");
        initializeCheckBoxes();

        System.out.println("Packing");
        pack();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox2 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        TabbedPane = new javax.swing.JTabbedPane();
        SelectionPanel = new javax.swing.JPanel();
        GroupScroll = new javax.swing.JScrollPane();
        ItemGroupPanel = new javax.swing.JPanel();
        ItemScroll = new javax.swing.JScrollPane();
        ItemPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ItemGroupSearch = new javax.swing.JTextField();
        ItemSearch = new javax.swing.JTextField();
        deselect = new javax.swing.JButton();
        InfoPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        load_items = new javax.swing.JButton();
        min_margin_field = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        max_cost_field = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        selected_items = new javax.swing.JList();
        selected_items_refresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        output_table = new javax.swing.JTable(table_model);
        use_filter = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        system_id = new javax.swing.JTextField();
        rem_inv = new javax.swing.JCheckBox();

        jCheckBox2.setText("jCheckBox2");

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Eve Market Tool");
        setLocationByPlatform(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridLayout(1, 1));

        TabbedPane.setFont(TabbedPane.getFont());
        TabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TabbedPaneStateChanged(evt);
            }
        });

        SelectionPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ItemGroupPanel.setLayout(new java.awt.GridLayout(100, 1));
        GroupScroll.setViewportView(ItemGroupPanel);

        SelectionPanel.add(GroupScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 340, 350));

        ItemPanel.setLayout(new java.awt.GridLayout(100, 1));
        ItemScroll.setViewportView(ItemPanel);

        SelectionPanel.add(ItemScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 40, 350, 360));

        jLabel1.setFont(jLabel1.getFont());
        jLabel1.setText("Groups");
        SelectionPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 60, -1));

        jLabel2.setFont(jLabel2.getFont());
        jLabel2.setText("Items");
        SelectionPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 10, 40, -1));

        ItemGroupSearch.setFont(ItemGroupSearch.getFont());
        ItemGroupSearch.setText("Item Group Search");
        ItemGroupSearch.setToolTipText("Item Group Search");
        ItemGroupSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ItemGroupSearchKeyTyped(evt);
            }
        });
        SelectionPanel.add(ItemGroupSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 140, -1));

        ItemSearch.setFont(ItemSearch.getFont());
        ItemSearch.setText("Item Search");
        ItemSearch.setToolTipText("Item Search");
        ItemSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ItemSearchKeyTyped(evt);
            }
        });
        SelectionPanel.add(ItemSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 120, -1));

        deselect.setFont(deselect.getFont());
        deselect.setText("Deselect All");
        deselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectActionPerformed(evt);
            }
        });
        SelectionPanel.add(deselect, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, -1, -1));

        TabbedPane.addTab("Select Groups", SelectionPanel);

        InfoPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(jLabel3.getFont());
        jLabel3.setText("Selected Items:");
        InfoPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, 20));

        load_items.setFont(load_items.getFont());
        load_items.setText("Load");
        load_items.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                load_itemsActionPerformed(evt);
            }
        });
        InfoPanel.add(load_items, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 300, -1, -1));

        min_margin_field.setText("0.1");
        InfoPanel.add(min_margin_field, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, 80, -1));

        jLabel4.setFont(jLabel4.getFont());
        jLabel4.setText("Min Margin %");
        InfoPanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 100, -1, -1));

        jLabel5.setFont(jLabel5.getFont());
        jLabel5.setText("Maximum Cost");
        InfoPanel.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 150, -1, -1));

        max_cost_field.setText("10000000");
        InfoPanel.add(max_cost_field, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, 80, -1));

        selected_items.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        selected_items.setModel(model);
        jScrollPane3.setViewportView(selected_items);

        InfoPanel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 120, 360));

        selected_items_refresh.setFont(selected_items_refresh.getFont());
        selected_items_refresh.setText("Refresh");
        selected_items_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selected_items_refreshActionPerformed(evt);
            }
        });
        InfoPanel.add(selected_items_refresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, -1, -1));

        jScrollPane1.setViewportView(output_table);

        InfoPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 500, 390));

        use_filter.setFont(use_filter.getFont());
        use_filter.setText("Use Filter");
        use_filter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                use_filterActionPerformed(evt);
            }
        });
        InfoPanel.add(use_filter, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 250, -1, -1));

        jLabel8.setFont(jLabel8.getFont());
        jLabel8.setText("System");
        InfoPanel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, -1, -1));

        system_id.setText("30000142");
        system_id.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                system_idFocusLost(evt);
            }
        });
        system_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                system_idKeyTyped(evt);
            }
        });
        InfoPanel.add(system_id, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 220, 80, -1));

        rem_inv.setFont(rem_inv.getFont());
        rem_inv.setText("Remove Invalids");
        InfoPanel.add(rem_inv, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 270, -1, -1));

        TabbedPane.addTab("Info", InfoPanel);

        getContentPane().add(TabbedPane);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectActionPerformed

        for (int i = 0; i < ItemGroups_CheckBoxes.length; i++) {
            JCheckBox box = ItemGroups_CheckBoxes[i];

            if (box.isSelected()) {
                box.setSelected(false);
            }

        }

        for (int i = 0; i < Items_CheckBoxes.length; i++) {
            JCheckBox box = Items_CheckBoxes[i];

            if (box.isSelected()) {
                box.setSelected(false);
            }

        }

    }//GEN-LAST:event_deselectActionPerformed

    private void ItemGroupSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ItemGroupSearchKeyTyped

        if (evt.getKeyChar() == '\n') {
            int found = 0;
            for (JCheckBox box : ItemGroups_CheckBoxes) {
                if (box.getText().toLowerCase().contains(ItemGroupSearch.getText().toLowerCase())) {
                    found++;
                    if (found > group_search_index) {
                        group_search_index++;
                        GroupScroll.getVerticalScrollBar().setValue(box.getY());
                        return;
                    }
                }
            }

        }
        group_search_index = 0;

    }//GEN-LAST:event_ItemGroupSearchKeyTyped

    private void TabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TabbedPaneStateChanged

        selected_items_refresh.doClick();

    }//GEN-LAST:event_TabbedPaneStateChanged

    private void selected_items_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selected_items_refreshActionPerformed

        model.clear();

        for (int i = 0; i < Items_CheckBoxes.length; i++) {
            JCheckBox box = Items_CheckBoxes[i];
            if (box.isSelected()) {
                model.addElement(box.getText());
            }

        }

        selected_items.repaint();

    }//GEN-LAST:event_selected_items_refreshActionPerformed

    private void load_itemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_load_itemsActionPerformed

        if (current_query != null) {
            if (current_query.isAlive()) {
                current_query.interrupt();
            }
        }

        current_query = new Thread(() -> {

            double min_margin = 0.1;
            try {
                min_margin = Double.valueOf(min_margin_field.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: Min Margin (" + min_margin_field.getText() + ") needs to be a number");
            }

            double max_cost = 10000000;
            try {
                max_cost = Double.valueOf(max_cost_field.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: Max Cost (" + max_cost_field.getText() + ") needs to be a number");
            }

            cell.min_margin = min_margin;
            cell.max_cost = max_cost;

            output_table_data.clear();

            output_table_data.setSize(0);

            int[] ids = Stream.of(model.toArray())
                    .map(Object::toString)
                    .mapToInt(DatabaseManager::queryItemId)
                    .toArray();

            int typesperquery = 20;

            if (ids.length > typesperquery) {

                int i = ids.length;

                while (i > 0) {

                    int[] subids = Arrays.copyOfRange(ids, Math.max(0, i - typesperquery), i);

                    output_table_data.addAll(DatabaseManager.getMarketInfoBulk(subids, sysid));

                    i -= typesperquery;
                }

            } else {
                output_table_data.addAll(DatabaseManager.getMarketInfoBulk(ids, sysid));
            }

            if (rem_inv.isSelected()) {
                output_table_data.removeIf(v -> Double.isNaN((double) v.get(1)) || Double.isInfinite((double) v.get(1)));
            }

            output_table.revalidate();

            current_query.interrupt();
        });

        current_query.setName("query-thread");

        current_query.start();
    }//GEN-LAST:event_load_itemsActionPerformed

    private void ItemSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ItemSearchKeyTyped

        if (evt.getKeyChar() == '\n') {
            int found = 0;
            for (JCheckBox box : Items_CheckBoxes) {
                if (box.getText().toLowerCase().contains(ItemSearch.getText().toLowerCase())) {
                    found++;
                    if (found > item_search_index) {
                        item_search_index++;
                        ItemScroll.getVerticalScrollBar().setValue(box.getY());
                        return;
                    }
                }
            }
        }

        item_search_index = 0;

    }//GEN-LAST:event_ItemSearchKeyTyped

    private void use_filterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_use_filterActionPerformed

        cell.setActive(use_filter.isSelected());

        output_table.revalidate();

    }//GEN-LAST:event_use_filterActionPerformed

    private void system_idKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_system_idKeyTyped

        if (evt.getKeyChar() == '\n') {
            
            this.requestFocusInWindow();//hacky way to call focus_lost (also looks better)

        }


    }//GEN-LAST:event_system_idKeyTyped

    private void system_idFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_system_idFocusLost

        try {
            sysid = Integer.valueOf(system_id.getText());

            system_id.setToolTipText(DatabaseManager.systems.get(sysid));
        } catch (NumberFormatException _e) {

            String text = system_id.getText();

            Entry<Integer, String> ent = DatabaseManager.systems.entrySet().stream()
                    .filter(e -> e.getValue().equalsIgnoreCase(text))
                    .findAny().orElse(null);

            if (ent == null) {
                JOptionPane.showMessageDialog(this, "Error: System could not be interpreted");
                return;
            }

            sysid = ent.getKey();
            system_id.setToolTipText(sysid + "");

        }
    }//GEN-LAST:event_system_idFocusLost

    public void setSelectedGroup(int id, boolean selected) {
        for (int i = 0; i < ItemGroups_CheckBoxes.length; i++) {
            JCheckBox box = ItemGroups_CheckBoxes[i];
            if (box.getToolTipText().equals(Integer.toString(id))) {
                box.setSelected(selected);
                return;
            }
        }
    }

    public void setSelectedItem(int id, boolean selected) {
        for (int i = 0; i < Items_CheckBoxes.length; i++) {
            JCheckBox box = Items_CheckBoxes[i];

            if (box.getToolTipText().equals(Integer.toString(id))) {
                box.setSelected(selected);
                return;
            }
        }
    }

    public void initializeCheckBoxes() {

        System.out.println("Checkboxes - Initing stuff");

        ItemGroupPanel.setLayout(ItemGroups_Layout);
        ItemPanel.setLayout(Items_Layout);

        CheckBoxListener group = new CheckBoxListener(true);
        CheckBoxListener item = new CheckBoxListener(false);

        System.out.println("Checkboxes - Doing Groups");

        //Groups
        ItemGroups_CheckBoxes = new JCheckBox[ItemGroups.length];

        ItemGroups_Layout.setRows(ItemGroups.length + 1);

        int perc = ItemGroups.length / 20;

        long pre = System.currentTimeMillis();

        for (int i = 0; i < ItemGroups.length; i++) {
            JCheckBox box = new JCheckBox(ItemGroups[i].getValue());
            ItemGroups_CheckBoxes[i] = box;

            box.setToolTipText(ItemGroups[i].getKey().toString());

            box.addItemListener(group);

            ItemGroupPanel.add(box);

            if (i % perc == 0) {
                System.out.println("Checkboxes - Group:" + Math.round(i / perc * 100) / 100 * 5);
            }

        }

        System.out.println("Created Checkboxes:Groups in " + (System.currentTimeMillis() - pre) + " millis");

        System.out.println("Checkboxes - Doing Items");
        //Items
        Items_CheckBoxes = new JCheckBox[Items.length];

        Items_Layout.setRows(Items.length + 1);

        perc = Items.length / 20;

        pre = System.currentTimeMillis();

        for (int i = 0; i < Items.length; i++) {
            JCheckBox box = new JCheckBox(Items[i].getValue());
            Items_CheckBoxes[i] = box;

            box.setToolTipText(Items[i].getKey().toString());

            box.addItemListener(item);

            ItemPanel.add(box);

            if (i % perc == 0) {
                System.out.println("Checkboxes - Item:" + Math.round(i / perc * 100) / 100 * 5);
            }

        }

        System.out.println("Created Checkboxes:Items in " + (System.currentTimeMillis() - pre) + " millis");

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane GroupScroll;
    private javax.swing.JPanel InfoPanel;
    private javax.swing.JPanel ItemGroupPanel;
    private javax.swing.JTextField ItemGroupSearch;
    private javax.swing.JPanel ItemPanel;
    private javax.swing.JScrollPane ItemScroll;
    private javax.swing.JTextField ItemSearch;
    private javax.swing.JPanel SelectionPanel;
    private javax.swing.JTabbedPane TabbedPane;
    private javax.swing.JButton deselect;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton load_items;
    private javax.swing.JTextField max_cost_field;
    private javax.swing.JTextField min_margin_field;
    private javax.swing.JTable output_table;
    private javax.swing.JCheckBox rem_inv;
    private javax.swing.JList selected_items;
    private javax.swing.JButton selected_items_refresh;
    private javax.swing.JTextField system_id;
    private javax.swing.JCheckBox use_filter;
    // End of variables declaration//GEN-END:variables
}
