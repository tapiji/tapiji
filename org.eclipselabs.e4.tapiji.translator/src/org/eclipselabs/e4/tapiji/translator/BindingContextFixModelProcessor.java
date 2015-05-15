package org.eclipselabs.e4.tapiji.translator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MBindingTable;
import org.eclipse.e4.ui.model.application.commands.MKeyBinding;


public class BindingContextFixModelProcessor {

    @Execute
    public void execute(final MApplication app) {
        // Collect key bindings
        Set<MKeyBinding> keys = new HashSet<MKeyBinding>();
        Map<String, MBindingTable> tables = new HashMap<String, MBindingTable>();
        for (MBindingTable table : app.getBindingTables()) {
            tables.put(table.getBindingContext().getElementId(), table);
            keys.addAll(table.getBindings());
        }

        System.out.println("BindingContextFixModelProcessor ... adding tag \"type:user\" to key bindings");
        // Add "type:user" tag to key bindings
        for (MKeyBinding key : keys) {
            if (!key.getTags().contains("type:user")) {
                key.getTags().add("type:user");
                System.out.println("  ... added to key binding " + key.getKeySequence() + " (ID " + key.getElementId() + ")");
            }
        }
    }
}
