import Conexion.*;
import Mundo.User;

public class App {
    public static void main(String[] args) {
        try {
            UserDAO miUserDAO = new UserDAO();
            System.out.println(miUserDAO.ReadAll()[0].GetAllProperties());
            User nuevo = new User(0, "juan", "jl@gmail", "123", "test", null, null);
            nuevo.setCreate_at();
            //miUserDAO.InsertNewRegister(nuevo);
            //miUserDAO.EliminarRegistro("nombre: juan");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
