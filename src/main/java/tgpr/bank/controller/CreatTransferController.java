package tgpr.bank.controller;


import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import tgpr.bank.model.Account;
import tgpr.bank.model.Security;
import tgpr.bank.model.Transfer;
import tgpr.bank.model.User;
import tgpr.bank.model.*;
import tgpr.bank.view.CreatTransferView;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CreatTransferController extends Controller{

    private final User user;
    private final boolean isAdmin;
    private CreatTransferView view;

    private int indexOfCurrentAccount = 0;
    private final Account currentAcc;




    public CreatTransferController() {

        this.user = Security.getLoggedUser();
        this.isAdmin = user.isAdmin();
        this.currentAcc = Account.getCurrentAccount();
    }

    public Window getView(){
        view = new CreatTransferView(this, user);
        return view;
    }

    public int getIndexOfCurrentAccount() {
        return indexOfCurrentAccount;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public ErrorList validate(String ibanSourceAccount, String ibanTxtIban, String title, String amount, double maxAmount, String description, String date) {
        return CreatTransferValidator.validate(ibanTxtIban,ibanSourceAccount,title,amount,maxAmount,description,date);
    }

    public void affichePopUp() {
        MessageDialogButton b = MessageDialogButton.OK;
        showMessage("your transfer has been succesfully created!","information", b);
        //
        view.close();
    }

    //transforme le iban dans le bon format (des espaces tous les 4 caractères)
    public String formatIban(String iban) {
        StringBuilder mot = new StringBuilder();
        int k = 0;
        for (int i = 0;i < iban.length();i++) {
            char a = iban.charAt(i);
            if (a != ' ') {
                if (k % 4 == 0)
                    mot.append(' ');
                mot.append(a);
                k++;
            }
        }
        return mot.toString().trim();
    }










    //les listes
    public List<Account> sourceAccount() {
        List<Account> list = isAdmin ? Account.getSourceAccountForAdmin() : Account.getListSourceAccountByIdUser(user.getId());
        if (currentAcc != null) {
            for (int i = 0; i < list.size(); i++) {
                if (currentAcc.getId() == list.get(i).getId()) {
                    indexOfCurrentAccount = i;
                    break;
                }
            }
        }
        return list;
    }


    public List<Account> getTargetAccountsExceptSourceAccount(Account a) {
        List<Account> list = isAdmin ? Account.getTargetAccountsForAdmin(a) : Account.getListTargetAccountsExceptSourceAccount(a,user.getId());

        list.add(0,new Account());
        return list;
    }

    public List<Category> getListCategory(Account a) {

        List<Category> list = Category.getAllCategoryByAccount(a.getId());
        list.add(0,new Category());

        return list;
    }








    //methodes pour sauvegarder dans la base de donnees
    public void saveNewFavorite(Account target) {
        if (target.isNotAccountOfLoggedUser(user.getId()))
            new Favourite(user.getId(),target.getId()).save();
    }

    public void saveNewAccount(String iban, String title, String type) {new Account(iban,title,type).save();}

    public void saveTransferCategory(int idTransfer, int idTarget, int idCategory) {new TransferCategory(idTransfer,idTarget,idCategory).save();}

    public int saveNewTransfer(double amount, String description, Account source_account, Account target_account, double sourceSolde, double targetSolde,
                               LocalDateTime created_at, int created_by, LocalDate effective_at, String state) {


        return new Transfer(amount,description,source_account.getId(),target_account.getId(),sourceSolde,
                targetSolde,created_at,created_by,effective_at,state).save();
    }



}

