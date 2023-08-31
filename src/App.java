import Conexion.*;
import Mundo.User;
//import Mundo.User;
public class App {
    public static void main(String[] args) {
        try {
            UserDAO miUserDAO = new UserDAO();
            System.out.println(miUserDAO.ReadAll()[2].GetAllProperties());
            User mio = new User(0, "juan", "ha@ja", "123", "user", null, null);
            mio.setCreate_at(null);
            miUserDAO.InsertNewRegister(mio);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
