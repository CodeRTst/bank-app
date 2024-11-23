package tgpr.bank.model;

import tgpr.framework.Model;
import tgpr.framework.Params;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Agency extends Model{

    /*
    protected static Connection db;

    static {
        try {
            db = DriverManager.getConnection("jdbc:mariadb://localhost:3306/tgpr-2223-g01?user=root&password=");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */
    private String name;
    private int manager;
    private int id;

    public Agency() {
    }

    public Agency(String name) {
        this.name = name;
    }

    public Agency(Object getId) {
        super();
    }

    public String getName() {
        return name;
    }

    public int getManager() {
        return manager;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "agency{" +
                "name='" + name + '\'' +
                '}';
    }



    //Est-ce que c'est nécéssaire que je dois récupérer l'id et l'idManager
    /*
    public static void mapper(ResultSet rs, Agency agency) throws SQLException {
        agency.name = rs.getString("name");
        agency.id = rs.getInt(0);
        agency.manager = rs.getInt(0);

    }
    */

    protected void mapper(ResultSet resultSet) throws SQLException {
        name = resultSet.getString("name");
        id = resultSet.getInt(1);
        manager = resultSet.getInt(1);

    }

    @Override
    public void reload() {
        reload("select * from agency where name=:name", new Params("name", name));
    }

    public static List<Agency> getAll() {
        return queryList(Agency.class, "select * from agency order by name");
    }

    public static List<Agency> getNameID() {
        return queryList(Agency.class, "select name from agency");
    }

    public static Agency getByName(String name) {
        return queryOne(Agency.class, "select * from agency where name=:name", new Params("name", name));
    }

    public static Agency getById(int id) {
        return queryOne(Agency.class, "select name from agency where id=:id", new Params("id", id));
    }



}
