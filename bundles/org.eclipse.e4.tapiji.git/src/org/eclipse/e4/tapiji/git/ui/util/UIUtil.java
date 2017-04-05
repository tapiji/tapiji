package org.eclipse.e4.tapiji.git.ui.util;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.diff.DiffLine;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.part.middle.ContentView;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class UIUtil {

    private static final String TAG = UIUtil.class.getSimpleName();

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

    @SuppressWarnings("restriction")
    public static void setCurrentBranch(String branchName, IGitService service, EModelService modelService, MApplication app) throws IOException {
        List<Reference> branches = service.localBranches();
        Optional<Reference> foundMaster = branches.stream().filter(branch -> branch.getName().toLowerCase().contains(branchName)).findAny();
        MUIElement dropDownMenu = modelService.find(UIEventConstants.MENU_CHANGE_BRANCH_ID, app);
        if (dropDownMenu instanceof HandledToolItemImpl) {
            if (foundMaster.isPresent()) {
                ((HandledToolItemImpl) dropDownMenu).setLabel(foundMaster.get().getName());
            } else if (branches.size() >= 1) {
                ((HandledToolItemImpl) dropDownMenu).setLabel(branches.get(0).getName());
            } else {
                Log.i(TAG, "NO BRANCH AVAILABLE");
            }
        }
    }
}
