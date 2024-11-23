package tgpr.bank.controller;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import tgpr.bank.BankApp;
import tgpr.bank.model.*;
import tgpr.bank.view.LoginView;
import tgpr.framework.*;
import tgpr.framework.Controller;
import tgpr.framework.ErrorList;
import tgpr.framework.Model;
import tgpr.framework.Error;



public class LoginController extends Controller {
    private User user;
    private LoginView view;
    public ErrorList login(String email, String password, String date){
        var errors = new ErrorList();
        errors.add(UserValidator.isValidEmail(email));
        errors.add(UserValidator.isValidPassword(password));
        errors.add(Tools.isValidDateTime(date) ? Error.NOERROR : new Error("Erreur date invalide" + "", Global.field.system_date));

        if (errors.isEmpty()) {
            var user = User.checkCredentials(email, password);
            this.user = user;
            Global sysDate = new  Global(Tools.toDateTime(date));

            if (user != null) {
                Security.login(user);
                Security.setDateSystem(sysDate);

               // ici j'enregistre les date de connection dans la db a supprimer si pas besoin ---------------------
                sysDate.save();
                Transfer.updateTransferBackToFuture();
                //-------------------------------------------------------------------------------------------------

            }


        }

        return errors;
    }

    public void navigate() {
        try {
            if(user.isManager()){
                navigateTo(new AgencyListController());
            }
            else
                navigateTo(new AccountsListController(Security.getLoggedUser()));
        } catch (Exception e){
            showMessage("  email or password incorrect   ","erreur", MessageDialogButton.Close);
        }

    }



    @Override
    public Window getView() {
        view = new LoginView(this);
        return view;
    }

    public void seedData() {
        Model.seedData(BankApp.DATABASE_SCRIPT_FILE);
    }
    public static void exit() {
        System.exit(0);
    }


    public static void logout() {
        Security.logout();
        navigateTo(new LoginController());
    }
    // public void showAccount() {
      //  Controller.navigateTo(new AccountsListController(user));
    //}




}
