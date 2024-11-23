package tgpr.bank.model;

import tgpr.framework.Model;
import tgpr.framework.Params;
import tgpr.framework.Tools;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class User extends Model {



    public enum field {Email,Password,LastName,FirstName,Birthdate,type,agency }
    private int id;
    private String email;
    private String password;
    private String lastName;
    private String firstName;
    private String type;
    private LocalDate birthDate;

    private Agency agence;

    public Agency getAgence() {
        return agence;
    }

    private enum type {client, manager, admin};

    private Integer agency = 0;

    public static User checkCredentials(String email, String password) {
        var user = User.getByEmail(email);
        if (user != null && user.password.equals(Tools.hash(password)))
            return user;
        return null;
    }


    public int getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }



    public String getType() {
        return type;
    }
    public boolean isAdmin() {
        return type.equalsIgnoreCase("admin");
    }
    public boolean isManager() {
        return type.equalsIgnoreCase("manager");
    }

    public Integer getAgency() {
        return agency;
    }

    public void setAgency(Integer agency) {
        this.agency = agency;
    }



    public User() {
    }


    public User(String email, String password, String lastName, String firstName,
                LocalDate birthDate, String type, int agency) {
        this.email = email;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.type = type;
        this.agency = agency;

    }

    public User(String email, String password, String lastName, String type) {
        this.email = email;
        this.password = password;
        this.lastName = lastName;
        this.type = type;
        this.agency = null;
    }

    public User(String email, String lastName, String firstName, LocalDate birthDate) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        email = rs.getString("email");
        password = rs.getString("password");
        lastName = rs.getString("last_name");
        firstName = rs.getString("first_name");
        birthDate = rs.getObject("birth_date", LocalDate.class);
        agency = rs.getInt("agency");
        type = rs.getString("type");

    }

    @Override
    public void reload() {
        reload("SELECT * FROM user WHERE email = :email", new Params("email",email));
    }

    public static List<User> getAll() {
        return queryList(User.class,"SELECT * FROM user ORDER BY first_name");
    }

    public static List<User> getByAgencyUser(int agency,String filterText) {
        String filter = '%' + filterText + '%';
        Params params = new Params("filter", filter);
        return queryList(User.class,"SELECT * FROM user JOIN agency where user.agency =:agency and (first_name like :filter OR last_name like :filter OR email like :filter OR birth_date like :filter) group by user.email ORDER BY first_name",new Params().add("agency",agency).add("filter",filter));
    }



    public static User getByFirstName(String firstName) {
        return queryOne(User.class,"SELECT first_name FROM user WHERE first_name = :firstName",new Params().add("first_name",firstName));
    }

    public static User getByLastName(String lastName) {
        return queryOne(User.class,"SELECT last_name FROM user WHERE last_name = :lastName",new Params().add("last_name",lastName));
    }
    public static User getByAgency(int agency) {
        return queryOne(User.class,"SELECT * FROM user WHERE agency = :agency",new Params().add("agency",agency));
    }

    public static User getByFirstNameLastName(String firstName,String lastName) {
        return queryOne(User.class,"SELECT * FROM user WHERE first_name = :firstName AND last_name = :lastName",new Params().add("last_name",lastName).add("first_name",firstName));
    }

    public User getUserBySourceAccount(Account a) {
        return queryOne(User.class,"SELECT * FROM user WHERE id IN (SELECT user FROM access WHERE account =:idAccount)",
                new Params("idAccount", a.getId()));
    }
    public static User getByEmail(String email) {
        return queryOne(User.class,"SELECT * FROM user WHERE email =:email",new Params("email",email));
    }
    public static User getUserById(int id){
        return queryOne(User.class,"SELECT * FROM user WHERE id=:id",new Params("id",id));
    }
    public static String toStringFnameLname(int  idUSer){
        //pour afficher dans les details de virement
        User user=getUserById(idUSer);
        String fname=user.getFirstName();
        String lname=user.getLastName();

        return fname != null ? lname + ", " + fname : lname;
       /*
        String fullName="";
        if(!(fname ==null) && !(lname ==null)){
            fullName+=fname+", "+lname;
        } else if (fname ==null && !(lname ==null)) {
            fullName+=lname;
        }else if (!(fname ==null) && lname ==null){
            fullName+=fname;
        }
        return fullName;

         */
    }
    public boolean delete() {
        int c3 = execute("delete from transfer_category where account in (select id from account, access where id = account and user =:id)",new Params("id",id));
        int c2 = execute("delete from transfer where created_by=:user", new Params("user", id));
        int c0 = execute("delete from favourite where user=:user", new Params("user", id));
        int c1 = execute("delete from access where user=:user", new Params("user", id));
        int c = execute("delete from user where email=:email", new Params("email", email));
        return c == 1;
    }

    public boolean save() {
        int c;
        User u = getByEmail(email);
        String sql;
        if (u == null)
            sql = "insert into user (email, password, last_name, first_name, birth_date,type, agency) " +
                    "values (:email,:password,:lastName,:firstName, :birthDate, :type,:agency)";
        else if (password == null || password.isBlank())
            sql = "update user set last_name=:lastName, first_name=:firstName,birth_date=:birthDate,type=:type,agency=:agency" +
                    " where email=:email";
        else
            sql = "update user set password=:password, last_name=:lastName, first_name=:firstName,birth_date=:birthDate,type=:type,agency=:agency" +
                    " where email=:email";
        c = execute(sql, new Params()
                .add("email", email)
                .add("password", password)
                .add("lastName", lastName)
                .add("firstName", firstName)
                .add("birthDate", birthDate)
                .add("type", type)
                .add("agency", agency));
        return c == 1;
    }




    @Override
    public boolean equals(Object o) {
        // s'il s'agit du même objet en mémoire, retourne vrai
        if (this == o) return true;
        // si l'objet à comparer est null ou n'est pas issu de la même classe que l'objet courant, retourne faux
        if (o == null || getClass() != o.getClass()) return false;
        // transtype l'objet reçu en User
        User user = (User) o;
        // retourne vrai si les deux objets ont le même Nom de famille
        // remarque : cela veut dire que les deux objets sont considérés comme identiques s'ils on le même email
        //            ce qui a du sens car c'est la clef primaire de la table. Attention cependant car cela signifie
        //            que si d'autres attributs sont différents, les objets seront malgré tout considérés égaux.
        return user.equals(user.email);
    }

    @Override
    public int hashCode() {
        // on retourne le hash code du email qui est "unique" puisqu'il correspond à la clef primaire
        return Objects.hash(email);
    }







}
