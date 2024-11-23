package tgpr.bank.view;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.bank.controller.AccountCategoryListController;
import tgpr.bank.controller.DisplayAccountController;
import tgpr.bank.model.*;
import tgpr.framework.*;

import java.util.List;

public class DisplayAccountView extends DialogWindow {
    private final DisplayAccountController controller;
    private final AccountCategoryListController controllerCATE;
    private Account account;

    private final Label lblIban;
    private final Label lblTitle;
    private final Label lblType;
    private final Label lblSaldo;
    private final User user;

    private  Button btnCreate;

    private   ComboBox<Account> txtFavouritAccounts;
    private   ObjectTable<Account> tableAccount;
    private   ObjectTable<Transfer> tableTransfer;

    private  TextBox txtFilter;
    public DisplayAccountView(DisplayAccountController controller,AccountCategoryListController controllerCATE, Account account, User user) {

        super("Account Details");

        this.controller = controller;
        this.controllerCATE = controllerCATE;
        this.account = account;
        this.user = Security.getLoggedUser();


        setHints(List.of(Hint.CENTERED));
        // permet de fermer la fenêtre en pressant la touche Esc
        setCloseWindowWithEscape(true);
        // définit une taille fixe pour la fenêtre de 25 lignes et 160 colonnes
        setFixedSize(new TerminalSize(113, 28));


        Panel main = new Panel();
        Panel fieldsAccountDetails = new Panel().setLayoutManager(new GridLayout(4).setTopMarginSize(1)).addTo(main);
        Panel root = new Panel().setLayoutManager(new GridLayout(2)).addTo(main);


        fieldsAccountDetails.addComponent(new Label("IBAN:"));
        lblIban = new Label("").addTo(fieldsAccountDetails).addStyle(SGR.BOLD);

        fieldsAccountDetails.addComponent(new Label("Type:"));
        lblType = new Label("").addTo(fieldsAccountDetails).addStyle(SGR.BOLD);

        fieldsAccountDetails.addComponent(new Label("Title:"));
        lblTitle = new Label("").addTo(fieldsAccountDetails).addStyle(SGR.BOLD);


        fieldsAccountDetails.addComponent(new Label("Saldo:"));
        lblSaldo = new Label("").addTo(fieldsAccountDetails).addStyle(SGR.BOLD);



        // ajouter la panel (table) de account history


        createPanelHistory().addTo(root).setPreferredSize(new TerminalSize(ViewManager.getTerminalColumns(),16));
        Panel panelBottom = new Panel();
        new EmptySpace().addTo(main);
        createPanelCategory().addTo(panelBottom).setPreferredSize(new TerminalSize(52,16));
        createPanelFavourit().addTo(panelBottom).setPreferredSize(new TerminalSize(60,16));
        panelBottom.setLayoutManager(new GridLayout(2).setBottomMarginSize(0)).addTo(main);
        new EmptySpace().addTo(main);





        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL)).setLayoutData(Layouts.LINEAR_CENTER).addTo(main);


        new Button("New Transfer", controller::addNewTransfer).addTo(buttons);


        new Button("Close", this::close).addTo(buttons);

        setComponent(main);

        refresh();
    }

    public void refresh() {
        if (account != null) {
            account = Account.getById(account.getId());
            lblIban.setText(account.getIban());
            lblType.setText(account.getType());
            lblTitle.setText(account.getTitle());
            lblSaldo.setText(account.toStringSaldo());
        }
        // lblSaldo.setText(Tools.ifNull(account.getSaldo(), " ");
    }


    //-------------------------------------------------Transfer history table
    private Panel createPanelHistory(){

        Panel root = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        Panel panelFilter = new Panel().setLayoutManager(new GridLayout(2));
        Panel panelTable = new Panel().setLayoutManager(new GridLayout(1));

        txtFilter = new TextBox();
        tableTransfer=new ObjectTable<>(
                new ColumnSpec<>("Effect Date", m -> Tools.ifNull(m.getEffectiveAt(),"")),
                new ColumnSpec<>("Description", m -> Tools.ifNull(Tools.abbreviate(m.getDescription(),7), "")),
                new ColumnSpec<>("From/To", m -> Tools.abbreviate(Tools.ifNull(m.toStrigAnotherAccount(account.getId()),"").toString(),29)),
                new ColumnSpec<>("Category", m -> Tools.abbreviate(Tools.ifNull(m.getCategory(account.getId()),"").toString(),8)),//
                new ColumnSpec<>("    "+"Amount", m -> m.toStringAmountTable(account.getId())),
                new ColumnSpec<>("       "+"Soldo", m -> m.toStringSoldo(account.getId())),
                new ColumnSpec<>("State", m -> Tools.ifNull(m.getState(), "")));

        tableTransfer.setPreferredSize(new TerminalSize(ViewManager.getTerminalColumns(), 6));

        reloadTableData();


        new Label("Filter:").addTo(panelFilter);
        txtFilter.addTo(panelFilter).takeFocus().setTextChangeListener((txt, byAll) -> reloadTableData()).setPreferredSize(new TerminalSize(30,1));
        new EmptySpace().addTo(root);

        //affichage les details de virement , par la selection de transfer de la table
        tableTransfer.setSelectAction(() -> {
            var transfer = tableTransfer.getSelected();
            controller.transferDetail(transfer);
            reloadTableData();
            reloadData();
            reloadTableData();
            tableTransfer.setSelected(transfer);

        });
        Panel panelBorder = new Panel();
        panelFilter.addTo(panelBorder);
        new EmptySpace().addTo(panelBorder);
        tableTransfer.addTo(panelTable);
        panelTable.addTo(panelBorder);
        panelFilter.addTo(panelBorder);
        panelBorder.addTo(root);
        panelBorder.withBorder(Borders.singleLine(" History ")).addTo(root);

        reloadTableData();


        return root;
    }
    public void reloadTableData() {
        // supprimer les elements de table
        tableTransfer.clear();
        //var transfers=controller.getTransfers();
        var transfers = controller.getTransfers(txtFilter.getText());
        // remplir la table de Transfer
        tableTransfer.add(transfers);
    }


    //-------------------------------------------------Category

    private TextBox addCate;
    private  ObjectTable<Category> table;
    private Button btnadd;
    private Panel createPanelCategory(){
        Panel root = new Panel();
        setComponent(root);

        Panel cate = new Panel().setLayoutManager(new GridLayout(8).setLeftMarginSize(1).setBottomMarginSize(1))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);

        table = new ObjectTable<>(
                new ColumnSpec<>("Name", Category::getName).setMinWidth(20),
                new ColumnSpec<>("Type", Category::type).setMinWidth(10),
                new ColumnSpec<>("Use",Category::countUse).setMinWidth(10)
        );
        reloadData();
        table.setSelectAction( ()->{
            var categorie = table.getSelected();
            if (categorie.getAccount() == 0)
                controllerCATE.editSystemFavorite();
            else
                controllerCATE.editCategory(categorie);
            reloadData();
            reloadTableData();
            table.setSelected(categorie);
        });
        reloadData();
        table.setPreferredSize(new TerminalSize(55,25));

        cate.addComponent(table);


        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        addCate = new TextBox("").setPreferredSize(new TerminalSize(15,1)).addTo(buttons);
        btnadd = new Button("Add", this::add).addTo(buttons);
        new Button("Reset", this::reset).addTo(buttons);
        root.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        reloadData();


        Panel panelBorder = new Panel();
        cate.addTo(panelBorder);
        buttons.addTo(panelBorder);
        new EmptySpace().addTo(panelBorder);
        panelBorder.addTo(root);
        panelBorder.withBorder(Borders.singleLine(" Category ")).addTo(root);

        return root;

    }

    private void add(){
        controllerCATE.addCategory(addCate.getText(),account);
        reloadData();
        reloadTableData();
    }
    private void reset(){
        addCate.setText("");

    }
    public void reloadData() {
        table.clear();
        var categories = controllerCATE.getCategorys(account.getId());
        table.add(categories);
    }


//-----------------------------------------------END CATEGORY

//START Favourite--------------------------------------------------
    private Panel createPanelFavourit(){

        Panel root = new Panel();
        Panel panelBorder = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL)).addTo(root);
        panelBorder.withBorder(Borders.singleLine(" Favourite Accounts ")).addTo(root);
        //Panel panelTable = new Panel().addTo(panelBorder);


           tableAccount = new ObjectTable<>(
                new ColumnSpec<>("IBAN", Account::getIbanTitle).setMinWidth(30),
                new ColumnSpec<>("Type", Account::getType).setMinWidth(10)).addTo(panelBorder);
        tableAccount.setPreferredSize(new TerminalSize(ViewManager.getTerminalColumns(), 6));

        new EmptySpace().addTo(panelBorder);


        // spécifie l'action a exécuter quand on presse Enter ou la barre d'espace
        tableAccount.setSelectAction(() -> {
            var account = tableAccount.getSelected();
            if(account !=null)
                controller.removeFavourite(account, user);
            reloadDataAccount();
            tableAccount.setSelected(account);
        });

        var buttons = new Panel().addTo(panelBorder).setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        txtFavouritAccounts = new ComboBox<>("", Account.getAddFavoritAccountsFromHistory(user.getId(),account.getId())).setPreferredSize(new TerminalSize(30, 2)).addTo(buttons);
        btnCreate= new Button("Add", this::addFavourite).addTo(buttons);
        new Button("Reset", this::resetFavourite).addTo(buttons);
        reloadDataAccount();
        return root;
    }



    public void reloadDataAccount() {
        // vide le tableau
        tableAccount.clear();
        // demande au contrôleur la liste des favoris
        var accounts = Account.getAllFavouritsAccounts(user.getId());
        // ajoute l'ensemble des favoris au tableau
        tableAccount.add(accounts);

        txtFavouritAccounts.clearItems();
        List<Account> list = Account.getAddFavoritAccountsFromHistory(user.getId(),account.getId());

        for (Account a : list) {
            txtFavouritAccounts.addItem(a);
        }
        btnCreate.setEnabled(list.size() != 0);
        txtFavouritAccounts.setEnabled(list.size() != 0);
    }
    

    private void resetFavourite() {
        txtFavouritAccounts.setSelectedIndex(-1);    }

    private void addFavourite() {
        if(txtFavouritAccounts.getSelectedItem() == null)
            resetFavourite();
        else {
            controller.save(
                    txtFavouritAccounts.getSelectedItem()
            );
            reloadDataAccount();

        }
    }
}