package tgpr.bank.controller;

import com.googlecode.lanterna.gui2.Window;
import tgpr.bank.model.Category;
import tgpr.bank.view.EditCategoryView;
import tgpr.framework.Controller;

public class EditCategoryController extends Controller {

    private final EditCategoryView view;


    private Category categorie;
    private boolean isNew;

    public EditCategoryController() {
        this(null);
    }

    @Override
    public Window getView() {
        return view;
    }

    public EditCategoryController(Category categorie) {
        this.categorie=categorie;
        isNew=categorie==null;
        view = new EditCategoryView(this, categorie);
    }
    public Category getCategorie() {
        return categorie;
    }


    public void save(String txtCategorie) {

        categorie.upadateCategory(txtCategorie);
        view.close();
    }
    public void save() {

        categorie.save();
        view.close();
    }
    public void delete(String categorie) {
        this.categorie.delete();
        view.close();
    }
}
