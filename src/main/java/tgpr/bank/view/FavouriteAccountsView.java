package tgpr.bank.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.bank.controller.FavouriteAccountsController;
import tgpr.bank.model.*;
import tgpr.framework.*;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.*;
import java.util.List;

public class FavouriteAccountsView extends DialogWindow {
    private final FavouriteAccountsController controller;
    private final ComboBox<Account> txtFavouritAccounts;
    private final ObjectTable<Account> table;
    private final Button btnCreate;


    private final User user;

    public FavouriteAccountsView(FavouriteAccountsController controller, User user) {
        super("Favourite Accounts");
        this.controller = controller;
        this.user = user;//Security.getLoggedUser();;

        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);


        Panel root = new Panel();
        // crée un tableau de données pour l'affichage des membres
        table = new ObjectTable<>(
                new ColumnSpec<>("IBAN",Account::getIbanTitle),
                new ColumnSpec<>("Type",Account::getType));
        //reloadData();

        // ajoute le tableau au root panel
        root.addComponent(table);

        new EmptySpace().addTo(root);
        new EmptySpace().addTo(root);
        new EmptySpace().addTo(root);

        // spécifie l'action a exécuter quand on presse Enter ou la barre d'espace
        table.setSelectAction(() -> {
            var account = table.getSelected();
            if(account !=null)
                controller.removeFavourite(account);
            reloadData();
            table.setSelected(account);
        });

        var buttons = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        txtFavouritAccounts = new ComboBox<>("", Account.getAddFavoritAccountsFromHistory(user.getId(),2)).setPreferredSize(new TerminalSize(30, 2)).addTo(buttons);
        btnCreate = new Button("add", this::add).addTo(buttons);
        reloadData();
        new Button("Reset", this::reset).addTo(buttons);
        setComponent(root);
    }


    private void add() {
        if(txtFavouritAccounts.getSelectedItem() == null)
            reset();
        else {
            controller.save(
                    txtFavouritAccounts.getSelectedItem()
            );
            reloadData();

        }
    }

    private void reset() {
        txtFavouritAccounts.setSelectedIndex(-1);
    }

    public void reloadData() {
        // vide le tableau
        table.clear();
        // demande au contrôleur la liste des favoris
        var accounts = Account.getAllFavouritsAccounts(user.getId());
        // ajoute l'ensemble des favoris au tableau
        table.add(accounts);

        txtFavouritAccounts.clearItems();
        List<Account> list = Account.getAddFavoritAccountsFromHistory(user.getId(),2);

        for (Account a : list) {
            txtFavouritAccounts.addItem(a);
        }
        btnCreate.setEnabled(list.size() != 0);
        txtFavouritAccounts.setEnabled(list.size() != 0);
    }

}
