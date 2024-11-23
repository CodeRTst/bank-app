package tgpr.bank.controller;


import com.googlecode.lanterna.gui2.Window;
import tgpr.bank.model.*;
import tgpr.bank.view.FavouriteAccountsView;
import tgpr.framework.Controller;


public class FavouriteAccountsController extends Controller {


    private User user ;
    private Favourite favourite ;
    private  final FavouriteAccountsView view;



    public FavouriteAccountsController(User user) {
        this.user = user;//Security.getLoggedUser();
        view = new FavouriteAccountsView(this,user);

    }

    public Window getView(){
        return view;
    }



    public void removeFavourite(Account account) {
        if(askConfirmation("Do you want to remove this account from your favourites ?  \n\n" + account.getIbanTitle() ,"Remove Favourite"))
        {
            Favourite.getByIdaccountIduser(user.getId(), account.getId()).delete();

        }


    }

    public void save(Account selectedItem) {
        favourite = new Favourite(user.getId(),selectedItem.getId());
        favourite.save();
    }


}
