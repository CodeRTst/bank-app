package tgpr.bank.model;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.sql.SQLException;
import tgpr.framework.Model;
import tgpr.framework.Params;
import tgpr.framework.Tools;

public class Global extends Model {

    public enum field {system_date};


    private  LocalDateTime systemDate ;
    public Global(LocalDateTime systemDate) {
        this.systemDate = systemDate;
    }
    public Global() {

    }

    public LocalDateTime getSystemDate() {
        return systemDate;
    }

    public void setSystemDate(LocalDateTime systemDate) {
        this.systemDate = systemDate;
    }
    public static Global getSystemDateRow() {return queryOne(Global.class,"select * from global limit 1");}

    @Override
    public String toString() {
        return
                Tools.toString(systemDate)
                ;
    }
    protected void mapper(ResultSet rs) throws SQLException {
        systemDate = rs.getObject("system_date", LocalDateTime.class);
    }

    @Override
    public void reload() {
        reload("select * from global where system_date=:systemDate", new Params("systemDate", systemDate));
    }



    public boolean save() {
        int c;
        String sql;
        sql = "update global set system_date=:systemDate";
        c = execute(sql, new Params().add("systemDate", systemDate));
        return c == 1;
    }


}

/*
function main de BankApp pour le test du model Global
-------------------------------------------------

public static void main(String[] args) {
        System.out.println("\nListe des dates :");
        var globals = Global.getAll();
        for (var g : globals)
            System.out.println(g);

        System.out.println("\nNouvelle Date 'test'");
        LocalDateTime date = LocalDateTime.of(5,5,5,5,5,5);
        var test = new Global(date);
        // sauvegarde
        boolean res = test.save();
        assert res;
        // relecture en BD
        test = Global.getBySystemDate(date);
        assert test != null;
        System.out.println(test);
        //delete
        res = test.delete();
        assert res;
        assert Global.getBySystemDate(date) == null;

}
 */
