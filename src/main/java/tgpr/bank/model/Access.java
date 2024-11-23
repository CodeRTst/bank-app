package tgpr.bank.model;

import tgpr.framework.Model;
import tgpr.framework.Params;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Access extends Model {

    private int user;
    private int account;
    private String type;
    private enum type{holder,proxy};

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        user =rs.getInt("user");
        account=rs.getInt("account");
        type=rs.getString("type");
    }

    @Override
    public String toString() {
        return "Access{" +
                "user=" + user +
                ", account=" + account +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public void reload() {
        reload("select * from access where user=:user", new Params("user", user));

    }

    public static List<Access> getAll(){
        return queryList(Access.class, "select * from access order by user");

    }

    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }



    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }





}
