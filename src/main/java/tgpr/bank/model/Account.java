package tgpr.bank.model;


import tgpr.framework.Model;
import tgpr.framework.Params;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;




public class Account extends Model {

    public enum field {iban,title}
    public static Account a;
    private int id;
    private String iban;
    private String title;
    private double floor;
    private double saldo;
    private String type;
    private String ibanTitle;
    private static Account currentAccount = null;





    @Override

    public String toString() {
        if (this.iban == null)
            return "--- Encode IBAN myself ---";
        else if (isFavouriteAccount())
            return iban + " | " + title + " | " + "favoris";
        else if (Security.isAdmin() && type.equals("external"))
            return iban + " | " + title + " | " + "external";
        else if (Security.isAdmin() && !type.equals("external"))
            return iban + " | " + title + " | " + type + " | " + signSaldo() + " €";
        else if (!type.equals("external") && isNotAccountOfLoggedUser(Security.getLoggedUser().getId()))
            return ibanTitle;
        else if (!this.type.equals("external")) //) && //compte non externe && compte lui appartient
            return iban + " | " + title + " | " + type + " | " + signSaldo() + " €";




        return ibanTitle;
    }



    public  String toStringSaldo() {

        if(saldo<0)
            return String.format( "%.2f", saldo )+" €";
        else
            return "+"+String.format( "%.2f", saldo )+" €";
    }

    public  String toStringFloor() {

        if(floor<0)
            return String.format( "%.2f", floor )+" €";
        else
            return "+"+ String.format( "%.2f", floor )+" €";

    }


    public String signSaldo() {return saldo > 0 ? "+" + saldo : "" + saldo;}
    public String toStringDetailTransferAccounts() {
        return iban + " | " + title + " | " + type  + (type.equalsIgnoreCase("external") ? "" : " | " + signSaldo());
    }

    public String toStringTransfer() {

        return   iban + " - " + title +" ";

    }







    public Account(){

    }
    public Account(int id, String iban, String title, double floor,String type, double saldo) {
        this.id = id;
        this.iban = iban;
        this.title = title;
        this.floor = floor;
        this.saldo = saldo;
        this.type=type;
    }

    public Account(String iban, String title, String type) {
        this.iban = iban;
        this.title = title;
        this.type = type;
    }







    public static void setCurrentAccount(Account a){currentAccount = a;}
    public static Account getCurrentAccount(){return currentAccount;}
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public double getFloor() {
        return floor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getIbanTitle() {return ibanTitle;}






    protected void mapper(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        iban =rs.getString("iban");
        title =rs.getString("title");
        floor =rs.getDouble("floor");
        type=rs.getString("type");
        saldo=rs.getDouble("saldo");
        ibanTitle = iban + " - " + title;
    }
    @Override
    public void reload() {
        reload("select * from account where id=:id", new Params("id", id));
    }







    public static List<Account> getListSourceAccountByIdUser(int idUser) {
        return queryList(Account.class,"select account.* from account join access on account.id = access.account " +
                "where access.user =:idUser",new Params("idUser",idUser));
    }


    public static List<Account> getAddFavoritAccountsFromHistory(int idUser, int idAccount) {
        return queryList(Account.class,
                "SELECT DISTINCT account.* " +
                        "FROM account, (SELECT t.* FROM account acc, transfer t " +
                        "WHERE (acc.id = t.source_account or (t.target_account =:idAccount AND t.state <> 'future')) " +
                        "AND acc.id IN (SELECT account from access WHERE user =:idUser)) AS tab " +
                        "WHERE (account.id = tab.source_account OR account.id = tab.target_account) " +
                        "AND account.id NOT IN (SELECT access.account FROM access WHERE user =:idUser) " +
                        "AND account.id NOT IN (SELECT account from favourite)",new Params().add("idUser", idUser).add("idAccount",idAccount));
    }

    public static List<Account> getListTargetAccountsExceptSourceAccount(Account a, int idUser) {
        int idAccount = a.getId();
        return queryList(Account.class,"select * from account " +
                "where account.id <> :idAccount and (account.id IN (SELECT favourite.account from favourite WHERE favourite.user =:idUser) " +
                " OR account.id IN (SELECT access.account FROM access WHERE access.user =:idUser))",new Params().add("idAccount",idAccount).add("idUser",idUser));
    }

    public boolean isFavouriteAccount() {
        int idUser =  Security.getLoggedUser().getId();
        return queryOne(Account.class,"SELECT * FROM account WHERE id =:idAccount and " +
                "id IN (SELECT account FROM favourite WHERE user =:idUser)", new Params().add("idUser", idUser).add("idAccount", id)) != null;
    }

    public boolean isNotAccountOfLoggedUser(int idUser) {
        return queryOne(Account.class,"select * from account where id =:idAccount and " +
                        "id in (select account from access where user =:idUser)",
                new Params().add("idAccount",id).add("idUser", idUser)) == null;
    }

    public static List<Account> getTargetAccountsForAdmin(Account a) {
        return queryList(Account.class,"select * from account where id <> :idAccount" ,new Params("idAccount",a.getId()));
    }

    public static List<Account> getSourceAccountForAdmin() {
        return queryList(Account.class,"select * from account where type <> 'external'");
    }

    public static List<Account> getAll(int idUser) {
        return queryList(Account.class, "select * from account a join access ac on a.id = ac.account "+
                "where ac.user =:idUser", new Params("idUser",idUser));

    }


    public static List<Account> getAllFavouritsAccounts(int idUser) {
        return queryList(Account.class, "select * from account " +
                        "where account.id IN (SELECT favourite.account from favourite WHERE favourite.user =:idUser) "
                , new Params("idUser", idUser));
    }



    public static Account getByIban(String iban) {
        return queryOne(Account.class, "select * from account where iban=:iban", new Params("iban", iban));
    }
    public static Account getById(int id) {
        return queryOne(Account.class, "select * from account where id=:id", new Params("id", id));
    }






    public boolean save() {
        int c;
        Account m = getById(id);
        String sql;
        if (m == null)
            sql = "insert into account (id,iban, title, floor,type, saldo) " +
                    "values (:id,:iban,:title,:floor,:type,:saldo)";
        else
            sql = "update account set id=:id, iban=:iban,title=:title, floor=:floor, type =:type, saldo=:saldo " +
                    "where id=:id";
        c = execute(sql, new Params()
                .add("id", id)
                .add("iban", iban)
                .add("title", title)
                .add("floor", type.equals("external") ? null : floor)
                .add("type", type)
                .add("saldo", type.equals("external") ? null : saldo));

        return c == 1;
    }

    public boolean delete() {
        int c = execute("delete from account where id=:id", new Params("id", id));
        return c == 1;
    }


}

