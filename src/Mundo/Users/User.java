package Mundo.Users;

import java.time.format.DateTimeFormatter;

import Model.ModelMethods;
import Mundo.TableProperties;
import Utils.ModelMetadata;

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
    @TableProperties(miConstraint = "not null primary key auto_increment", miType = "int")
    private int id_pk;   
    /**
     * nombre del usuario
     * */
    @TableProperties(miConstraint = "not null unique", miType = "varchar(100)")
    private String nombre;
    /**
     * email del usuario
     * */
    @TableProperties(miConstraint = "not null unique", miType = "varchar(100)")
    private String email;
    /**
     * password del usuario
     * */
    @TableProperties(miConstraint = "not null", miType = "varchar(100)")
    private String password;
    /**
     * rol del usuario
     * puede ser null
     * */
    @TableProperties(miConstraint = "", miType = "varchar(50)")
    private String rol;
    /**
     * fecha de creación del usuario
     * */
    @TableProperties(miConstraint = "not null", miType = "datetime")
    private String create_at;
    /**
     * fecha de modificación del usuario
     * pude ser null
     * */
    @TableProperties(miConstraint = "", miType = "datetime")
    private String update_at;

    //constructor

    /**
     * constructor para crear el usuario desde una consulta
     * @param nId_pk
     * @param nNombre
     * @param nEmail
     * @param nPassword
     * @param nRol
     * @param nCreate_at
     * @param nUpdate_at
     */
    public User(int nId_pk, String nNombre, String nEmail, String nPassword, String nRol, String nCreate_at, String nUpdate_at) {
        id_pk     = nId_pk;
        nombre    = nNombre;
        email     = nEmail;
        password  = nPassword;
        rol = nRol;
        create_at = nCreate_at;
        update_at = nUpdate_at;
    }
    /**
     * constructor crear el usuario para una consulta
     * @param nNombre
     * @param nEmail
     * @param nPassword
     * @param nRol
     */
    public User(String nNombre, String nEmail, String nPassword, String nRol) {
        nombre   = nNombre;
        email    = nEmail;
        password = nPassword;
        rol      = nRol;
    }
    
    /**
     * constructor solo para utilizar la clase como modelo
     */
    public User() { 
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
            LocalDateTime miDate  = LocalDateTime.parse(create_at, dtf);  
            date                  = dtf.format(miDate).toString();
        }
        return date;    
    }

    /**
     * inicializa la fecha de creación del usuario
     * */
    public void setCreate_at() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate  = LocalDateTime.now();  
        create_at             = dtf.format(miDate).toString();
    }

    /**
     * da formato a la fecha de modificación del usuario
     * @return fecha de modificación del usuario
    */
    public String getUpdate_at() {
        String date = null;
        if(update_at != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
            LocalDateTime miDate  = LocalDateTime.parse(update_at, dtf);  
            date                  = dtf.format(miDate).toString();
        }
        return date;    
    }

    /**
     * inicializa la fecha de modificación del usuario
     * */
    public void setUpdate_at() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate  = LocalDateTime.now();  
        update_at             = dtf.format(miDate).toString();
    }

    /**
     * método que retorna todas las propiedades del usuario en orden
     * @return String con las propiedades del usuario ordenadas
     * */
    @Override
    public String getAllProperties() {
        StringBuffer all = new StringBuffer();
        if(this.getId_pk() > 0) {
            all.append("id_pk: " + this.getId_pk() + "\n");
        }
        if(this.getNombre() != null && this.getNombre() != "" ) {
            all.append("nombre: " + this.getNombre() + "\n");
        }
        if(this.getEmail() != null && this.getEmail().isEmpty() == false){
            all.append("email: " + this.getEmail() + "\n");
        }
        if(this.getPassword() != null && this.getPassword().isEmpty() == false){
            all.append("password: " + this.getPassword() + "\n");
        }
        if(this.getRol() != null && this.getRol().isEmpty() == false){
            all.append( "Rol: " + this.getRol() + "\n");
        }
        if(this.getCreate_at() != null) {
            all.append( "create_at: " + this.getCreate_at() + "\n");
        }
        if(this.getUpdate_at() != null) {
            all.append( "update_at: " + this.getUpdate_at());
        }
        return all.toString();
    }
    /**
     * método que inicializa las propiedades del modelo de base de datos
     * @return string con las propiedades del modelo de base de datos
     */
    @Override
    public String initModel() {
        ModelMetadata metadata = new ModelMetadata("Mundo.Users.User");
        return metadata.getModelProperties();
    }
}
