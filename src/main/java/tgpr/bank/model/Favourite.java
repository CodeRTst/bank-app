package tgpr.bank.model;

import tgpr.framework.Model;
import tgpr.framework.Params;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;



public class Favourite extends Model {

    private int idUser;
    private int idaccount;

    private String title;
    private String type;
    private String iban;



    public Favourite() {
    }



    public Favourite(int idUser, int idaccount) {
        this.idUser = idUser;
        this.idaccount = idaccount;

    }

    public int getIduser() {
        return idUser;
    }

    public void setIduser(int iduser) {
        this.idUser = iduser;
    }

    public int getIdaccount() {
        return idaccount;
    }

    public void setIdaccount(int idaccount) {
        this.idaccount = idaccount;
    }



    protected void mapper(ResultSet resultSet) throws SQLException {
        idaccount = resultSet.getInt("account");
        idUser = resultSet.getInt("user");
        //iban = resultSet.getString("iban");
        //title = resultSet.getString("title");
        //type = resultSet.getString("type");


    }


    public static List<Favourite> getAll() {
        return queryList(Favourite.class, "select * from account, favourite where account.id = favourite.account;");
    }

    public static List<Favourite> getAllFavouritsAccounts(int idUser) {
         return queryList(Favourite.class, "select * from account " +
                        "where account.id IN (SELECT favourite.account from favourite WHERE favourite.user =:idUser) "
                , new Params("idUser", idUser));
    }

    public static Favourite getByIdaccount(int idaccount) {
        return queryOne(Favourite.class, "select * from favourite,account where account.id = favourite.account AND account=:idaccount", new Params("idaccount", idaccount));
    }

    public static Favourite getByIdUserIdAccount(int iduser, int idAccount) {
        return queryOne(Favourite.class,"select * from favourite where user =:idUser " +
                "and account =:idAccount",new Params().add("idUser",iduser).add("idAccount",idAccount));
    }

    public boolean delete() {
        int c = execute("delete from favourite where account=:idAccount and  " +
                "user =:idUser", new Params().add("idUser",idUser).add("idAccount",idaccount));
        return c == 1;
    }

    //la methode save ne modifie pas la table, car on ne modifie pas des clés etrangères
    public boolean save() {
        int c = 0;
        Favourite f = getByIdUserIdAccount(idUser,idaccount);
        if (f == null) {
            c = execute("insert into favourite (user, account) values (:idUser,:idAccount)", new Params()
                    .add("idUser", idUser)
                    .add("idAccount", idaccount));
        }
        return c == 1;
    }



    @Override
    public void reload() {
        reload("select account.iban from account, favourite where account.id = favourite.account;", new Params("idaccount", idaccount));
    }


    @Override
    public String toString() {
        return iban + " - " + title + "\t";

    }

    public String getTitle() {
        return " - " + title;
    }

    public String getType() {
        return type;
    }

    public static Favourite getByIdaccountIduser(int idUser,int idaccount) {
        return queryOne(Favourite.class,"SELECT * FROM favourite WHERE account=:idaccount AND user =:idUser",new Params("idaccount",idaccount).add("idUser", idUser));
    }

    /*public static List<Favourite> getListTargetAccountNotInFavourite(int idUsern ) {

        return queryList(Favourite.class,"select * from account " +
                "where account.id <> :idAccount and (account.id IN (SELECT favourite.account from favourite WHERE favourite.user =:idUser) " +
                " OR account.id IN (SELECT access.account FROM access WHERE access.user =:idUser))",new Params().add("idAccount",idAccount).add("idUser",idUser));
    }

     */

}


    /* JEREMY : test affichage favoris - CLI
    //tester dans le main bankapp
        var favourite = Favourite.getAll();
        for (var f : favourite)
            System.out.println(f);
    */

    /*
    //JEREMY : test affichage favoris view - UI
        Controller.navigateTo(new FavouriteListController());
   */

    /*
    //Jeremy:test delete -- CLI
        var test = Favourite.getByIdaccount(5);
        assert test != null;
        System.out.println(test);
        // suppression
        var res = test.delete();
        assert res;
        assert Favourite.getByIdaccount(5) == null




 */

/*          test ajout ds la BDD Favourite
            var test = new Favourite(4,5);
            var res = test.save();

 */