package tgpr.bank.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.bank.controller.EditCategoryController;
import tgpr.bank.model.Category;
import tgpr.framework.Layouts;

import java.util.List;
import java.util.regex.Pattern;

public class EditCategoryView extends DialogWindow {

    private final EditCategoryController controller;
    private final Category categories;

    private final Label errCategory;
    private final TextBox txtCategorie;
    private Label txtType;
    private final Button btnAddUpdate;

    private final  Button btndel;

    public EditCategoryView( EditCategoryController controller, Category categories) {
        super((categories == null ? "Add " : "Update ") + categories);
        this.controller = controller;
        this.categories = categories;


        setHints(List.of(Hint.CENTERED));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);
        // définit une taille fixe pour la fenêtre de 15 lignes et 70 colonnes
       //setFixedSize(new TerminalSize(40, 8));


        Panel main =new Panel();

        Panel root = new Panel().addTo(main);
        root.setLayoutManager(new GridLayout(2));
        new EmptySpace().addTo(root).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));

        new Label("Name :").addTo(root);
        txtCategorie = new TextBox(new TerminalSize(25, 1)).addTo(root).setText(categories.getName())
                .setValidationPattern(Pattern.compile("[a-z][a-z\\d]{0,18}"));
        new EmptySpace().addTo(root).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));

        new Label("Type :").addTo(root);
        txtType= new Label(categories == null ? "" : categories.type()).addTo(root);

        new EmptySpace().addTo(root).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));

        var buttons = new Panel().addTo(main).setLayoutManager(new LinearLayout(Direction.HORIZONTAL)).setLayoutData(Layouts.LINEAR_CENTER);
        btnAddUpdate = new Button(categories == null ? "Add" : "Save", this::add).addTo(buttons);
        new Button("Delete", this::delete).addTo(buttons);
        btndel = new Button("Close", this::close).addTo(buttons);


        errCategory = new Label("").addTo(main).setForegroundColor(TextColor.ANSI.RED);


        setComponent(main);

    }
    private void delete() {
        if(categories == null){
            btndel.setLabel("");
        }
        if(categories.type() == "system"){
            errCategory.setText( "Error Type System");
        }else{
            controller.delete(txtCategorie.getText());
        }
    }

    private void add() {
        if(categories == null){
            controller.save();
        }
        if(categories.type() == "system"){
            errCategory.setText( "Error Type System");
        }else {
            controller.save(txtCategorie.getText()
            );
        }

    }

}
