/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evemarginfinder;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class TableCellRenderer extends DefaultTableCellRenderer {

    private final List<Vector> tdata;

    public TableCellRenderer(List<Vector> tdata) {
        this.tdata = tdata;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Color color = QueryTranslator.getCellColors(table.convertRowIndexToModel(row), column, tdata);

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        } else {
            c.setForeground(color);
            c.setBackground(table.getBackground());
        }
        return c;

    }

}
