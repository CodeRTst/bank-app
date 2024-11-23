package tgpr.bank.view;


import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import tgpr.bank.controller.LoginController;
import tgpr.bank.model.User;
import tgpr.framework.*;
import tgpr.bank.controller.AgencyListController;
import tgpr.bank.model.Agency;
import java.util.List;

public class AgencyListView extends BasicWindow {
    private final AgencyListController controller;
    private final ObjectTable<Agency> table;
    private final Menu menuFile;

    public AgencyListView(AgencyListController controller) {
        this.controller = controller;
        setTitle("Manager");
        setHints(List.of(Hint.EXPANDED));

        // Le panel 'root' est le composant racine de la fenêtre (il contiendra tous les autres composants)
        Panel root = new Panel();
        setComponent(root);


        MenuBar menuBar = new MenuBar().addTo(root);
        menuFile = new Menu("File");
        menuBar.add(menuFile);
        MenuItem menuLogout = new MenuItem("Logout", () -> LoginController.logout());
        menuFile.add(menuLogout);
        MenuItem menuExit = new MenuItem("Exit", ()-> LoginController.exit());
        menuFile.add(menuExit);


        // ajoute une ligne vide
        new EmptySpace().addTo(root);

        // crée un tableau de données pour l'affichage des agences
        table = new ObjectTable<>(
                new ColumnSpec<>("Names", Agency::getName)
                //new ColumnSpec<>("ID", Agency::getId)
        );


        // ajoute le tableau au root panel
        root.addComponent(table);
        // spécifie que le tableau doit avoir la même largeur quee le terminal et une hauteur de 15 lignes
        table.setPreferredSize(new TerminalSize(ViewManager.getTerminalColumns(), 15));

        table.setSelectAction(() -> {
            int iduser=0;
            var agency = table.getSelected();
            int res = table.getSelected().getId();
            //System.out.println(res);
            if(res == 1){
                 iduser=1;
            }else if(res==2) iduser=2;
            else iduser=3;
            //System.out.println(iduser);
            controller.ceditUser(iduser);
            reloadData();

        });

        new EmptySpace().addTo(root);

        // crée un bouton pour l'ajout d'un client et lui associe une fonction lambda qui sera appelée
        // quand on clique sur le bouton
        var btnAddUser = new Button("New Client", () -> {
            User u = controller.addUser();
            if (u != null)
            reloadData();
        }).addTo(root);

        // charge les données dans la table
        reloadData();
    }
    public void reloadData() {
        // vide le tableau
        table.clear();
        // demande au contrôleur la liste des agences
        var agencies = controller.getAgencies();
        // ajoute l'ensemble des agences au tableau
        table.add(agencies);
    }






}