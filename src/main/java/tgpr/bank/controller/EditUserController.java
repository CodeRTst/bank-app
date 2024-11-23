package tgpr.bank.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.bank.model.User;
import tgpr.bank.model.UserValidator;
import tgpr.bank.view.EditUserView;
import tgpr.framework.Controller;
import tgpr.framework.Tools;
import tgpr.framework.ErrorList;

import java.time.LocalDate;

public class EditUserController extends Controller {
        private final EditUserView view;
        private User user;
        private final boolean isNew;

        public EditUserController() {
            this(null);
        }

        public EditUserController(User user) {
            this.user = user;
            isNew = user == null;
            view = new EditUserView(this, user);

        }

        public void save(String email,  String password,  String lastName, String firstName, String birthDate, String type, int agency) {
            var errors = validate(email,password,lastName,firstName,birthDate,type,agency);
            if (errors.isEmpty()) {
                var hashedPassword = password.isBlank() ? password : Tools.hash(password);
                user = new User(email,hashedPassword,lastName,firstName,Tools.toDate(birthDate),type,agency);
                user.save();
                view.close();
            } else
                showErrors(errors);
        }


        public ErrorList validate(String email,String password,String lastName, String firstName, String birthDate, String type,int agency) {
        var errors = new ErrorList();

        if (isNew) {
            errors.add(UserValidator.isValidEmail(email));
            errors.add(UserValidator.isValidUniqueEmail(email));
            errors.add(UserValidator.isValidPassword(password));
            errors.add(UserValidator.isValidLastName(lastName));
            errors.add(UserValidator.isValidFirstName(firstName));

        }

        if (!birthDate.isBlank() && !Tools.isValidDate(birthDate))
            errors.add("invalid birth date", User.field.Birthdate);


        var hashedPassword = password.isBlank() ? password : Tools.hash(password);
        var user = new User(email,hashedPassword,lastName,firstName,Tools.toDate(birthDate),type,agency);
        errors.addAll(UserValidator.validate(user));

        return errors;
    }

    public void delete() {
        if (askConfirmation("You are about to delete this client. Please confirm.", "Delete client")) {
            user.delete();
            view.close();
        }
    }



    @Override
    public Window getView() {
        return view;
    }

    public User getUser() {
        return user;
    }

    }


