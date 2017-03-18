package org.eclipse.e4.tapiji.git.ui.util;


import java.util.stream.IntStream;
import org.eclipse.e4.tapiji.git.model.diff.DiffLine;
import org.eclipse.e4.tapiji.git.ui.part.middle.ContentView;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class UIUtil {

    public static void colorizeLineBackground(DiffLine line, TableItem item) {
        if (line.getNumberRight().contains("+") || line.getNumberLeft().contains("+")) {
            item.setBackground(ContentView.GREEN);
        } else if (line.getNumberRight().contains("-") || line.getNumberLeft().contains("-")) {
            item.setBackground(ContentView.RED);
        }
    }

    public static void createColumns(int columns, Table table, int[] alignment) {
        IntStream.rangeClosed(0, columns - 1).forEach(i -> {
            TableColumn column = new TableColumn(table, alignment[i]);
            column.pack();
        });
    }

    public static TableColumnLayout setColumnWeights(Table table, int[] weights) {
        TableColumnLayout layout = new TableColumnLayout();
        IntStream.rangeClosed(0, table.getColumnCount() - 1).forEach(i -> layout.setColumnData(table.getColumns()[i], new ColumnWeightData(weights[i], weights[i])));
        return layout;
    }
}
