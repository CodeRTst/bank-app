package tgpr.bank.view;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.bank.controller.EditFavouriteController;
import tgpr.bank.model.Account;
import java.util.List;




public class EditFavouriteView extends DialogWindow {

    private final EditFavouriteController controller;

    private final Account account;


    private final Button btnYes ;
    private final Button btnNo ;





    public EditFavouriteView(EditFavouriteController controller, Account account) {
        super("Remove Favourite");


        this.controller = controller;
        this.account = account;

        setHints(List.of(Hint.CENTERED));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);
        Panel root = new Panel();

        new Label("Do you want to remove this account from your favourites ? ").addTo(root);

        new EmptySpace().addTo(root);


        new Label(this.account.getIbanTitle()).addTo(root);
        new EmptySpace().addTo(root);


        var buttons = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        new Label("                                 ").setLayoutData(GridLayout.createHorizontallyFilledLayoutData(3)).addTo(buttons);
        new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(8)).addTo(buttons);
        btnYes = new Button( "Yes", this::delete).addTo(buttons);
        btnNo= new Button("No", this::close).addTo(buttons);

        new EmptySpace().addTo(root);


        setComponent(root);

    }

    private void delete() {
        controller.delete();
    }



}
