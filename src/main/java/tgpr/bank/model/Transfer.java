package tgpr.bank.model;


import tgpr.framework.Model;
import tgpr.framework.Params;
import tgpr.framework.Tools;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public class Transfer extends Model {



    public enum field {amount,description,created_at}

    private int id;
    private double amount;

    private String description;
    private int source_account;
    private int target_account;
    private double source_saldo;
    private double target_saldo;
    private LocalDateTime created_at;
    private int created_by;
    private LocalDate effective_at;
    private String state;
    private Account source;
    private Account target;


    //--------------back to the future-----------------------------------------------------------------------------------------------------------



    public static void updateTransfersInTheFuture() {
        execute("update transfer set state = 'ignored' where created_at >:dateSystem",new Params("dateSystem",Security.getSystemDate().getSystemDate()));
        execute("update transfer set state = 'executed',source_saldo = null, target_saldo = null  where created_at <=:dateSystem",new Params("dateSystem",Security.getSystemDate().getSystemDate()));
    }

    public static void saldoAccountToZero() {
        execute("update account set saldo = 0 where type <> 'external'",new Params());
    }

    public static void deleteTransferAndSetSaldoToZero() {
        updateTransfersInTheFuture();
        saldoAccountToZero();
    }

    public static List<Transfer> getListTransferBackTofuture() {
        return queryList(Transfer.class,"select * from transfer where state <> 'ignored' order by ifnull(effective_at, created_at)");

    }

    public static void updateTransferBackToFuture() {
        LocalDateTime dateTime = Security.getSystemDate().getSystemDate();
        deleteTransferAndSetSaldoToZero();
        List<Transfer> list = getListTransferBackTofuture();
        int i = 0;

        for (;i < list.size();i++) {
            Transfer t = list.get(i);
            Account source = Account.getById(t.getSource_account());
            Account target = Account.getById(t.getTarget_account());


            if (t.getEffective_at() != null && t.getEffective_at().isAfter(dateTime.toLocalDate())) {
                t.setState("future");
            } else {
                if (!(source.getType().equals("external")) && (source.getSaldo() - t.getAmount() < source.getFloor()))
                    t.setState("rejected");

                else {
                    t.setState("executed");
                    source.setSaldo(source.getSaldo() - t.getAmount());
                    target.setSaldo(target.getSaldo() + t.getAmount());
                    source.save();
                    target.save();
                    if (!source.getType().equals("external"))
                        t.setSource_saldo(source.getSaldo());
                    if (!target.getType().equals("external"))
                        t.setTarget_saldo(target.getSaldo());
                }

            }
            t.source = source;
            t.target = target;
            t.saveForBackFutur();
        }

    }


    //^^^^^^^^^^^^^^^^ back to the future ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^




    public Transfer() {}


    public Transfer(double amount, String description, int source_account, int target_account, double source_saldo, double target_saldo, LocalDateTime created_at, int created_by, LocalDate effective_at, String state) {

        this.amount = amount;
        this.description = description;
        this.source_account = source_account;
        this.target_account = target_account;
        this.source_saldo = source_saldo;
        this.target_saldo = target_saldo;
        this.created_at = created_at;
        this.created_by = created_by;
        this.effective_at = effective_at;
        this.state = state;

    }

    //constructeur avec tt les champs not null de la db pour les test
    public Transfer(String description, LocalDateTime created_at, String state, int source_account, int target_account, int created_by) {
        this.description = description;
        this.created_at= created_at;
        this.state=state;
        this.source_account=source_account;
        this.target_account=target_account;
        this.created_by=created_by;
    }


    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        id=rs.getInt("id");
       amount = rs.getDouble("amount");
       description = rs.getString("description");
       source_account = rs.getInt("source_account");
       target_account= rs.getInt("target_account");
       source_saldo = rs.getDouble("source_saldo");
       target_saldo = rs.getDouble("target_saldo");
       created_at = rs.getObject("created_at", LocalDateTime.class);
       created_by = rs.getInt("created_by");
       effective_at = rs.getObject("effective_at", LocalDate.class);
       state = rs.getString("state");
    }

    @Override
    public void reload() {
        reload("Select * from transfer where id=:id", new Params("id",id));

    }






    public static List<Transfer> getAll(){
        return queryList(Transfer.class, "select * from transfer order by id");

    }

    public static Transfer getByid(int id){
        return queryOne(Transfer.class,"Select * from transfer where id=:id", new Params("id",id));
    }


    public static List<Transfer> getFiltered(String filterText,Account account) {
        int idAccount=account.getId();
        String filter = '%' + filterText + '%';

        String sqlIbanSource="source_account in (select a.id from account a,transfer t where a.id=t.target_account and source_account=:idAccount and (title like :filter or iban like :filter) )";
        String sqlIbanTarget="target_account in (select a.id from account a,transfer t where a.id=t.source_account and target_account=:idAccount and (title like :filter or iban like :filter) )";
        String sqlCategory="id in (select tc.transfer from transfer_category tc,transfer t where tc.transfer=t.id and tc.category in(select id from category where name like :filter ))";
        Params params = new Params().add("filter", filter).add("idAccount",idAccount);

        // affichage le transfert correspondent à id de account selectionné et filtrer les résultat sur les tables transfer et category et account

        String sql = "select * from transfer where (description like :filter or source_account like :filter or target_account " +
                "like :filter or amount like :filter or source_saldo like :filter or target_saldo like :filter or state like :filter " +
                " or "+sqlIbanSource+
                " or "+sqlIbanTarget+
                " or "+sqlCategory+" and created_at <= (select Cast(system_date AS DATE) from global))"+" "+//effective_at <= (select system_date from global) or
                " and state <> 'ignored' and (source_account =:idAccount or (target_account =:idAccount and (state not in ('future','rejected'))))" +
                " order by ifnull(effective_at, created_at) DESC";

        return queryList(Transfer.class, sql,  params);

    }
    public  Category getCategory(int idAccount){

        return queryOne(Category.class,"select category.* from category ,transfer_category " +
                "where " +
                "transfer_category.category=category.id " +
                "and transfer_category.transfer=:idTransfer and transfer_category.account=:idAccount", new Params().add("idTransfer",id).add("idAccount",idAccount));
    }









    public int save() {
        return insert("insert into transfer(amount, description, source_account, target_account, source_saldo, target_saldo, created_at, created_by, effective_at, state) " +
                "values (:amount, :description, :source_account, :target_account, :source_saldo, :target_saldo, :created_at, :created_by, :effective_at, :state)",new Params()
                .add("amount", amount)
                .add("description", description)
                .add("source_account", source_account)
                .add("target_account", target_account)
                .add("source_saldo", state.equals("future") ? null : source_saldo)
                .add("target_saldo", state.equals("future") ? null : target_saldo)
                .add("created_at", created_at)
                .add("created_by", created_by)
                .add("effective_at", effective_at)
                .add("state", state));



    }

    public boolean saveForBackFutur() {
        int c = execute("update transfer set source_saldo =:source_saldo, target_saldo =:target_saldo, state =:state where id =:id", new Params()
                .add("source_saldo", state.equals("future") || state.equals("rejected") || source.getType().equals("external") ? null : source_saldo)
                .add("target_saldo", state.equals("future") || state.equals("rejected") || target.getType().equals("external") ? null : target_saldo)
                .add("state", state)
                .add("id",id));

        return c == 1;
    }

    public boolean saveCategoty(int idAccount,int idCategory) {
        int c;
        Category ct = this.getCategory(idAccount);
        String sql;

        if (ct == null )
            sql = "insert into transfer_category (transfer, account,category) values (:id,:idAccount,:idCategory)";
        else
            sql = "update transfer_category set category=:idCategory where transfer=:id and account=:idAccount";
        c = execute(sql, new Params()
                .add("id", id)
                .add("idAccount", idAccount)
                .add("idCategory", idCategory));
        return c == 1;
    }

    public boolean delete(int idAccount) {//ca doit supprimer uniquement le transfer. Supprimer un transferCategory ne ce fait pas a partir de cette classe
        //suppression dans la table de transfer_category(si il y a de category pour ce transfer et account) et puis dans transfer
        if(!(getCategory(idAccount) ==null)){
            deleteTransferCategory(idAccount);
        }
        int d = execute("delete from transfer where id=:id", new Params("id", id));
        return  d == 1;
    }
    public boolean deleteTransferCategory(int idAccount) {
        int c = execute("delete from transfer_category where transfer=:id and account=:idAccount" , new Params("id", id).add("idAccount",idAccount));
        return c==1 ;
    }

    public boolean delete() {
        int c = execute("delete from transfer where id=:id", new Params("id", id));
        return c == 1;
    }














    public int getId() {
        return id;
    }
    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public int getSource_account() {
        return source_account;
    }

    public int getTarget_account() {
        return target_account;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public int getCreated_by() {
        return created_by;
    }

    public LocalDate getEffective_at() {
        return effective_at;
    }

    public void setSource_saldo(double source_saldo) {
        this.source_saldo = source_saldo;
    }

    public void setTarget_saldo(double target_saldo) {
        this.target_saldo = target_saldo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }







    @Override
    public String toString() {
        return
                "id=" + id +
                        ", amount=" + amount +
                        ", source_account=" + source_account +
                        ", target_account=" + target_account +
                        ", source_saldo=" + source_saldo +
                        ", target_saldo=" + target_saldo +
                        ", created_at=" + created_at +
                        ", effective_at=" + effective_at +
                        ", state='" + state + '\'' +
                        "}\n";
    }

    public  String toStrigAnotherAccount(int idAccount){
        if(idAccount==target_account){
            return  Account.getById(source_account).toStringTransfer()+"  ";
        }else if(idAccount==source_account){
            return  Account.getById(target_account).toStringTransfer()+"  ";
        }
        return "";
    }



    public String toStringAmountTable(int id) {// cette methode est la meme que celle en dessous (toStringAmount)
        /// pour afficher dans le table de account history ,si le compte sélectionné est la destinataire affiche avec + sinon -
        String txtAmount=String.valueOf(amount);
        String space="";
        if(txtAmount.length()==3){
            space+= "   ";
        } else if (txtAmount.length()==4) {
            space+= "  ";
        }else if (txtAmount.length()>4) {
            space+= " ";
        }

        return  (target_account==id) ? space+"+"+String.format( "%.2f", amount )+" €" : space+"-"+String.format( "%.2f", amount )+" €";
    }

    public String toStringAmount(int id) {
        /// si le compte sélectionné est la destinataire affiche avec + sinon -
        return (target_account==id) ? "+"+String.format( "%.2f", amount )+" €" :"-"+String.format( "%.2f", amount )+" €";
    }

    public  String toStringSoldo(int id) {
        //afficher le solde de compte qui concerne le account sélectionné
        if (!state.equalsIgnoreCase("future")){
            if(target_account==id){
                if(target_saldo>0){
                    return "    +"+String.format( "%.2f", target_saldo )+" €";
                }else if(target_saldo<0){
                    return "    "+String.format( "%.2f", target_saldo )+" €";
                }
            }else if(source_account==id){
                if(source_saldo>0){
                    return "    +"+String.format( "%.2f", source_saldo )+" €";
                }else if(source_saldo<0){
                    return "    "+String.format( "%.2f", source_saldo )+" €";
                }
            }
        }

        return "";
    }


    public String Saldo(int id) {
        if (state.equalsIgnoreCase("future"))
            return "";
        double solde = source_account == id ? source_saldo : target_saldo;
        if(solde > 0 ){
            return  "+" +  solde+ " €";
        }else if(solde < 0){
            return solde+ " €";
        }
        return "";
    }


    public  String toStringAcountDetail(int id) {
        // pour afficher les details de virement quand un virement est sélectionnée
        double saldo =Account.getById(id).getSaldo();
        return  Account.getById(id).getIban() + " | " +
                Account.getById(id).getTitle() + " | " +
                Account.getById(id).getType() + " | " +
                (((Account.getById(id).getSaldo()>0)) ?"+"+saldo+" €":saldo+" €");
    }

    public String isYourAccount(int idAccount,int idAccountToCompare){
        if(idAccount==idAccountToCompare){
            return " (your account)";
        }
        return "";
    }
    public String getEffectiveAt(){
        // si il est y pas de date de effet il renvoie la date de création sinon c'est le date de effet

        return (effective_at==null) ? Tools.toString(created_at):Tools.toString(effective_at);
    }
/*
function main de BankApp pour le test du model
-------------------------------------------------

public static void main(String[] args) {
    Timestamp t = new Timestamp(new Date().getTime());
    var test = new Transfer("lala",t,"execute",1,2,1);
    boolean res= test.save();
    assert test !=null;
    System.out.println(test);
    res =test.delete();
    assert res;
    assert Transfer.getByid(27)==null;
    }

*/


}
