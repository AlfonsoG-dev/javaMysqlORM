import Conexion.*;
import Mundo.User;

public class App {
    public static void main(String[] args) {
        try {
            UserDAO miUserDAO = new UserDAO();
            System.out.println("usuarios: " + miUserDAO.ReadAll().length);
            User nuevo = new User(0, "juan", "jl@gmail", "123", "user", null, null);
            System.out.println(nuevo.InitModel());
            nuevo.setCreate_at();
            //miUserDAO.InsertNewRegister(nuevo);
            //miUserDAO.UpdateRegister(nuevo, "nombre: juan, password: 123");
           //miUserDAO.EliminarRegistro("nombre: juan");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
