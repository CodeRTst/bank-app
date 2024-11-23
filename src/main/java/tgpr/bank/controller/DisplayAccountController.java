package tgpr.bank.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.bank.model.*;
import tgpr.bank.view.DisplayAccountView;
import tgpr.framework.Controller;
import java.util.List;


public class DisplayAccountController extends Controller {

    private final AccountCategoryListController controllerCATE;
    private  final DisplayAccountView view;
    private final  Account account;
    private final User user;
    private  Favourite favourite;




    public DisplayAccountController(AccountCategoryListController controllerCATE, Account account, User user) {
        this.controllerCATE = controllerCATE;
        this.account = account;
        this.user= Security.getLoggedUser();
        view = new DisplayAccountView(this, controllerCATE, account,user);
    }

    @Override
    public Window getView() {
        return view;
    }



    public void addNewTransfer () {
        var controller = new CreatTransferController();
        navigateTo(controller);
        view.reloadTableData();
        view.reloadData();
        view.reloadDataAccount();
        view.refresh();
    }



    public List<Transfer> getTransfers(String filter) {
        List<Transfer> transfers=Transfer.getFiltered(filter,account);

        return transfers;
    }





    public void transferDetail(Transfer transfer) {
        navigateTo(new TransferDetailController(transfer,account));
        view.reloadData();
    }

    public void save(Account selectedItem) {
        favourite = new Favourite(user.getId(),selectedItem.getId());
        favourite.save();
    }



    public void removeFavourite(Account account,User user) {
        if(askConfirmation("Do you want to remove this account from your favourites ?  \n\n" + account.getIbanTitle() ,"Remove Favourite"))
        {
            Favourite.getByIdaccountIduser(user.getId(), account.getId()).delete();

        }


    }




}


