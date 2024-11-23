package tgpr.bank.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.bank.controller.CreatTransferController;
import tgpr.bank.model.*;
import tgpr.framework.Layouts;
import tgpr.framework.Tools;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;


public class CreatTransferView extends DialogWindow {

    private final  CreatTransferController controller ;
    private final ComboBox<Account> cmbxSourceAccount;
    private final ComboBox<Account> cmbxTargetAccount;
    private final TextBox txtIban;
    private final TextBox txtTitle;
    private final CheckBox chkFavoris;
    private final TextBox txtAmount;
    private final TextBox txtDescription;
    private final TextBox txtDate;
    private final ComboBox<Category> txtCategory;
    private final Label errIban;
    private final Label errTitle;
    private final Label errAmount;
    private final Label errDescription;
    private final Label errDate;
    private final Button btnCreate;
    private final User user;
    private double maxAmount; //le montant maximum d'un compte pour un virement



    public CreatTransferView(CreatTransferController controller, User user) {
        super("Creat Transfer");

        this.controller = controller;
        this.user = user;

        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);

        Panel root = new Panel();
        Panel pop = new Panel().addTo(root);
        pop.setLayoutManager(new GridLayout(5).setTopMarginSize(1));




        //COMBOBOX source accounts
        new Label("Source Account: ").addTo(pop);
        cmbxSourceAccount = new ComboBox<>(controller.sourceAccount()).addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(3))
                .addListener((selectedIndex, previousSelection, changedByUserInteraction) -> {if (changedByUserInteraction)refreshSourcesAccountAndCategory();});
        new EmptySpace().addTo(pop);
        new EmptySpace().addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(5));
        cmbxSourceAccount.setSelectedIndex(controller.getIndexOfCurrentAccount());
        maxAmount = cmbxSourceAccount.getSelectedItem().getSaldo() - cmbxSourceAccount.getSelectedItem().getFloor();



        //COMBOBOX Target Accounts
        new Label("Target Account: ").addTo(pop);
        cmbxTargetAccount = new ComboBox<>("--- Encode IBAN myself ---",controller.getTargetAccountsExceptSourceAccount(cmbxSourceAccount.getSelectedItem()))
                .addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4))
                .addListener((selectedIndex, previousSelection, changedByUserInteraction) -> { if (changedByUserInteraction) setIbanAndTitleOfTargetAccountOnTextboxes();});
        new EmptySpace().addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(5));


        //INPUT de iban
        new EmptySpace().addTo(pop);
        new Label("IBAN :").addTo(pop);
        txtIban= new TextBox(new TerminalSize(20,1)).addTo(pop).setValidationPattern(Pattern.compile("[A-Za-z0-9 ]{0,20}"))
                .setTextChangeListener((txt, byUser) -> {if (byUser) validation();});
        new EmptySpace().addTo(pop);
        new EmptySpace().addTo(pop);
        new EmptySpace().addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));
        errIban = new Label("").addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(3))
                .setForegroundColor(TextColor.ANSI.RED);



        //INPUT du title
        new EmptySpace().addTo(pop);
        new Label("Title :").addTo(pop);
        txtTitle= new TextBox(new TerminalSize(35,1)).addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(3))
                .setValidationPattern(Pattern.compile("[A-Za-z0-9 ]{0,20}")).setTextChangeListener((txt, byUser) -> {if (byUser) validation();});
        new EmptySpace().addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));
        errTitle = new Label("").addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(3)).addTo(pop)
                .setForegroundColor(TextColor.ANSI.RED);


        //CHECKBOX add to favourites
        new EmptySpace().addTo(pop);
        new Label("Favorit :").addTo(pop);
        chkFavoris = new CheckBox().addTo(pop).setEnabled(!controller.isAdmin());// un admin ne decide pas les favoris d'un compte, car un compte peut etre lié à plusieurs utiliasteurs
        new EmptySpace().addTo(pop);
        new EmptySpace().addTo(pop);
        new EmptySpace().addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(5));


        //INPUT amount
        new Label("Amount :").addTo(pop);
        txtAmount= new TextBox(new TerminalSize(12,1)).addTo(pop).setTextChangeListener((txt, byUser) -> validation())
                .setValidationPattern(Pattern.compile("[0-9]{0,8}(.[0-9]{0,2})?"));  // [0-9]{0,8}
        new EmptySpace().addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(3));
        new EmptySpace().addTo(pop);
        errAmount = new Label("").addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4)).addTo(pop)
                .setForegroundColor(TextColor.ANSI.RED);



        //INPUT description
        new Label("Description :").addTo(pop);
        txtDescription= new TextBox(new TerminalSize(30,2)).addTo(pop)
                .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)).setTextChangeListener((txt, byUser) -> validation());
        new EmptySpace().addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));
        new EmptySpace().addTo(pop);
        errDescription = new Label("").addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4))
                .setForegroundColor(TextColor.ANSI.RED);


        //INPUT effect Date
        new Label("Date : ").addTo(pop);
        txtDate= new TextBox(new TerminalSize(11,1)).addTo(pop).setTextChangeListener((txt, byUser) -> validation())
                .setValidationPattern(Pattern.compile("[0-9/]{0,10}"));
        new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(3)).addTo(pop);
        new EmptySpace().addTo(pop);
        errDate = new Label("").addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4))
                .setForegroundColor(TextColor.ANSI.RED);


        //COMBOBOX de category
        new Label("Category : ").addTo(pop);
        txtCategory = new ComboBox<>("NO CATEGORY ",controller.getListCategory(cmbxSourceAccount.getSelectedItem())).addTo(pop);
        txtCategory.setDropDownNumberOfRows(6);
        txtCategory.setSelectedIndex(0);
        new EmptySpace().addTo(pop).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(3));




        //PANEL boutons
        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL)).setLayoutData(Layouts.LINEAR_CENTER).addTo(root);

        //BUTTON create et close
        btnCreate = new Button("create", this::add).addTo(buttons).setEnabled(false);
        new Button("close", this::close).addTo(buttons);





        setComponent(root);

    }





    //raffraichir la liste de targetAccounts et liste de Category lorsqu'on selectionne un sourceAccount
    public void refreshSourcesAccountAndCategory() {
        Account a = cmbxSourceAccount.getSelectedItem();
        maxAmount = a.getSaldo() - a.getFloor();//calcule le nouveau maxAmount
        List<Account> list = controller.getTargetAccountsExceptSourceAccount(a);
        List<Category> list2 = controller.getListCategory(a);

        cmbxTargetAccount.clearItems();
        txtCategory.clearItems();

        int size = Math.max(list.size(), list2.size());

        for (int i = 0;i < size;i++) {
            if (i < list.size())
                cmbxTargetAccount.addItem(list.get(i));
            if (i < list2.size())
                txtCategory.addItem(list2.get(i));
        }

        txtIban.setText("").setReadOnly(false);
        txtTitle.setText("").setReadOnly(false);
        validation();
    }


    //encode le iban et titre d'un targetAccount selectionné dans les textboxes txtIban et txtTitle
    public void setIbanAndTitleOfTargetAccountOnTextboxes() {

        Account a = cmbxTargetAccount.getSelectedItem();
        if (a.getIban() != null) {
            txtTitle.setText(a.getTitle()).setReadOnly(true);
            txtIban.setText(a.getIban()).setReadOnly(true);
        }
        else {
            txtIban.setText("").setReadOnly(false);
            txtTitle.setText("").setReadOnly(false);
        }
        validation();
    }

    //verifie si le iban dans le textbox txtIban exist dans la base de donnees
    //si le iban respect le pattern, il encode le iban dans le txtIban avec le bon format (avec les espaces tous les 4 caractères)
    public void existAccount(String iban) {
        Account a = Account.getByIban(iban);

        if (errIban.getText().equals("")) {
            txtIban.setText(iban).setReadOnly(true);
            if (a != null)
                txtTitle.setText(a.getTitle()).setReadOnly(true);
        }
    }

    //verifie que tous les champs respectent les règles metiers et affiche les erreurs si neccessaire
    //il active ou désactive le bouton CREATE
    public void validation() {
        String goodFormatIban = controller.formatIban(txtIban.getText());

        var errors = controller.validate(
                cmbxSourceAccount.getSelectedItem().getIban(),
                goodFormatIban,
                txtTitle.getText(),
                txtAmount.getText(),
                maxAmount,
                txtDescription.getText(),
                txtDate.getText()
        );


        errIban.setText(errors.getFirstErrorMessage(Account.field.iban));
        errTitle.setText(errors.getFirstErrorMessage(Account.field.title));
        errAmount.setText(errors.getFirstErrorMessage(Transfer.field.amount));
        errDescription.setText(errors.getFirstErrorMessage(Transfer.field.description));
        errDate.setText(errors.getFirstErrorMessage(Transfer.field.created_at));

        btnCreate.setEnabled(errors.isEmpty());

        existAccount(goodFormatIban);
    }



    //vu qu'il n'y a plus d'erreur on peut alors inserer (add();) les donnees dans la base de donnees sans passer par la validation.
    public void add() {

        /*
        Première chose à faire on détermine les valeurs exacte à inserer dans la base de données.
        Ensuit on insère le targetAccount si il n'existe pas dans la base de données et recupère son id qui nous servira plus tard.
        Troisième étape on modifie les soldes des comptes si le status et "executed".
        Si demandé on insère le targetAccount les favoris.
        Comme les soldes des comptes sont modifiés et que j'ai leurs id on peut à présent insérer le transfère dans la base de données.
        Pour terminer on récupère l'id du transfère inséré pour ajouter un nouveau transferCategory dans la base de données si
        une category est sélectionné.
         */



        LocalDateTime creatAt = Security.isRealDateTime ? LocalDateTime.now() : Security.getSystemDate().getSystemDate();
        LocalDate effectiveAt = txtDate.getText().equals("") ? null : Tools.toDate(txtDate.getText());
        double amount = Double.parseDouble(txtAmount.getText());
        Account source = cmbxSourceAccount.getSelectedItem();
        Account target = Account.getByIban(txtIban.getText());//vérifie si le targetAccount existe ou pas.
        String state = txtDate.getText().equals("") ? "executed" : "future";




        if (target == null) {
            controller.saveNewAccount(txtIban.getText(),txtTitle.getText(),"external");//stocke dans la base de donnees
            target = Account.getByIban(txtIban.getText()); // recupere l'id du targetAcount qui est stocké dans la base de donnees.
        }

        if (state.equals("executed")) {
            source.setSaldo(source.getSaldo() - amount);
            source.save();//pour modifier le solde dans la base de donnees
            if (!target.getType().equals("external")) {//si le targetAccount n'est pas un compte externe => on modifie le solde.
                target.setSaldo(target.getSaldo() + amount);
                target.save();//pour modifier le solde dans la base de donnees
            }
        }

        if (chkFavoris.isChecked())
            controller.saveNewFavorite(target); //insère un compte favoris dans la base de donnees.



        int idTransfer = controller.saveNewTransfer( //insère le transfère dans la base de donnees (remarque! idTransfer récupère l'id du transfère dans la base de donnees.
                amount,
                txtDescription.getText(),
                source,
                target,
                source.getSaldo(),
                target.getSaldo(),
                creatAt,
                user.getId(),
                effectiveAt,
                state);


        Transfer transfer = Transfer.getByid(idTransfer);// recupere le transfer qui est stocké dans la base de donnees.


        //le premier item dans la combobox category a des attribut "null" -> à voir dans controller.getListCategory(Account a)
        if (txtCategory.getSelectedIndex() != 0)
            controller.saveTransferCategory(transfer.getId(),source.getId(),txtCategory.getSelectedItem().getId()); //insère un transfereCategory dans la base de donnees.


        controller.affichePopUp();

    }

}
