package tgpr.bank.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.bank.model.User;
import tgpr.bank.view.UserListView;
import tgpr.framework.Controller;
import java.util.List;


public class UserListController extends Controller {

    private final int userid;
    private List<User> uers;

    public UserListController(int userid) {
        this.userid = userid;
    }

    @Override
    public Window getView() {
        return new UserListView(this);
    }


    public List<User> getUsers(String filter) {
        return User.getByAgencyUser(this.userid,filter);
    }


    public User addUser() {
        var controller = new EditUserController();
        navigateTo(controller);
        return controller.getUser();
    }

    public void editUser(User user) {
        navigateTo(new EditUserController(user));
    }
}
