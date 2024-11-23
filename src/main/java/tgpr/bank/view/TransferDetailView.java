package tgpr.bank.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.bank.controller.TransferDetailController;
import tgpr.bank.model.*;
import tgpr.framework.Tools;

import java.util.List;

public class TransferDetailView extends DialogWindow {

    private final TransferDetailController controller;
    private final Transfer transfer;
    private final Account account;



    private final Label lblCreatedAt;
    private final Label lblEffectiveAt;
    private final Label lblCreatedBy;
    private final Label lblSourceAccount;
    private final Label lblTargetAccount;
    private final Label lblAmount;
    private final Label lblSaldoAfterTransfer;
    private final Label lblDescription;
    private final Label lblState;

    private final ComboBox<Category> cboCategory;




    public TransferDetailView(TransferDetailController controller, Transfer transfer,Account account) {
        super("View Transfer");

        this.controller = controller;
        this.transfer = transfer;
        this.account = account;


        setHints(List.of(Hint.CENTERED));
        setCloseWindowWithEscape(true);

        Panel root = new Panel();
        setComponent(root);

        Panel fields = new Panel().setLayoutManager(new GridLayout(2).setTopMarginSize(1)).addTo(root);

        fields.addComponent(new Label("Created At:"));
        lblCreatedAt = new Label("").addTo(fields).addStyle(SGR.BOLD);

        fields.addComponent(new Label("Effective At:"));
        lblEffectiveAt = new Label("").addTo(fields).addStyle(SGR.BOLD);

        fields.addComponent(new Label("Created By:"));
        lblCreatedBy = new Label("").addTo(fields).addStyle(SGR.BOLD);

        fields.addComponent(new Label("Source Account:"));
        lblSourceAccount = new Label("").addTo(fields).addStyle(SGR.BOLD);

        fields.addComponent(new Label("Target Account:"));
        lblTargetAccount = new Label("").addTo(fields).addStyle(SGR.BOLD);

        fields.addComponent(new Label("Amount:"));
        lblAmount = new Label("").addTo(fields).addStyle(SGR.BOLD);

        fields.addComponent(new Label("Soldo after transfer:"));
        lblSaldoAfterTransfer = new Label("").addTo(fields).addStyle(SGR.BOLD);

        fields.addComponent(new Label("Description:"));
        lblDescription = new Label("").addTo(fields).addStyle(SGR.BOLD);

        fields.addComponent(new Label("State:"));
        lblState = new Label("").addTo(fields).addStyle(SGR.BOLD);

        fields.addComponent(new Label("Category:"));

        cboCategory = new ComboBox<>("NO CATEGORY",Category.getByAccount(account.getId())).addTo(fields)
                .setPreferredSize(new TerminalSize(14, 1));

        new EmptySpace().addTo(root);

        var buttons = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        btnDeleteTransfer(buttons);
        new Button("Save", this::save).addTo(buttons);
        new Button("Close", this::close).addTo(buttons);

        root.addComponent(buttons, LinearLayout.createLayoutData(LinearLayout.Alignment.Center));

        refresh();

    }

    private void refresh() {

        if (transfer != null) {

            lblCreatedAt.setText(Tools.toString(transfer.getCreated_at()));
            lblEffectiveAt.setText(Tools.toString(transfer.getEffective_at()));
            lblCreatedBy.setText(controller.lastNameFirstName());
            lblSourceAccount.setText(controller.displayAccount(transfer.getSource_account()));
            lblTargetAccount.setText(controller.displayAccount(transfer.getTarget_account()));
            lblAmount.setText(controller.amountWithSign());
            lblSaldoAfterTransfer.setText(controller.saldo());
            lblDescription.setText(transfer.getDescription());
            lblState.setText(transfer.getState());
            cboCategory.addListener((selectedIndex, previousSelection, changedByUserInteraction) -> Category.getByAccount(account.getId()));
            //cboCategory.setSelectedItem(transfer.getCategory(account.getId()));
            setSelectedItemCategory();
        }

    }

    private void save() {

        if(!(cboCategory.getSelectedItem() ==null) && !(cboCategory.getSelectedItem().getName().equalsIgnoreCase("No Category"))){
            controller.save(account.getId(),cboCategory.getSelectedItem().getId());
            refresh();
        }
       }




    private void setSelectedItemCategory(){//si il ya pas de category pour ce viremet
        Category c=new Category();
        c.setName("No Category");
        if(transfer.getCategory(account.getId())==null){
            cboCategory.addItem(c);
            cboCategory.setSelectedItem(c);
        }else{
            cboCategory.setSelectedItem(transfer.getCategory(account.getId()));
        }
    }

    private void delete() {
        controller.delete();
    }
    private  void btnDeleteTransfer(Panel panel){
        if(transfer.getState().equalsIgnoreCase("future")){
            new Button("Delete", this::delete).addTo(panel);
        }
    }

}





