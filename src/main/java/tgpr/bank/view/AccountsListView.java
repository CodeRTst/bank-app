package tgpr.bank.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import tgpr.bank.controller.LoginController;
import tgpr.framework.ColumnSpec;
import tgpr.framework.ObjectTable;
import tgpr.framework.Tools;
import tgpr.framework.ViewManager;
import tgpr.bank.controller.AccountsListController;
import tgpr.bank.model.Account;
import tgpr.bank.model.Security;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;



import java.util.List;

public class AccountsListView extends BasicWindow {
    private final AccountsListController controller;

    private final ObjectTable<Account> table;

    private  Account account;
    private final Button btnCreate;



    public AccountsListView(AccountsListController controller) {
        this.controller = controller;

        setTitle(getTitleWithUser());
        setHints(List.of(Hint.EXPANDED));

        // Le panel 'root' est le composant racine de la fenêtre (il contiendra tous les autres composants)
        Panel root = new Panel();
        setComponent(root);

        MenuBar menuBar = new MenuBar().addTo(root);
        Menu menuFile = new Menu("File");
        menuBar.add(menuFile);
        MenuItem menuLogout = new MenuItem("Logout", LoginController::logout);
        menuFile.add(menuLogout);
        MenuItem menuExit = new MenuItem("Exit", LoginController::exit);
        menuFile.add(menuExit);



        new EmptySpace().addTo(root);

        new Label(">> YOUR ACCOUNTS << ").addTo(root);

        // ajoute une ligne vide
        new EmptySpace().addTo(root);

        // crée un tableau de données pour l'affichage des membres
        table = new ObjectTable<>(
                new ColumnSpec<>("Iban", Account::getIban),
                new ColumnSpec<>("Title", m -> Tools.ifNull(m.getTitle(), "")),
                new ColumnSpec<>("    "+"Type", m -> Tools.ifNull(m.getType(), "")),
                new ColumnSpec<>("     "+"Floor", m -> Tools.ifNull("   "+m.toStringFloor(), "")),
                new ColumnSpec<>("     "+"Saldo", m -> Tools.ifNull("   "+m.toStringSaldo(), ""))
        );

        // ajoute le tableau au root panel
        root.addComponent(table);
        // spécifie que le tableau doit avoir la même largeur quee le terminal et une hauteur de 15 lignes
        table.setPreferredSize(new TerminalSize(ViewManager.getTerminalColumns(), 15));
        // charge les données dans la table
        new EmptySpace().addTo(root);


        // spécifie l'action a exécuter quand on presse Enter ou la barre d'espace
        table.setSelectAction(() -> {
            var account = table.getSelected();
            Account.setCurrentAccount(account);
            if(account!=null) {
                controller.showAccountDetail(account);
                table.setSelected(account);
            }


        });


        // crée un bouton pour l'ajout d'un new transfert et lui associe une fonction lambda qui sera appelée
        // quand on clique sur le bouton
        btnCreate = new Button("New Transfer", () -> {
            controller.addNewTransfer();
                reloadData();
        }).addTo(root);

        reloadData();



    }

    private String getTitleWithUser() {
        return "Welcome to BankApp (" + Security.getLoggedUser().getEmail() + " - " + (Security.isAdmin() ? "Admin"  :Security.isManager()?"Manager":"Client") +" "+ (Security.isRealDateTime ? "- use system date/time" : Security.getSystemDate()) +")";
    }
    public void reloadData() {
        // vide le tableau
        table.clear();
        // demande au contrôleur la liste des membres
        var accounts = controller.getAccounts();
        // ajoute l'ensemble des membres au tableau
        table.add(accounts);
        btnCreate.setEnabled(accounts.size() != 0 || controller.isAdmin());
    }
}
