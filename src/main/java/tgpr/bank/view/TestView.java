package tgpr.bank.view;

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.bank.controller.TestController;
import tgpr.bank.model.Test;
import tgpr.framework.ColumnSpec;
import tgpr.framework.Layouts;
import tgpr.framework.ObjectTable;

import java.util.List;

public class TestView extends DialogWindow {
    private final TestController controller;
    private final ObjectTable<Test> tbl;

    public TestView(TestController controller) {
        super("Test");
        setHints(List.of(Hint.EXPANDED));

        this.controller = controller;

        var root = new Panel()
                .setLayoutManager(new GridLayout(1).setTopMarginSize(1).setLeftMarginSize(1));
        setComponent(root);

        tbl = new ObjectTable<>(
                new ColumnSpec<>("Tests", Test::getName).setWidth(15)
        ).addTo(root);

        ((Component) tbl).setLayoutData(Layouts.LINEAR_BEGIN);

        tbl.setSelectAction(() -> {
            controller.displayTest(tbl.getSelected());
        });

        refresh();
    }

    private void refresh() {
        tbl.add(controller.getData());
    }
}
