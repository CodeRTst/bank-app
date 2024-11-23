package tgpr.bank.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.bank.model.Agency;
import tgpr.bank.model.User;
import tgpr.bank.view.AgencyListView;
import tgpr.framework.Controller;

import java.util.List;

public class AgencyListController extends Controller {

    private List<Agency> agency;

    @Override
    public Window getView() {
        return new AgencyListView(this);
    }

    public List<Agency> getAgencies() {
        return Agency.getAll();
    }


    public User addUser() {
    var controller = new EditUserController();
    navigateTo(controller);
    return controller.getUser();
    }

    public void ceditUser(int userid) {
        navigateTo(new UserListController(userid));
    }

}