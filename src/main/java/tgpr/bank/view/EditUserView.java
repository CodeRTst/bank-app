package tgpr.bank.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.bank.model.UserValidator;
import tgpr.framework.Layouts;
import tgpr.framework.Tools;
import tgpr.bank.controller.EditUserController;
import tgpr.bank.model.User;
import java.util.List;
import java.util.regex.Pattern;

public class EditUserView extends DialogWindow {

        private final EditUserController controller;

        private final TextBox txtFirstName;
        private final TextBox txtLastName;
        private final TextBox txtBirthDate;
        private final TextBox txtEmail;
        private final TextBox txtPassword;
        //private final ComboBox<Integer> cboAgency;
        private final ComboBox<String> cboAgency;
        //private final TextBox txtType;
        private final String txtType;
        private final Label errFirstName;
        private final Label errLastName;
        private final Label errBirthDate;
        private final Label errEmail;
        private final Label errPassword;
        private final Label errAgency;
        //private final Label errType;

        private final Button btnAddUpdate;
        private Button btnDelete=null;

        private final Button btnClose;

        private final User user;

        public EditUserView(EditUserController controller, User user) {
            // définit le titre de la fenêtre
            super((user == null ? "Add " : "Update ") + "Client");

            this.user = user;
            this.controller = controller;

            setHints(List.of(Hint.CENTERED));
            // permet de fermer la fenêtre en pressant la touche Esc
            setCloseWindowWithEscape(true);
            // définit une taille fixe pour la fenêtre de 15 lignes et 70 colonnes
            //setFixedSize(new TerminalSize(50, 20));

            Panel root2= new Panel();
            setComponent(root2);

            Panel root = new Panel();
            root.setLayoutManager(new GridLayout(2).setTopMarginSize(1)).addTo(root2);;

            new Label("Email:").addTo(root);
            txtEmail = new TextBox(new TerminalSize(20, 1)).addTo(root)
                    .setValidationPattern(Pattern.compile("[\\w.@]{0,20}"))
                    .setTextChangeListener((txt, byUser) -> validate());
                    //.setReadOnly(user != null);
            new EmptySpace().addTo(root);
            errEmail = new Label("").addTo(root)
                    .setForegroundColor(TextColor.ANSI.RED);

            new Label("Password:").addTo(root);
            txtPassword = new TextBox(new TerminalSize(20, 1)).addTo(root)
                    .setMask('*')
                    .setTextChangeListener((txt, byUser) -> validate());
            new EmptySpace().addTo(root);
            errPassword = new Label("").addTo(root).setForegroundColor(TextColor.ANSI.RED);

            new Label("Last Name:").addTo(root);
            txtLastName = new TextBox(new TerminalSize(20, 1)).addTo(root)
                    .setValidationPattern(Pattern.compile("[\\u00C0-\\u017Fa-zA-Z ']+([- ][\\u00C0-\\u017Fa-zA-Z']+){0,20}"))
                    .setTextChangeListener((txt, byUser) -> validate());
                    //.setReadOnly(user != null);
            new EmptySpace().addTo(root);
            errLastName = new Label("").addTo(root)
                    .setForegroundColor(TextColor.ANSI.RED);

            new Label("First Name:").addTo(root);
            txtFirstName = new TextBox(new TerminalSize(20, 1)).addTo(root)
                    .setValidationPattern(Pattern.compile("[\\u00C0-\\u017Fa-zA-Z ']+([- ][\\u00C0-\\u017Fa-zA-Z']+){0,20}"))
                    .setTextChangeListener((txt, byUser) -> validate());
                    //.setReadOnly(user != null);
            new EmptySpace().addTo(root);
            errFirstName = new Label("").addTo(root)
                    .setForegroundColor(TextColor.ANSI.RED);

            new Label("Birth Date:").addTo(root);
            txtBirthDate = new TextBox(new TerminalSize(20, 1)).addTo(root)
                    .setValidationPattern(Pattern.compile("[\\d/]{0,10}"))
                    .setTextChangeListener((txt, byUser) -> validate());
            new EmptySpace().addTo(root);
            errBirthDate = new Label("").addTo(root).setForegroundColor(TextColor.ANSI.RED);

            txtType="Client";
            /*new Label("Type:").addTo(root);
            txtType = new TextBox(new TerminalSize(11, 1)).addTo(root)
                    .setValidationPattern(Pattern.compile("[aA-zZ][aA-zZ\\d]{0,7}"))
                    .setTextChangeListener((txt, byUser) -> validate());
                    //.setReadOnly(user != null);
            new EmptySpace().addTo(root);
            errType = new Label("").addTo(root)
                    .setForegroundColor(TextColor.ANSI.RED);
            */
            new Label("Agency:").addTo(root);

            /* ComboBox en Int == modifier attribut et les methodes ci-dessous
            cboAgency = new ComboBox<>(1, 2).addTo(root)
                    .setPreferredSize(new TerminalSize(20, 1));
            cboAgency.setSelectedItem(1);
            */
            //ComboBOx en String
            cboAgency = new ComboBox<>();
            cboAgency.addItem("Agence1");
            cboAgency.addItem("Agence2");
            cboAgency.addItem("Agence3");
            cboAgency.getSelectedIndex();
            cboAgency.addTo(root);
            cboAgency.addListener((selectedIndex, previousSelection, changedByUserInteraction) -> validate());
            new EmptySpace().addTo(root);
            errAgency = new Label("").addTo(root).setForegroundColor(TextColor.ANSI.RED);

            new EmptySpace().addTo(root);
            new EmptySpace().addTo(root);

            var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL)).setLayoutData(Layouts.LINEAR_CENTER);

            btnAddUpdate = new Button(user == null ? "Create" : "Save", this::add).addTo(buttons);
            if(user !=null){
                btnDelete = new Button("Delete", this::delete).addTo(buttons);
            }
            btnClose = new Button("Close", this::close).addTo(buttons);
            root2.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
            //root2.addComponent(buttons, GridLayout.createHorizontallyFilledLayoutData(2));



            setComponent(root2);

            if (user != null) {
                txtEmail.setText(user.getEmail());
                txtLastName.setText(user.getLastName());
                txtFirstName.setText(user.getFirstName());
                txtBirthDate.setText(Tools.toString(user.getBirthDate()));
                //txtType.setText(user.getType());
                //cboAgency.setSelectedItem(user.getAgency());
                cboAgency.setSelectedIndex(user.getAgency()-1);

            }

        }

        private void add() {
            controller.save(
                    txtEmail.getText(),
                    txtPassword.getText(),
                    txtLastName.getText(),
                    txtFirstName.getText(),
                    txtBirthDate.getText(),
                    //txtType.getText(),
                    txtType,
                    //cboAgency.getSelectedItem()
                    cboAgency.getSelectedIndex()+1

            );
        }
            private void validate() {
            var errors = controller.validate(
                    txtEmail.getText(),
                    txtPassword.getText(),
                    txtLastName.getText(),
                    txtFirstName.getText(),
                    txtBirthDate.getText(),
                    //txtType.getText(),
                    txtType,
                    //cboAgency.getSelectedItem()
                    cboAgency.getSelectedIndex()+1

            );

            errEmail.setText(errors.getFirstErrorMessage(User.field.Email));
            errPassword.setText(errors.getFirstErrorMessage(User.field.Password));
            errLastName.setText(errors.getFirstErrorMessage(User.field.LastName));
            errFirstName.setText(errors.getFirstErrorMessage(User.field.FirstName));
            errBirthDate.setText(errors.getFirstErrorMessage(User.field.Birthdate));


            btnAddUpdate.setEnabled(errors.isEmpty());
            //btnDelete.setEnabled(errors.isEmpty());

        }

    private void delete() {
        controller.delete();
    }



}
