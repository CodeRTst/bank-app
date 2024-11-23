package tgpr.bank.view;



import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import tgpr.bank.controller.AccountsListController;
import tgpr.bank.controller.AgencyListController;
import tgpr.bank.controller.LoginController;
import tgpr.bank.model.Global;
import tgpr.bank.model.Security;
import tgpr.bank.model.User;
import tgpr.framework.*;
import tgpr.framework.Panels;
import java.time.LocalDateTime;
import java.util.List;


public class LoginView extends BasicWindow {

    private final LoginController controller;
    private final TextBox txtEmail;
    private final TextBox txtPassword;
    private final Button btnLogin;

    private CheckBox chkDate;

    private TextBox customDate;
    private Label email;
    private Label password;
    private Label chkDateSystem;
    private Label customDateSystem;
    private Label errEmail;
    private Label errPassword;
    private Label errDate;

    public LoginView(LoginController controller)  {
        this.controller = controller;


        setTitle("Login");
        setHints(List.of(Hint.CENTERED));


        Panel root = new Panel();
        setComponent(root);

        Panel panel = new Panel().setLayoutManager(new GridLayout(4).setLeftMarginSize(1).setTopMarginSize(1))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);
        // INPUT EMAIL
        email = new Label("Email:").setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)).addTo(panel);
        txtEmail = new TextBox(new TerminalSize(20,1)).addTo(panel).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2))
                .setTextChangeListener((txt,byUser) -> login());
                // Ajouter les messge d'errer Appel une methode a chaque entree de char;
        new EmptySpace().addTo(panel).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));
        errEmail = new Label("").setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)).setForegroundColor(TextColor.ANSI.RED).addTo(panel);


        // INPUT PASSWORD
        password = new Label("Password:").setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)).addTo(panel);
        txtPassword = new TextBox().setMask('*').addTo(panel).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2))
                .setTextChangeListener((txt,byUser) -> login());
        new EmptySpace().addTo(panel).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2));
        errPassword = new Label("").setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)).setForegroundColor(TextColor.ANSI.RED).addTo(panel);

        //CHECKBOX DATE
        chkDateSystem = new Label("Use System Date/Time:").setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)).addTo(panel);
        chkDate = new CheckBox().setChecked(true).addTo(panel);
        chkDate.addListener((byUser)->getChkDate());
        new EmptySpace().addTo(panel);
        new EmptySpace().addTo(panel).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(4));


        // CUSTOM DATE SYSTEM
        customDateSystem = new Label("Custom System Date/Time :").setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)).addTo(panel);
        customDate= new TextBox().setText(String.valueOf(new Global(LocalDateTime.now()))).setReadOnly(true).addTo(panel)
                .setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)).setTextChangeListener((txt,byUser) -> login());
        errDate = new Label("").setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)).setForegroundColor(TextColor.ANSI.RED).addTo(panel);



        // BTN  DE LOGIN
        Panel buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL))
                .setLayoutData(Layouts.LINEAR_CENTER).addTo(root);
        btnLogin = new Button("Login", this::toController).addTo(buttons).setEnabled(false);
        Button btnExit = new Button("Exit", this::exit).addTo(buttons);


        // Espace de connection test

        Button btnSeedData = new Button("Reset Database", this::seedData);
        Panel debug = Panels.verticalPanel(LinearLayout.Alignment.Center,
                new Button("Login as default admin", this::logAsDefaultAdmin),
                new Button("Login as default manager", this::logAsDefaultManager),
                new Button("Login as default client", this::logAsDefaultClient),
                btnSeedData
        ).setLayoutData(Layouts.LINEAR_CENTER);
        debug.withBorder(Borders.singleLine(" For debug purpose ")).addTo(root);


        txtEmail.takeFocus();
    }


    private void toController() {
        controller.navigate();
    }

    private void logAsDefaultClient() {

        controller.login(Configuration.get("default.client.email"), Configuration.get("default.client.password"),  customDate.getText());
        Controller.navigateTo(new AccountsListController(Security.getLoggedUser()));
    }

    private void logAsDefaultManager() {
        Controller.navigateTo(new AgencyListController());
       ;

    }

    private void logAsDefaultAdmin() {

        controller.login(Configuration.get("default.admin.email"), Configuration.get("default.admin.password"), customDate.getText());
        Controller.navigateTo(new AccountsListController(Security.getLoggedUser()));
    }

    private void seedData() {
        controller.seedData();
        btnLogin.takeFocus();
    }


    private void exit() {
        controller.exit();

    }

    private void login() {
        var errors = controller.login(txtEmail.getText(), txtPassword.getText(),customDate.getText());

       errEmail.setText(errors.getFirstErrorMessage(User.field.Email));
        errPassword.setText(errors.getFirstErrorMessage(User.field.Password));
        errDate.setText(errors.getFirstErrorMessage(Global.field.system_date));

        btnLogin.setEnabled(errors.isEmpty());
    }

    public void setCustomDate(TextBox customDate) {
        this.customDate = customDate;
    }

    public void getChkDate() {
        if (chkDate.isChecked()) {
            customDate.setText(String.valueOf(new Global(LocalDateTime.now()))).setReadOnly(true);
            Security.isRealDateTime = true;
        }else{
            customDate.setReadOnly(false);
            Security.isRealDateTime = false;
        }
    }
}


