package tgpr.bank.controller;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import tgpr.bank.model.Account;
import tgpr.bank.model.Category;
import tgpr.bank.model.User;
import tgpr.bank.view.AccountCategoryListView;
import tgpr.framework.Controller;

import java.util.List;

public class AccountCategoryListController extends Controller {
    private User user;
    private List<Category> category;

    public AccountCategoryListController(User u) {
        this.user  = u ;
    }

    public Window getView() {
        return new AccountCategoryListView(this);
    }

    public List<Category> getCategorys(int idaccount){
        return Category.getByAccount(idaccount);
    }
    public List<Category> getCategorysReset(int idaccount){
        return Category.getByAccountReset(idaccount);
    }

    public void  addCategory(String categorie, Account account){
        if (!categorie.equals("")) {
            var cate = new Category(categorie, account.getId());
            boolean categoryNotExist = cate.saveNewCategory();
            if (!categoryNotExist)
                showMessage("already exist this category","error",MessageDialogButton.Close);
        }
    }



    public void ceditUser(int userid) {
        navigateTo(new UserListController(userid));
    }

    public void editCategory(Category categorie) {
        navigateTo(new EditCategoryController(categorie));
    }

    public void editSystemFavorite() {
        showMessage(" \nedit system category not possible","error", MessageDialogButton.Close);
    }
}
