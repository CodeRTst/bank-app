package tgpr.bank.view;


import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.framework.ColumnSpec;
import tgpr.framework.ObjectTable;
import tgpr.framework.Tools;
import tgpr.bank.controller.UserListController;
import tgpr.bank.model.User;
import tgpr.framework.Panels;


import java.util.List;

public class UserListView extends DialogWindow {

    static List<User.field> SortableFields = List.of(User.field.FirstName,User.field.LastName,User.field.Email, User.field.Birthdate);
    private final UserListController controller;
    private final ObjectTable<User> table;
    private final TextBox txtFilter;

    public UserListView(UserListController controller) {
        super("Clients");
        this.controller = controller;

        setTitle("Agency Details");
        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);

        // Le panel 'root' est le composant racine de la fenêtre (il contiendra tous les autres composants)
        Panel root2 = new Panel();;
        setComponent(root2);

        Panel root = Panels.verticalPanel(LinearLayout.Alignment.Center);
        root.withBorder(Borders.doubleLine("Clients")).addTo(root2);

        Panel content = new Panel().addTo(root).setLayoutManager(new LinearLayout(Direction.VERTICAL));

        Panel filter = new Panel();
        filter.setLayoutManager(new GridLayout(30));
        filter.addComponent(new Label("Filter:"));
        txtFilter = new TextBox();
        txtFilter.setTextChangeListener((txt, byUser) -> reloadData());
        filter.addComponent(txtFilter);
        content.addComponent(filter);

        // ajoute une ligne vide
        new EmptySpace().addTo(content);
        // ajoute une ligne vide
        new EmptySpace().addTo(root);

        // crée un tableau de données pour l'affichage des clients #users

            table = new ObjectTable<>(
                    new ColumnSpec<>("first Name", User::getFirstName),
                    new ColumnSpec<>("last Name", User::getLastName),
                    new ColumnSpec<>("Email", User::getEmail),
                    //new ColumnSpec<>("Agency", User::getAgency),
                    new ColumnSpec<User>("Birth Date", u -> Tools.toString(u.getBirthDate()))

            );

        // ajoute le tableau au root panel
            //root.addComponent(table);
            content.addComponent(table);
        // spécifie que le tableau doit avoir la même largeur quee le terminal et une hauteur de 15 lignes

        table.setSelectAction(() -> {
            var user = table.getSelected();
            controller.editUser(user);
            reloadData();
            table.setSelected(user);
        });

       // new EmptySpace().addTo(root);

        // crée un bouton pour l'ajout d'un user et lui associe une fonction lambda qui sera appelée
        // quand on clique sur le bouton
        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        var btnAddUser = new Button("New Client", () -> {
            User u = controller.addUser();
            if (u != null)
                reloadData();
        }).addTo(buttons);
        var btnClose = new Button("Close", this::close).addTo(buttons);
        root2.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
        // charge les données dans la table
        reloadData();
    }

    public void reloadData() {
        // vide le tableau
        table.clear();
        // demande au contrôleur la liste des membres
        //var users = controller.getUsers();
        var users = controller.getUsers(txtFilter.getText());
        // ajoute l'ensemble des membres au tableau
        table.add(users);
        // requis à cause d'un bug quand on ferme la fenêtre popup avec la touche Escape
        table.invalidate();
    }

}
