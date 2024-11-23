package tgpr.bank.model;


public class Security {

    public static boolean isRealDateTime = true;
    private static Global systemDate =null;
    public static void setDateSystem(Global date) {
        systemDate = date;
    }
    public static Global getSystemDate() {
        return systemDate;
    }


    public static User getLoggedUser() {
        return loggedUser;
    }

    //A SUPPRIMER j'ajout de la methode set pour devloppoment du UC_categoryliste ------
    public static void setLoggedUser(User loggedUser) {
        Security.loggedUser = loggedUser;
    }
//------------------------------------------------------
    private static User loggedUser = null;
    public static void login(User user) {
        loggedUser = user;
    }

    public static boolean isLogged() {
        return loggedUser != null;
    }

    public static boolean isLoggedUser(User user) {
        return loggedUser.equals(user);
    }

    public static void logout() {
        login(null);
        isRealDateTime = true;
        //systemDate.delete();

    }

    public static boolean isAdmin() {
        return loggedUser != null && loggedUser.isAdmin();
    }

    public static boolean isManager() {
        return loggedUser != null && loggedUser.isManager();
    }
    }

