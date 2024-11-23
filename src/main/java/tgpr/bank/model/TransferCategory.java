package tgpr.bank.model;

import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TransferCategory extends Model {

// Pas fait de constructeur parce ce que
// c'est juste une table de jointure donc pas de insert ici je pense.
// juste un getAll et le reload avec le id de transfer
    private int transfer;
    private int account;
    private int category;


    public int getTransfer() {
        return transfer;
    }

    public void setTransfer(int transfer) {
        this.transfer = transfer;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }



    public TransferCategory() {}

    public TransferCategory(int transfer, int account, int category) {
        this.transfer = transfer;
        this.account = account;
        this.category = category;
    }


    @Override
    public String toString() {
        return "TransferCategory{" +
                "transfer=" + transfer +
                ", account=" + account +
                ", category=" + category +
                '}';
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        transfer = rs.getInt("transfer");
        account=rs.getInt("account");
        category=rs.getInt("category");
    }

    @Override
    public void reload() {
            reload("select * from transfer_category where transfer=:transfer", new Params("transfer", transfer));

    }

    public static List<TransferCategory> getAll(){
        return queryList(TransferCategory.class, "select * from transfer_category");

    }

    public static TransferCategory getByIdTransferIdAccount(int transfer, int account) {
        return queryOne(TransferCategory.class,"select * from transfer_category " +
                "where transfer =:idTransfer and account =:idAccount",
                new Params().add("idTransfer", transfer).add("idAccount", account));
    }

    public boolean save() {
        int c;
        TransferCategory t = getByIdTransferIdAccount(transfer, account);
        String sql;
        if (t == null)
            sql = "insert into transfer_category (transfer, account, category) " +
                    "values (:idTransfer, :idAccount, :idCategory)";
        else
            sql = "update transfer_category set category =:idCategory " +
                    "where transfer =:idTransfer and account =:idAccount";
        c = execute(sql, new Params()
                .add("idTransfer", transfer)
                .add("idAccount", account)
                .add("idCategory",category)
                );

        return c == 1;
    }


    public static boolean delete(int idTransfer, int idAccount) {
        int c = execute("delete from transfer_category where transfer =:idTransfer and account =:idAccount"
                , new Params().add("idTransfer",idTransfer).add("idAccount",idAccount));
        return c == 1;
    }

}
