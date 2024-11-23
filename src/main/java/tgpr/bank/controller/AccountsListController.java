package tgpr.bank.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.bank.model.Account;
import tgpr.bank.model.Security;
import tgpr.bank.model.User;
import tgpr.bank.view.AccountsListView;
import tgpr.framework.Controller;
import java.util.List;



public class AccountsListController extends Controller {
    private final User user;
    private final AccountsListView view;


    private final AccountCategoryListController controllerCATE;

    public AccountsListController(User user){
        this.user= Security.getLoggedUser();
        view = new AccountsListView(this);
        this.controllerCATE = new AccountCategoryListController(user);
    }

    public List<Account> getAccounts() {
        return Account.getListSourceAccountByIdUser(user.getId());

    }




    public void addNewTransfer () {
        Account.setCurrentAccount(null);
        var controller = new CreatTransferController();

        navigateTo(controller);
    }






    public void showAccountDetail(Account account) {
        navigateTo(new DisplayAccountController( controllerCATE, account,user));
        view.reloadData();
    }


    @Override
    public Window getView() {
        return view;
    }

    public boolean isAdmin(){return user.isAdmin();}
}