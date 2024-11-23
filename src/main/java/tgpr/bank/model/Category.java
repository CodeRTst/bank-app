package tgpr.bank.model;


import tgpr.framework.Model;
import tgpr.framework.Params;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Category extends Model {

    private int id;
    private String name;
    private int account;






    public Category() {
    }

    public Category(String name, int account) {
        this.name = name;
        this.account = account;

    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }




    @Override
    public String toString() {

        return name != null ? name : "NO CATEGORY" ;

    }

    public String type(){
        if(getId()<=10){
            return "system";
        }
        return "local";

    }





    protected void mapper(ResultSet rs) throws SQLException {
        id=rs.getInt("id");
        name = rs.getString("name");
        account = rs.getInt("account");
    }

    @Override
    public void reload() {
        reload("select * from category where name=:name", new Params("name", name));
    }







    public static List<Category> getAll() {

        return queryList(Category.class, "select * from category order by name");
    }
    public static List<Category> getByAccount(int account) {
        return queryList(Category.class, "select * from category where account=:account OR account is null order by name", new Params("account", account));
    }

    public static List<Category> getByAccountReset(int account) {
        return queryList(Category.class, "select * from category where  account is null", new Params("account", account));
    }


    public static List<Category> getAllCategoryByAccount(int idAccount) {
        return queryList(Category.class,"select * from category where account =:idAccount or account is null order by name",new Params("idAccount",idAccount));
    }

    public static Category getById(int id) {
        return queryOne(Category.class, "select * from category where id=:id", new Params("id", id));
    }

    public  Integer countUse() {
        return queryScalar(Integer.class, "SELECT count(*) FROM transfer_category where category =:category and account =:account",
                new Params("category", this.id).add("account", Account.getCurrentAccount().getId()));
    }

    public boolean saveNewCategory() {
        int c = 0;
        Category t = getByNameAndAccount(name,account);
        if (t == null) {
            c = execute("insert into category (name, account) values (:name,:account)", new Params()
                    .add("name", name)
                    .add("account", account));
        }
        return c == 1;
    }



    public static Category getByNameAndAccount(String name, int idAccount) {
        return queryOne(Category.class, "select * from category where name =:name and (account =:idAccount or account is null)",
                new Params().add("name", name).add("idAccount", idAccount));
    }
    public boolean save() {
        int c;
        Category t = getById(id);
        String sql;
        if (t == null)
            sql = "insert into category (name, account) values (:name,:account)";
        else
            sql = "update category set name=:name, account=:account where id=:id";
        c = execute(sql, new Params()
                .add("name", name)
                .add("account", account));
        return c == 1;
    }


    public boolean upadateCategory(String name){
        int c;
        String sql;
        sql = "update category set name=:name where id=:id";
        c = execute(sql, new Params()
                .add("name", name)
                .add("id", id)
        );

        return c == 1;

    }
    public boolean delete() {
        int d = execute("delete from transfer_category where category=:id", new Params("id", id));
        int c = execute("delete from category where id=:id", new Params("id", id));
        return c == 1 && d==1;
    }







    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



}






