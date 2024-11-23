package tgpr.bank.controller;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import tgpr.bank.model.*;
import tgpr.bank.view.TransferDetailView;
import tgpr.framework.Controller;


public class TransferDetailController extends Controller {

    private final TransferDetailView view;
    private final Transfer transfer;
    private final Account account;



    public TransferDetailController(Transfer transfer,Account account) {
        this.transfer = transfer;
        this.account = account;
        view = new TransferDetailView(this, transfer,account);
    }



    public void delete() {
        if (askConfirmation("You are about to delete this transfer. Please confirm.", "Delete transfer")) {
            TransferCategory.delete(transfer.getId(),account.getId());
            transfer.delete(account.getId());
            view.close();
        }
    }
    //-------------------------------
    public void save(int idAcount,int idCategory) {
        if(askConfirmation("You are about to modify this transfer.", "Save changings")){
            transfer.saveCategoty(idAcount,idCategory);
            showMessage("Transfer modified successfully!", "INFO", MessageDialogButton.OK);
            view.close();
        }
    }
    //------------------------------

    @Override
    public Window getView() {
        return view;
    }




    //--------------méthodes ajouté par Hassan------------------------------------------------------------------------------
    public String lastNameFirstName() {return User.toStringFnameLname(transfer.getCreated_by());}
    public String amountWithSign() {return transfer.toStringAmount(account.getId());}
    public String displayAccount(int idAccountToDisplay) {
        Account accountToDisplay = Account.getById(idAccountToDisplay);
        return accountToDisplay.toStringDetailTransferAccounts() + (account.getId() == idAccountToDisplay ? " (your account)" : "");
    }
    public String saldo(){return transfer.Saldo(account.getId());}

    public void deleteTransferCategory() {
        if(askConfirmation("You are about to modify this transfer.", "Save changings")) {
            TransferCategory.delete(transfer.getId(), account.getId());
            showMessage("Transfer modified successfully!", "INFO", MessageDialogButton.OK);
            view.close();
        }
    }



    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

}