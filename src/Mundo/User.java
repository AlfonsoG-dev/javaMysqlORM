package Mundo;

import java.time.format.DateTimeFormatter;

import Model.ModelMethods;
import java.time.LocalDateTime;

/**
 * clase representante del usuario
 * implementa el método que genera las propiedades del objeto
 * */
public class User implements ModelMethods {

    //atributos

    /**
     * id del usuario
     * solo posee método get
    * */
    private int id_pk;   
    /**
     * nombre del usuario
     * */
    private String nombre;
    /**
     * email del usuario
     * */
    private String email;
    /**
     * password del usuario
     * */
    private String password;
    /**
     * rol del usuario
     * puede ser null
     * */
    private String rol;
    /**
     * fecha de creación del usuario
     * */
    private String create_at;
    /**
     * fecha de modificación del usuario
     * pude ser null
     * */
    private String update_at;

    //constructor

    /**
     * constructor del usuario
     * @param nId_pk
     * @param nNombre
     * @param nEmail
     * @param nPassword
     * @param nRol
     * @param nCreate_at
     * @param nUpdate_at
     */
    public User(int nId_pk, String nNombre, String nEmail, String nPassword, String nRol, String nCreate_at, String nUpdate_at) {
        id_pk = nId_pk;
        nombre = nNombre;
        email = nEmail;
        password = nPassword;
        rol = nRol;
        create_at = nCreate_at;
        update_at = nUpdate_at;
    }

    //métodos

    public int getId_pk() {
        return id_pk;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nNombre) {
        nombre = nNombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String nemail) {
        email = nemail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String npassword) {
        password = npassword;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String nrol) {
        rol = nrol;
    }

    /**
     * da formato a la fecha de creación del usuario
     * @return fecha de creación del usuario
    */
    public String getCreate_at() {
        String date = null;
        if(create_at != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
            LocalDateTime miDate = LocalDateTime.parse(create_at, dtf);  
            date = dtf.format(miDate).toString();
        }
        return date;    
    }

    /**
     * inicializa la fecha de creación del usuario
     * */
    public void setCreate_at() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate = LocalDateTime.now();  
        create_at = dtf.format(miDate).toString();
    }

    /**
     * da formato a la fecha de modificación del usuario
     * @return fecha de modificación del usuario
    */
    public String getUpdate_at() {
        String date = null;
        if(update_at != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
            LocalDateTime miDate = LocalDateTime.parse(update_at, dtf);  
            date = dtf.format(miDate).toString();
        }
        return date;    
    }

    /**
     * inicializa la fecha de modificación del usuario
     * */
    public void setUpdate_at() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate = LocalDateTime.now();  
        update_at = dtf.format(miDate).toString();
    }

    /**
     * método que retorna todas las propiedades del usuario en orden
     * @return String con las propiedades del usuario ordenadas
     * */
    @Override
    public String GetAllProperties() {
        String all = "id_pk: " + this.getId_pk() + "\n";
        if(this.getNombre() != null && this.getNombre() != "" ) {
            all +="nombre: " + this.getNombre() + "\n";
        }
        if(this.getEmail() != null && this.getEmail().isEmpty() == false){
            all +="email: " + this.getEmail() + "\n";
        }
        if(this.getPassword() != null && this.getPassword().isEmpty() == false){
            all +="password: " + this.getPassword() + "\n";
        }
        if(this.getRol() != null && this.getRol().isEmpty() == false){
            all += "Rol: " + this.getRol() + "\n";
        }
        if(this.getCreate_at() != null) {
            all += "create_at: " + this.getCreate_at() + "\n";
        }
        if(this.getUpdate_at() != null) {
            all += "update_at: " + this.getUpdate_at();
        }
        return all;
    }
    /**
     * método que inicializa las propiedades del modelo de base de datos
     * @return HashMap con las propiedades del modelo de base de datos
     */
    @Override
    public String InitModel() {
        String build = "";
        String[] columns = {
            "id_pk",
            "nombre",
            "email",
            "password",
            "rol",
            "create_at",
            "update_at"
        };
        String[] values = {
            "int not null unique primary key autoincrement",
            "varchar(100) not null unique",
            "varchar(100) not null unique",
            "varchar(100) not null",
            "varchar(50)",
            "datetime not null",
            "datetime"
        };
        for(int i = 0; i < columns.length; i++) {
            build += columns[i] + ": " + values[i] + "\n";
        }
        return build;
    }
}
