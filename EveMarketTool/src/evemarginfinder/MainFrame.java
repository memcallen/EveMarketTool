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
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public final class MainFrame extends javax.swing.JFrame {

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
    
    public final Vector<Vector> output_table_data = new Vector();
    public TableCellRenderer cell = new TableCellRenderer(output_table_data);


    public int group_search_index = 0;
    public int item_search_index = 0;

    private static int sysid = 30000142;

    private static Thread current_query;

    public MainFrame(Entry<Integer, String>[] entries_groups, Entry<Integer, String>[] entries_items) {

        ConsoleFrame.log("Loading laf");
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

        ConsoleFrame.log("Doing table stuff");

        table_model = new DefaultTableModel(output_table_data, ConfigManager.table_headers) {

            @Override
            public Class<?> getColumnClass(int column) {
                return ConfigManager.table_classes[column];
            }

        };

        ConsoleFrame.log("Initing components");
        initComponents();

        TableColumnModel tcm = output_table.getColumnModel();
        
        for(int i = 0; i < tcm.getColumnCount(); i++){
            tcm.getColumn(i).setCellRenderer(cell);
        }
        
        TableRowSorter<TableModel> sorter = new TableRowSorter(output_table.getModel());

        output_table.setRowSorter(sorter);

        ItemGroups = entries_groups;
        Items = entries_items;

        ConsoleFrame.log("Initing Check Boxes");
        initializeCheckBoxes();

        ConsoleFrame.log("Packing");
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
        SettingsPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        cfg_format = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cfg_url = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        cfg_idroot = new javax.swing.JTextField();
        cfg_id = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cfg_reg = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cfg_stat = new javax.swing.JTextField();
        cfg_save = new javax.swing.JButton();
        cfg_reset = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        parse_decoder = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        parse_table = new javax.swing.JComboBox<>();
        parse_reload = new javax.swing.JButton();
        cfg_reload = new javax.swing.JButton();
        cfg_write = new javax.swing.JButton();

        jCheckBox2.setText("jCheckBox2");

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Eve Market Tool");
        setLocationByPlatform(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                onexit(evt);
            }
        });
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

        SelectionPanel.add(ItemScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 40, 350, 350));

        jLabel1.setFont(jLabel1.getFont());
        jLabel1.setText("Groups");
        SelectionPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 60, -1));

        jLabel2.setFont(jLabel2.getFont());
        jLabel2.setText("Items");
        SelectionPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 10, 40, -1));

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
        SelectionPanel.add(ItemSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 10, 120, -1));

        deselect.setFont(deselect.getFont());
        deselect.setText("Deselect All");
        deselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectActionPerformed(evt);
            }
        });
        SelectionPanel.add(deselect, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, -1, -1));

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
        InfoPanel.add(selected_items_refresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, -1, -1));

        jScrollPane1.setDoubleBuffered(true);

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
        jLabel8.setText("Station/Region");
        InfoPanel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, -1, -1));

        system_id.setText("60003760");
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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Query URL Formatting"));

        jLabel6.setText("URL Format:");

        cfg_format.setText("{0}?{1}&{2}");
        cfg_format.setToolTipText("{0}=url, {1}=typeid, {2}=system/region");

        jLabel7.setText("URL:");

        cfg_url.setText("https://market.fuzzwork.co.uk/aggregates/");

        jLabel9.setText("TypeID Format:");

        cfg_idroot.setText("types={0}");
        cfg_idroot.setToolTipText("Root Format");

        cfg_id.setText("{0},");
        cfg_id.setToolTipText("Format for each number (concatted)");

        jLabel10.setText("Region Format:");

        cfg_reg.setText("region={0}");

        jLabel11.setText("Station Format:");

        cfg_stat.setText("station={0}");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cfg_format, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cfg_url, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cfg_idroot)
                            .addComponent(cfg_reg)
                            .addComponent(cfg_stat, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                            .addComponent(cfg_id))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(cfg_format, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cfg_url, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cfg_idroot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cfg_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cfg_reg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(cfg_stat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cfg_save.setText("Save");
        cfg_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cfg_saveActionPerformed(evt);
            }
        });

        cfg_reset.setText("Reset To Defaults");
        cfg_reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cfg_resetActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Response Parsing"));

        parse_decoder.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fuzzworks" }));
        parse_decoder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parse_decoderActionPerformed(evt);
            }
        });

        jLabel12.setText("API Decoder:");

        jLabel13.setText("Table Generator:");

        parse_table.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Generic Table" }));
        parse_table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parse_tableActionPerformed(evt);
            }
        });

        parse_reload.setText("Reload Parser Configs");
        parse_reload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parse_reloadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addGap(9, 9, 9)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(parse_decoder, 0, 148, Short.MAX_VALUE)
                    .addComponent(parse_table, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(95, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(parse_reload)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(parse_decoder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(parse_table, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(parse_reload)
                .addContainerGap())
        );

        cfg_reload.setText("Reload From File");
        cfg_reload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cfg_reloadActionPerformed(evt);
            }
        });

        cfg_write.setText("Write To File");
        cfg_write.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cfg_writeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SettingsPanelLayout = new javax.swing.GroupLayout(SettingsPanel);
        SettingsPanel.setLayout(SettingsPanelLayout);
        SettingsPanelLayout.setHorizontalGroup(
            SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SettingsPanelLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SettingsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cfg_reset)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cfg_reload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cfg_write)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cfg_save)))
                .addContainerGap())
        );
        SettingsPanelLayout.setVerticalGroup(
            SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cfg_save)
                    .addComponent(cfg_reload)
                    .addComponent(cfg_write)
                    .addComponent(cfg_reset))
                .addContainerGap())
        );

        TabbedPane.addTab("Settings", SettingsPanel);

        getContentPane().add(TabbedPane);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectActionPerformed

        for (JCheckBox box : ItemGroups_CheckBoxes) {
            if (box.isSelected()) {
                box.setSelected(false);
            }
        }

        for (JCheckBox box : Items_CheckBoxes) {
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

        for (JCheckBox box : Items_CheckBoxes) {
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

            output_table_data.clear();

            output_table_data.setSize(0);

            sysid = Integer.valueOf(system_id.getText());

            int[] ids = Stream.of(model.toArray())
                    .map(Object::toString)
                    .mapToInt(DatabaseManager::queryItemId)
                    .toArray();

            int numperquery = 20;

            if (ids.length > numperquery) {

                int i = ids.length;

                while (i > 0) {

                    int[] subids = Arrays.copyOfRange(ids, Math.max(0, i - numperquery), i);

                    output_table_data.addAll(DatabaseManager.getMarketInfoBulk(subids, sysid));

                    i -= numperquery;
                    
                }

            } else {
                output_table_data.addAll(DatabaseManager.getMarketInfoBulk(ids, sysid));
            }

            if (rem_inv.isSelected()) {
                output_table_data.removeIf(v -> Double.isNaN((double) v.get(1)) || Double.isInfinite((double) v.get(1)));
            }

            output_table.revalidate();

            output_table.repaint();
            
            output_table.doLayout();
            
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

        //TODO replace this
        //cell.setActive(use_filter.isSelected());

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

    private void cfg_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cfg_saveActionPerformed
        ConfigManager.set("uformat", cfg_format.getText());
        ConfigManager.set("url", cfg_url.getText());
        ConfigManager.set("typeroot", cfg_idroot.getText());
        ConfigManager.set("type", cfg_id.getText());
        ConfigManager.set("region", cfg_reg.getText());
        ConfigManager.set("station", cfg_stat.getText());
    }//GEN-LAST:event_cfg_saveActionPerformed

    private void cfg_resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cfg_resetActionPerformed

        ConfigManager.reset();

        refreshConfigVisuals();
    }//GEN-LAST:event_cfg_resetActionPerformed

    private void parse_decoderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parse_decoderActionPerformed
        
        if(parse_decoder.getItemCount() == 0){
            return;
        }
        
        String item = parse_decoder.getSelectedItem().toString();
        
        try{
            ConfigManager.setActiveParser(item);
        }catch(NullPointerException e){
            JOptionPane.showMessageDialog(this, "Error: that decoder doesn't exist, this should never happen", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_parse_decoderActionPerformed

    private void onexit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onexit
        ConfigManager.save();
    }//GEN-LAST:event_onexit

    private void cfg_reloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cfg_reloadActionPerformed
        ConfigManager.load();

        refreshConfigVisuals();
    }//GEN-LAST:event_cfg_reloadActionPerformed

    private void parse_reloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parse_reloadActionPerformed
        ConfigManager.loadParsers();

        refreshConfigVisuals();
    }//GEN-LAST:event_parse_reloadActionPerformed

    private void parse_tableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parse_tableActionPerformed
        
        if(parse_table.getItemCount() == 0){
            return;
        }
        
        String item = parse_table.getSelectedItem().toString();
        
        try{
            ConfigManager.setActiveTable(item);
        }catch(NullPointerException e){
            JOptionPane.showMessageDialog(this, "Error: that table generator doesn't exist, this should never happen", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_parse_tableActionPerformed

    private void cfg_writeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cfg_writeActionPerformed
        
        this.cfg_saveActionPerformed(null);
        
        ConfigManager.save();
    }//GEN-LAST:event_cfg_writeActionPerformed

    public void refreshConfigVisuals() {
        cfg_format.setText(ConfigManager.get("uformat"));
        cfg_url.setText(ConfigManager.get("url"));
        cfg_idroot.setText(ConfigManager.get("typeroot"));
        cfg_id.setText(ConfigManager.get("type"));
        cfg_reg.setText(ConfigManager.get("region"));
        cfg_stat.setText(ConfigManager.get("station"));

        parse_decoder.removeAllItems();
        
        ConfigManager.query_parsers.forEach((cfg) -> {
            parse_decoder.addItem(cfg.name);
        });
        
        parse_table.removeAllItems();
        
        ConfigManager.table_generators.forEach((cfg) -> {
            parse_table.addItem(cfg.name);
        });
    }

    public void setSelectedGroup(int id, boolean selected) {
        for (JCheckBox box : ItemGroups_CheckBoxes) {
            if (box.getToolTipText().equals(Integer.toString(id))) {
                box.setSelected(selected);
                return;
            }
        }
    }

    public void setSelectedItem(int id, boolean selected) {
        for (JCheckBox box : Items_CheckBoxes) {
            if (box.getToolTipText().equals(Integer.toString(id))) {
                box.setSelected(selected);
                return;
            }
        }
    }

    public void initializeCheckBoxes() {

        ConsoleFrame.log("Checkboxes - Initing stuff");

        ItemGroupPanel.setLayout(ItemGroups_Layout);
        ItemPanel.setLayout(Items_Layout);

        CheckBoxListener group = new CheckBoxListener(true);
        CheckBoxListener item = new CheckBoxListener(false);

        ConsoleFrame.log("Checkboxes - Doing Groups");

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
                ConsoleFrame.log("Checkboxes - Group:" + Math.round(i / perc * 100) / 100 * 5);
            }

        }

        ConsoleFrame.log("Created Checkboxes:Groups in " + (System.currentTimeMillis() - pre) + " millis");

        ConsoleFrame.log("Checkboxes - Doing Items");
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
                ConsoleFrame.log("Checkboxes - Item:" + Math.round(i / perc * 100) / 100 * 5);
            }

        }

        ConsoleFrame.log("Created Checkboxes:Items in " + (System.currentTimeMillis() - pre) + " millis");

        refreshConfigVisuals();

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
    private javax.swing.JPanel SettingsPanel;
    private javax.swing.JTabbedPane TabbedPane;
    private javax.swing.JTextField cfg_format;
    private javax.swing.JTextField cfg_id;
    private javax.swing.JTextField cfg_idroot;
    private javax.swing.JTextField cfg_reg;
    private javax.swing.JButton cfg_reload;
    private javax.swing.JButton cfg_reset;
    private javax.swing.JButton cfg_save;
    private javax.swing.JTextField cfg_stat;
    private javax.swing.JTextField cfg_url;
    private javax.swing.JButton cfg_write;
    private javax.swing.JButton deselect;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton load_items;
    private javax.swing.JTextField max_cost_field;
    private javax.swing.JTextField min_margin_field;
    private javax.swing.JTable output_table;
    private javax.swing.JComboBox<String> parse_decoder;
    private javax.swing.JButton parse_reload;
    private javax.swing.JComboBox<String> parse_table;
    private javax.swing.JCheckBox rem_inv;
    private javax.swing.JList selected_items;
    private javax.swing.JButton selected_items_refresh;
    private javax.swing.JTextField system_id;
    private javax.swing.JCheckBox use_filter;
    // End of variables declaration//GEN-END:variables
}
