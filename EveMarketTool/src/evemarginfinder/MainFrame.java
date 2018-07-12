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

import evemarginfinder.EventQueue.EventType;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public final class MainFrame extends javax.swing.JFrame {

    public InterfaceController controller;
    public EventQueue equeue;
    public CheckBoxHandler cbh;

    public MainFrame(ItemGroup[] item_groups, Entry<Integer, String>[] entries_items,
            HashMap<Integer, ItemGroup> group_lookup, FilterFrame filter) {

        equeue = new EventQueue();

        equeue.run();

        cbh = new CheckBoxHandler(item_groups, entries_items, group_lookup);

        controller = new InterfaceController(this, filter, equeue, cbh);

        controller.pre_component_init();

        ConsoleFrame.log("Initing components");
        initComponents();

        controller.post_component_init(GroupScroll, ItemScroll, ItemGroupPanel, ItemPanel, output_table, NumSelected,
                new JRadioButton[]{station_button, system_button, region_button});

        equeue.queueEvent(EventType.REVALIDATE_COMBOBOXES);
        
        ConsoleFrame.log("Packing");
        pack();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox2 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        LocationButtonGroup = new javax.swing.ButtonGroup();
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
        jLabel4 = new javax.swing.JLabel();
        NumSelected = new javax.swing.JTextField();
        InfoPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        load_items = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        selected_items = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        output_table = new javax.swing.JTable(controller.table_model);
        rem_inv = new javax.swing.JCheckBox();
        show_filters = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        system_id = new javax.swing.JTextField();
        station_button = new javax.swing.JRadioButton();
        system_button = new javax.swing.JRadioButton();
        region_button = new javax.swing.JRadioButton();
        SettingsPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        parse_decoder = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        parse_table = new javax.swing.JComboBox<>();
        parse_reload = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        config_selector = new javax.swing.JComboBox<>();
        cfg_reload = new javax.swing.JButton();
        cfg_write = new javax.swing.JButton();
        open_new = new javax.swing.JButton();
        cfg_remove = new javax.swing.JButton();
        cfg_add = new javax.swing.JButton();

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

        ItemGroupPanel.setLayout(null);
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

        jLabel4.setText("# Selected:");
        SelectionPanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, -1, 30));

        NumSelected.setEditable(false);
        NumSelected.setText("0");
        SelectionPanel.add(NumSelected, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 10, 60, -1));

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
        InfoPanel.add(load_items, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 340, -1, -1));

        selected_items.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        selected_items.setModel(controller.selected_items_model);
        jScrollPane3.setViewportView(selected_items);

        InfoPanel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 120, 360));

        jScrollPane1.setDoubleBuffered(true);

        jScrollPane1.setViewportView(output_table);

        InfoPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, 500, 390));

        rem_inv.setFont(rem_inv.getFont());
        rem_inv.setText("Remove Invalid");
        InfoPanel.add(rem_inv, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 310, -1, -1));

        show_filters.setText("Edit Filters");
        show_filters.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                show_filtersActionPerformed(evt);
            }
        });
        InfoPanel.add(show_filters, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 270, -1, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Location"));

        system_id.setText("The Forge");
        system_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                system_idKeyTyped(evt);
            }
        });

        LocationButtonGroup.add(station_button);
        station_button.setText("Station");
        station_button.setActionCommand("0");
        station_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                area_buttonActionPerformed(evt);
            }
        });

        LocationButtonGroup.add(system_button);
        system_button.setText("System");
        system_button.setActionCommand("1");
        system_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                area_buttonActionPerformed(evt);
            }
        });

        LocationButtonGroup.add(region_button);
        region_button.setSelected(true);
        region_button.setText("Region");
        region_button.setActionCommand("2");
        region_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                area_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(station_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(system_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(system_id, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
            .addComponent(region_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(system_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(station_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(system_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(region_button)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        InfoPanel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 110, 150));

        TabbedPane.addTab("Info", InfoPanel);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Response Parsing"));

        parse_decoder.setModel(controller.parse_decoder_model);
        parse_decoder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parse_decoderActionPerformed(evt);
            }
        });

        jLabel12.setText("API Decoder:");

        jLabel13.setText("Table Generator:");

        parse_table.setModel(controller.parse_table_model);
        parse_table.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parse_tableActionPerformed(evt);
            }
        });

        parse_reload.setText("Reload Parsing Configs");
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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(parse_reload)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(parse_decoder, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(parse_table, 0, 148, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(parse_reload))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Config File"));

        jLabel14.setText("Current Config:");

        config_selector.setModel(controller.config_selector_model);
        config_selector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                config_selectorActionPerformed(evt);
            }
        });

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

        open_new.setText("Open Cfg");
        open_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open_newActionPerformed(evt);
            }
        });

        cfg_remove.setText("Remove");
        cfg_remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cfg_removeActionPerformed(evt);
            }
        });

        cfg_add.setText("Add Cfg");
        cfg_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cfg_addActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cfg_reload, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cfg_write, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(cfg_add)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(open_new)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cfg_remove))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(config_selector, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(config_selector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cfg_add)
                    .addComponent(open_new)
                    .addComponent(cfg_remove))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cfg_write)
                    .addComponent(cfg_reload))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout SettingsPanelLayout = new javax.swing.GroupLayout(SettingsPanel);
        SettingsPanel.setLayout(SettingsPanelLayout);
        SettingsPanelLayout.setHorizontalGroup(
            SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(217, Short.MAX_VALUE))
        );
        SettingsPanelLayout.setVerticalGroup(
            SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(253, Short.MAX_VALUE))
        );

        TabbedPane.addTab("Settings", SettingsPanel);

        getContentPane().add(TabbedPane);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectActionPerformed

        equeue.queueEvent(EventType.DESELECT_ALL);

    }//GEN-LAST:event_deselectActionPerformed

    private void ItemGroupSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ItemGroupSearchKeyTyped

        if (evt.getKeyChar() == '\n') {

            equeue.queueEvent(EventType.GROUP_SEARCH, ItemGroupSearch.getText().toLowerCase());

        } else {

            equeue.queueEvent(EventType.STOP_GROUP_SEARCH);

        }

    }//GEN-LAST:event_ItemGroupSearchKeyTyped

    private void TabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TabbedPaneStateChanged

        equeue.queueEvent(EventType.REVALIDATE_ITEMLIST);
        equeue.queueEvent(EventType.REVALIDATE_TABLE_HEADERS);
        equeue.queueEvent(EventType.REVALIDATE_TABLE_DATA);

    }//GEN-LAST:event_TabbedPaneStateChanged

    @SuppressWarnings("CallToPrintStackTrace")
    private void load_itemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_load_itemsActionPerformed

        equeue.queueEvent(EventType.START_QUERY);

    }//GEN-LAST:event_load_itemsActionPerformed

    private void ItemSearchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ItemSearchKeyTyped

        if (evt.getKeyChar() == '\n') {

            equeue.queueEvent(EventType.ITEM_SEARCH, ItemSearch.getText());

        } else {

            equeue.queueEvent(EventType.STOP_ITEM_SEARCH);

        }

    }//GEN-LAST:event_ItemSearchKeyTyped

    private void system_idKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_system_idKeyTyped

        if (evt.getKeyChar() == '\n') {

            Consumer<String> callback = new Consumer<String>() {
                @Override
                public void accept(String t) {
                    system_id.setToolTipText(t);
                }
            };

            equeue.queueEvent(EventType.SET_SYS_CALLBACK, system_id.getText(), callback);

        }

    }//GEN-LAST:event_system_idKeyTyped

    private void parse_decoderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parse_decoderActionPerformed

        if (parse_decoder.getItemCount() == 0 || parse_decoder.getSelectedItem() == null) {
            return;
        }

        equeue.queueEvent(EventType.SET_PARSER, parse_decoder.getSelectedItem().toString());

    }//GEN-LAST:event_parse_decoderActionPerformed

    private void cfg_reloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cfg_reloadActionPerformed

        equeue.queueEvent(EventType.RELOAD_CURR_CONFIG);

    }//GEN-LAST:event_cfg_reloadActionPerformed

    private void parse_reloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parse_reloadActionPerformed

        equeue.queueEvent(EventType.FORCE_RELOAD_LUA);

    }//GEN-LAST:event_parse_reloadActionPerformed

    private void parse_tableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parse_tableActionPerformed

        if (parse_table.getItemCount() == 0 || parse_table.getSelectedItem() == null) {
            return;
        }

        equeue.queueEvent(EventType.SET_GENERATOR, parse_table.getSelectedItem().toString());

    }//GEN-LAST:event_parse_tableActionPerformed

    private void cfg_writeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cfg_writeActionPerformed

        equeue.queueEvent(EventType.SAVE_CURR_CONFIG);

    }//GEN-LAST:event_cfg_writeActionPerformed

    private void config_selectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_config_selectorActionPerformed

        if (config_selector.getSelectedItem() != null) {
            equeue.queueEvent(EventType.SET_CONFIG, config_selector.getSelectedItem().toString());
        }

    }//GEN-LAST:event_config_selectorActionPerformed

    private void open_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open_newActionPerformed

        JFileChooser chooser = new JFileChooser();

        chooser.setCurrentDirectory(new File("."));

        chooser.setFileFilter(new FileNameExtensionFilter("Config", "emt.cfg"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            equeue.queueEvent(EventType.OPEN_NEW_CONFIG, chooser.getSelectedFile(),
                    (Consumer<FileNotFoundException>) ((ex) -> JOptionPane.showMessageDialog(this,
                            ex.getMessage(), "Could not load file", JOptionPane.ERROR_MESSAGE)));

        }

    }//GEN-LAST:event_open_newActionPerformed

    private void cfg_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cfg_addActionPerformed

        String name = JOptionPane.showInputDialog(this, "Enter name:");

        if (name == null) {
            return;
        }

        equeue.queueEvent(EventType.CREATE_CONFIG, name);

    }//GEN-LAST:event_cfg_addActionPerformed

    private void cfg_removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cfg_removeActionPerformed

        int choice = JOptionPane.showConfirmDialog(this, "Save Config?");

        if (choice == JOptionPane.CANCEL_OPTION) {
            return;
        }

        equeue.queueEvent(EventType.REMOVE_CURR_CONFIG, choice == JOptionPane.YES_OPTION);

    }//GEN-LAST:event_cfg_removeActionPerformed

    private void show_filtersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_show_filtersActionPerformed

        equeue.queueEvent(EventType.EDIT_FILTERS);

    }//GEN-LAST:event_show_filtersActionPerformed

    private void area_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_area_buttonActionPerformed
        String button = LocationButtonGroup.getSelection().getActionCommand();

        try {
            int b = Integer.parseInt(button);

            equeue.queueEvent(EventType.SET_SYS_TYPE, b);
        } catch (NumberFormatException e) {
            ConsoleFrame.log_error("Error parsing area button info: " + e.getMessage());
        }
    }//GEN-LAST:event_area_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane GroupScroll;
    private javax.swing.JPanel InfoPanel;
    private javax.swing.JPanel ItemGroupPanel;
    private javax.swing.JTextField ItemGroupSearch;
    private javax.swing.JPanel ItemPanel;
    private javax.swing.JScrollPane ItemScroll;
    private javax.swing.JTextField ItemSearch;
    private javax.swing.ButtonGroup LocationButtonGroup;
    private javax.swing.JTextField NumSelected;
    private javax.swing.JPanel SelectionPanel;
    private javax.swing.JPanel SettingsPanel;
    private javax.swing.JTabbedPane TabbedPane;
    private javax.swing.JButton cfg_add;
    private javax.swing.JButton cfg_reload;
    private javax.swing.JButton cfg_remove;
    private javax.swing.JButton cfg_write;
    private javax.swing.JComboBox<String> config_selector;
    private javax.swing.JButton deselect;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton load_items;
    private javax.swing.JButton open_new;
    private javax.swing.JTable output_table;
    private javax.swing.JComboBox<String> parse_decoder;
    private javax.swing.JButton parse_reload;
    private javax.swing.JComboBox<String> parse_table;
    private javax.swing.JRadioButton region_button;
    private javax.swing.JCheckBox rem_inv;
    private javax.swing.JList selected_items;
    private javax.swing.JButton show_filters;
    private javax.swing.JRadioButton station_button;
    private javax.swing.JRadioButton system_button;
    private javax.swing.JTextField system_id;
    // End of variables declaration//GEN-END:variables
}
