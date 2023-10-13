import Conexion.*;
import Mundo.User;
import Utils.UserBuilder;

public class App {
    public static void main(String[] args) {
        try {
            QueryDAO<User> miUserDAO = new QueryDAO<User>();
            User nuevo = new User(0, "juan", "jl@gmail", "123", "user", null, null);
            UserBuilder builder = new UserBuilder();
            nuevo.setCreate_at();
            System.out.println(miUserDAO.ReadAll(builder).get(1).getNombre());
            //miUserDAO.InsertNewRegister(nuevo, "nombre: " + nuevo.getNombre(), builder);
            //nuevo.setUpdate_at();
            //miUserDAO.UpdateRegister(nuevo, "nombre: juan, password: 123", builder);
            //miUserDAO.EliminarRegistro("nombre: juan", builder);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
