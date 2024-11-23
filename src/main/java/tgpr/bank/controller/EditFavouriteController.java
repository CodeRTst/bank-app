package tgpr.bank.controller;


import tgpr.bank.model.Account;
import tgpr.bank.model.Favourite;
import tgpr.bank.model.Security;
import tgpr.bank.model.User;
import tgpr.bank.view.EditFavouriteView;
import tgpr.framework.Controller;


public class EditFavouriteController extends Controller {

    private  EditFavouriteView view;
    private final User user;
    private final Account account;




    public EditFavouriteController(Account account, User user) {
        this.account = account;
        this.user = Security.getLoggedUser();;
        view = new EditFavouriteView(this, account);

    }


    public void delete() {
        {
            Favourite.getByIdaccountIduser(user.getId(),account.getId()).delete();
            view.close();
        }
    }

    @Override
    public EditFavouriteView getView() {
            view = new EditFavouriteView(this,account);
            return view;

        }

}
