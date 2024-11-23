package tgpr.bank.view;

import com.googlecode.lanterna.gui2.*;
import tgpr.bank.controller.AccountCategoryListController;
import tgpr.bank.model.Category;
import tgpr.framework.ColumnSpec;
import tgpr.framework.Layouts;
import tgpr.framework.ObjectTable;
import java.util.List;


public class AccountCategoryListView extends BasicWindow {
    private final AccountCategoryListController controller;
    private final ObjectTable<Category> table;

    public AccountCategoryListView(AccountCategoryListController controller) {
        this.controller = controller;
        setTitle("Categories");
        setHints(List.of(Hint.EXPANDED));

        Panel root = new Panel();
        setComponent(root);

        Panel cate = new Panel().setLayoutManager(new GridLayout(4).setLeftMarginSize(1).setTopMarginSize(1))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);

        table = new ObjectTable<>(
                       new ColumnSpec<>("Name", Category::getName),
                       new ColumnSpec<>("Type", Category::type),
                        new ColumnSpec<>("User",Category::countUse)
                        );

        table.setSelectAction( ()->{
            var categorie = table.getSelected();
            controller.editCategory(categorie);
            reloadData();
            table.setSelected(categorie);
        });
        cate.addComponent(table);


        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        //new Button("Add", this::add).addTo(buttons);
        new Button("Reset", this::reset).addTo(buttons);
        root.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));




        reloadData();

    }
    //private void add(){
      // controller.addCategory(6);

    //}
    private void reset(){}
    public void reloadData() {
        table.clear();
      //  var categories = controller.getCategorys(2);
       // table.add(categories);
    }


    }
