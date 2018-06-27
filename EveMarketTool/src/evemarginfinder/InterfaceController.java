/*
 * The MIT License
 *
 * Copyright 2018 memca.
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
import java.awt.Component;
import java.awt.Font;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import treepanel.HideableTreeNode;
import treepanel.TreePanelLayout;
import static evemarginfinder.EventQueue.NoArgs;
import static evemarginfinder.EventQueue.OneArg;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.JCheckBox;
import org.luaj.vm2.LuaError;

/**
 * This class bridges the gap between EventQueue and MainFrame
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class InterfaceController {

    private final MainFrame frame;

    private final FilterFrame filter;

    private final CheckBoxHandler cbh;

    private final EventQueue equeue;

    public CustomListModel selected_items_model;
    public AbstractTableModel table_model;
    public CustomComboBoxModel<QueryTranslator.XMLLuaConfig, String> parse_decoder_model;
    public CustomComboBoxModel<QueryTranslator.XMLLuaConfig, String> parse_table_model;
    public CustomComboBoxModel<Configuration, String> config_selector_model;

    public final List<List> output_table_data = new ArrayList();
    public TableCellRenderer cell = new TableCellRenderer(output_table_data);

    private String group_search_text = null;
    private Font old_group_font = null;
    private Component current_font_comp = null;
    private Component current_group_comp = null;

    public ItemGroup group_last_ig = null;
    public int item_search_index = 0;
    private JScrollPane ItemScroll;

    private static int sysid = 10000002;
    private static int area_type = QueryTranslator.AREA_REGION;

    private static Thread current_query;

    public InterfaceController(MainFrame frame, FilterFrame filter, EventQueue equeue, CheckBoxHandler cbh) {
        this.frame = frame;
        this.filter = filter;
        this.equeue = equeue;
        this.cbh = cbh;
    }

    /**
     * Initializes data required to initialize the MainFrame's components
     */
    public void pre_component_init() {

        ConsoleFrame.log("Doing interface model stuff");

        selected_items_model = new CustomListModel(cbh);

        table_model = new AbstractTableModel() {

            @Override
            public Class<?> getColumnClass(int column) {
                return QueryTranslator.table_classes[column];
            }

            @Override
            public String getColumnName(int i) {
                return QueryTranslator.table_headers.get(i);
            }

            @Override
            public int getRowCount() {
                return output_table_data.size();
            }

            @Override
            public int getColumnCount() {
                return QueryTranslator.table_headers.size();
            }

            @Override
            public Object getValueAt(int i, int i1) {
                return output_table_data.get(i).get(i1);
            }

        };

        parse_decoder_model = new CustomComboBoxModel<>(QueryTranslator.query_parsers, x -> x.name);

        parse_table_model = new CustomComboBoxModel<>(QueryTranslator.table_generators, x -> x.name);

        config_selector_model = new CustomComboBoxModel<>(Configuration.configs, c -> c.name);

        equeue.registerEventFunction(EventType.GROUP_SEARCH, OneArg(this::ItemGroupSearch));

        equeue.registerEventFunction(EventType.STOP_GROUP_SEARCH, NoArgs(this::StopIGSearch));

        equeue.registerEventFunction(EventType.ITEM_SEARCH, OneArg(this::ItemSearch));

        equeue.registerEventFunction(EventType.STOP_ITEM_SEARCH, NoArgs(this::StopItemSearch));

        equeue.registerEventFunction(EventType.DESELECT_ALL, NoArgs(cbh::deselectAll));

        equeue.registerEventFunction(EventType.SET_SYSTEM, OneArg(this::UpdateSysID));

        equeue.registerEventFunction(EventType.SET_SYS_CALLBACK, this::UpdateSysIDCallback);

        equeue.registerEventFunction(EventType.SET_SYS_TYPE, OneArg(this::UpdateAreaType));

        equeue.registerEventFunction(EventType.START_QUERY, NoArgs(this::OnQuery));

        equeue.registerEventFunction(EventType.SET_PARSER, OneArg(this::UpdateQueryDecoder));

        equeue.registerEventFunction(EventType.RELOAD_CURR_CONFIG, NoArgs(this::ReloadCurrentConfig));

        equeue.registerEventFunction(EventType.REVALIDATE_ITEMLIST, NoArgs(this::RevalidateItemList));

        equeue.registerEventFunction(EventType.REVALIDATE_COMBOBOXES, NoArgs(this::RevalidateComboBoxes));

        equeue.registerEventFunction(EventType.REVALIDATE_TABLE_HEADERS, NoArgs(this::RevalidateTableHeaders));

        equeue.registerEventFunction(EventType.REVALIDATE_TABLE_DATA, NoArgs(this::RevalidateTableData));

        equeue.registerEventFunction(EventType.FORCE_RELOAD_LUA, NoArgs(this::ReloadLua));

        equeue.registerEventFunction(EventType.SET_GENERATOR, OneArg(this::UpdateTableGenerator));

        equeue.registerEventFunction(EventType.SAVE_CURR_CONFIG, NoArgs(this::SaveCurrentConfig));

        equeue.registerEventFunction(EventType.SET_CONFIG, OneArg(this::UpdateCurrentConfig));

        equeue.registerEventFunction(EventType.OPEN_NEW_CONFIG, this::OpenNewConfig);

        equeue.registerEventFunction(EventType.CREATE_CONFIG, OneArg(this::CreateConfig));

        equeue.registerEventFunction(EventType.REMOVE_CURR_CONFIG, OneArg(this::RemoveCurrentConfig));

        equeue.registerEventFunction(EventType.EDIT_FILTERS, NoArgs(this::EditFilter));

    }

    /**
     * Initializes data that requires the MainFrame's components
     *
     * @param GroupScroll
     * @param ItemScroll
     * @param ItemGroupPanel
     * @param ItemPanel
     * @param output_table
     * @param NumSelected
     */
    public void post_component_init(JScrollPane GroupScroll, JScrollPane ItemScroll, JPanel ItemGroupPanel,
            JPanel ItemPanel, JTable output_table, JTextField NumSelected) {

        this.ItemScroll = ItemScroll;

        GroupScroll.getVerticalScrollBar().setUnitIncrement(20);
        ItemScroll.getVerticalScrollBar().setUnitIncrement(20);

        ItemPanel.setLayout(new OptimizedScrollPaneLayout(ItemScroll.getViewport()::getViewRect));
        ItemScroll.getViewport().addChangeListener((ChangeEvent e) -> {
            ItemScroll.getViewport().revalidate();
        });

        TableColumnModel tcm = output_table.getColumnModel();

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            tcm.getColumn(i).setCellRenderer(cell);
        }

        TableRowSorter<TableModel> sorter = new TableRowSorter(output_table.getModel());

        output_table.setRowSorter(sorter);

        ConsoleFrame.log("Initing Check Boxes");
        cbh.initializeCheckBoxes(ItemGroupPanel, ItemPanel);
        cbh.setNumCounter(NumSelected);
        cbh.start();

        LayoutManager layout = ItemGroupPanel.getLayout();

        if (layout instanceof TreePanelLayout) {

            ((TreePanelLayout) layout).addLayoutEventListener(() -> {
                if (current_group_comp != null) {
                    int amount = current_group_comp.getY();

                    GroupScroll.getVerticalScrollBar().setValue(amount);
                    current_group_comp = null;
                }
            });

        } else {
            ConsoleFrame.log_error("Warning: ItemGroup Panel doesn't have a TreePanelLayout");
        }

    }

    public void ItemGroupSearch(String text) {

        // if it's a new query, search from the top
        if (!text.equals(group_search_text)) {
            StopIGSearch();
            group_search_text = text;
        }

        try {
            group_last_ig = cbh.findGroup(text, group_last_ig);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage());
            group_last_ig = null;
            return; // don't search on error
        }

        if (current_font_comp != null) {
            current_font_comp.setFont(old_group_font);
        }

        HideableTreeNode node = cbh.showBoxesTo(group_last_ig);

        current_group_comp = node.GetValue();
        current_font_comp = node.GetComp();
        old_group_font = current_font_comp.getFont();

        Font font = new Font(old_group_font.getName(), Font.BOLD, old_group_font.getSize());

        current_font_comp.setFont(font);

        current_group_comp.revalidate();

    }

    public void StopIGSearch() {

        if (current_font_comp != null) {
            current_font_comp.setFont(old_group_font);
            current_font_comp = null;
        }

        group_last_ig = null;
        group_search_text = null;
    }

    public void ItemSearch(String text) {
        text = text.toLowerCase();
        int found = 0;
        for (JCheckBox box : cbh.Items_CheckBoxes) {
            if (box.getText().toLowerCase().contains(text)) {
                found++;
                if (found > item_search_index) {
                    item_search_index++;
                    ItemScroll.getVerticalScrollBar().setValue(box.getY());
                    return;
                }
            }
        }
    }

    public void StopItemSearch() {
        item_search_index = 0;
    }

    public void UpdateSysID(String text) {

        if (text.matches("\\d+")) {
            sysid = Integer.valueOf(text);
        } else {
            sysid = DatabaseManager.querySystemId(text);
        }

    }

    public void UpdateSysIDCallback(Object[] args) {
        UpdateSysID((String) args[0]);

        if (sysid != -1) {
            ((Consumer<String>) args[1]).accept(DatabaseManager.querySystemName(sysid));
        } else {
            ((Consumer<String>) args[1]).accept("Invalid System");
        }

    }

    public void UpdateAreaType(Integer type) {
        area_type = type;
    }

    public void UpdateQueryDecoder(String item) {

        try {
            Configuration.set("query-parser", item);
            QueryTranslator.setActiveParser(item);
        } catch (NullPointerException e) {
            ConsoleFrame.log_error("Error: that decoder doesn't exist, this should never happen");
        }

    }

    public void ReloadCurrentConfig() {
        Configuration.reloadCurrent();

        equeue.queueEvent(EventType.REVALIDATE_COMBOBOXES);
    }

    public void RevalidateItemList() {
        selected_items_model.fireDataChanged();
    }

    public void RevalidateComboBoxes() {
        config_selector_model.setSelectedItem(Configuration.getCName());
        parse_decoder_model.setSelectedItem2(QueryTranslator.active_parser);
        parse_table_model.setSelectedItem2(QueryTranslator.active_table);

        config_selector_model.fireDataChanged();
        parse_decoder_model.fireDataChanged();
        parse_table_model.fireDataChanged();
    }

    public void RevalidateTableHeaders() {
        QueryTranslator.loadHeaders();
        table_model.fireTableStructureChanged();
    }

    public void RevalidateTableData() {
        table_model.fireTableStructureChanged();
        table_model.fireTableDataChanged();
    }

    public void ReloadLua() {
        QueryTranslator.loadParsers();

        equeue.queueEvent(EventType.REVALIDATE_COMBOBOXES);
    }

    public void UpdateTableGenerator(String item) {

        try {
            Configuration.set("query-table", item);
            QueryTranslator.setActiveTable(item);
        } catch (NullPointerException e) {
            ConsoleFrame.log_error("Error: that table generator doesn't exist, this should never happen");
        }

    }

    public void SaveCurrentConfig() {
        Configuration.saveCurrent();
    }

    public void UpdateCurrentConfig(String item) {
        Configuration.setActive(item);
        QueryTranslator.setActiveParser(Configuration.get("query-parser"));
        QueryTranslator.setActiveTable(Configuration.get("query-table"));

        equeue.queueEvent(EventType.REVALIDATE_COMBOBOXES);
    }

    public void OnQuery() {

        if (current_query != null) {
            current_query.interrupt();
        }

        QueryTranslator.onNewQuery();

        current_query = new Thread(this::QueryRunnable);

        current_query.setName("Current-Query");

        current_query.start();
    }

    private int min(int one, int two) {
        return one > two ? two : one;
    }

    private void QueryRunnable() {

        output_table_data.clear();

        if (sysid == -1) {
            JOptionPane.showMessageDialog(frame, "Invalid System ID/Name", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int[] ids = cbh.getItems();

        if (ids == null) {
            return;
        }

        int numperquery = 20;
        int i1 = 0, i2 = min(ids.length, numperquery);

        while (i1 < ids.length) {

            int[] subids = Arrays.copyOfRange(ids, i1, i2);

            try {
                output_table_data.addAll(DatabaseManager.getMarketInfoBulk(subids, sysid, area_type));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        e.getMessage().length() > 100 ? e.getMessage().substring(0, 100) + "..." : e.getMessage(),
                        "Error in lua script", JOptionPane.ERROR_MESSAGE);
                ConsoleFrame.log_error(e.getMessage());
                break;
            }
            
            i1 = min(i1 + numperquery, ids.length);
            i2 = min(i2 + numperquery, ids.length);
            
        }

        if (filter.asBool("Remove_Invalid")) {
            output_table_data.removeIf(v -> Double.isNaN((double) v.get(1)) || Double.isInfinite((double) v.get(1)));
        }

        System.out.println(output_table_data);

        table_model.fireTableDataChanged();

        current_query.interrupt();
    }

    /**
     *
     * TODO: re-work this <br>
     * Called by {@link OnQuery}
     */
    @Deprecated
    private void _QueryRunnable() {

        output_table_data.clear();

        if (sysid == -1) {
            JOptionPane.showMessageDialog(frame, "Invalid System ID/Name", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int[] ids = cbh.getItems();

        if (ids == null) {
            return;
        }

        int numperquery = 20;

        if (ids.length > numperquery) {

            int i = ids.length;

            while (i > 0) {

                int[] subids = Arrays.copyOfRange(ids, Math.max(0, i - numperquery), i);

                try {
                    output_table_data.addAll(DatabaseManager.getMarketInfoBulk(subids, sysid, area_type));
                } catch (LuaError e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            e.getMessage().length() > 100 ? e.getMessage().substring(0, 100) + "..." : e.getMessage(),
                            "Error in lua script", JOptionPane.ERROR_MESSAGE);
                    ConsoleFrame.log_error(e.getMessage());
                    break;

                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            e.getMessage().length() > 100 ? e.getMessage().substring(0, 100) + "..." : e.getMessage(),
                            "Error Connecting to API", JOptionPane.ERROR_MESSAGE);
                    ConsoleFrame.log_error(e.getMessage());
                    break;
                }

                i -= numperquery;

            }

        } else {

            try {
                output_table_data.addAll(DatabaseManager.getMarketInfoBulk(ids, sysid, area_type));
            } catch (LuaError e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        e.getMessage().length() > 100 ? e.getMessage().substring(0, 100) + "..." : e.getMessage(),
                        "Error in lua script", JOptionPane.ERROR_MESSAGE);
                ConsoleFrame.log_error(e.getMessage());
                return;

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        e.getMessage().length() > 100 ? e.getMessage().substring(0, 100) + "..." : e.getMessage(),
                        "Error Connecting to API", JOptionPane.ERROR_MESSAGE);
                ConsoleFrame.log_error(e.getMessage());
                return;
            }

        }

        if (filter.asBool("Remove_Invalid")) {
            output_table_data.removeIf(v -> Double.isNaN((double) v.get(1)) || Double.isInfinite((double) v.get(1)));
        }

        System.out.println(output_table_data);

        table_model.fireTableDataChanged();

        current_query.interrupt();
    }

    public void OpenNewConfig(Object[] args) {
        File file = (File) args[0];
        Consumer<FileNotFoundException> _catch = (Consumer<FileNotFoundException>) args[1];

        try {
            Configuration.loadFromFile(file);
            equeue.queueEvent(EventType.REVALIDATE_COMBOBOXES);
        } catch (FileNotFoundException ex) {
            _catch.accept(ex);
        }

    }

    public void CreateConfig(String name) {

        Configuration.addNew(name);
        Configuration.setActive(name);

        equeue.queueEvent(EventType.REVALIDATE_COMBOBOXES);
    }

    public void RemoveCurrentConfig(Boolean save) {

        try {
            Configuration.removeCurrent(save);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, ex, "Error removing configuration", JOptionPane.ERROR_MESSAGE);
        }

        equeue.queueEvent(EventType.REVALIDATE_COMBOBOXES);
    }

    public void EditFilter() {
        filter.setVisible(true);
    }

    private class CustomListModel extends AbstractListModel {

        private CheckBoxHandler cbh;

        public CustomListModel(CheckBoxHandler cbh) {
            this.cbh = cbh;
        }

        public void fireDataChanged() {
            this.fireContentsChanged(this, 0, getSize() - 1);
        }

        @Override
        public int getSize() {
            if (cbh.getItemsConst() != null) {
                return cbh.getItemsConst().length;
            } else {
                return 0;
            }
        }

        @Override
        public Object getElementAt(int index) {
            return DatabaseManager.queryItemName(cbh.getItemsConst()[index]);
        }
    }

}
