package Mundo.Cuentas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Model.ModelMethods;
import Mundo.TableProperties;
import Utils.ModelMetadata;

/**
 * clase que representa el modelo de cuentas
 */
public class Cuenta implements ModelMethods {
    /**
     * id de la cuenta
     */
    @TableProperties(miConstraint = "not null unique primary key auto_increment", miType = "int")
    private int id_pk;
    /**
     * nombre de la cuenta
     */
    @TableProperties(miConstraint = "not null", miType = "varchar(100)")
    private String nombre;
    /**
     * email de la cuenta
     */
    @TableProperties(miConstraint = "not null", miType = "varchar(100)")
    private String email;
    /**
     * foreign key de la cuenta al usuario
     */
    @TableProperties(miConstraint = "not null. foreign key(test_id_fk) references `test_user`(id_pk) on delete cascade on update cascade", miType = "int")
    private int user_id_fk;
    /**
     * fecha de creación de la cuenta
     */
    @TableProperties(miConstraint = "not null", miType = "datetime")
    private String create_at;
    /**
     * fecha de modificación de la cuenta
     */
    @TableProperties(miConstraint = "", miType = "datetime")
    private String update_at;
    
    /**
     * constructor para crear la cuenta desde una consulta
     * @param nId
     * @param nNombre
     * @param nEmail
     * @param nFk
     * @param nCreate_at
     * @param nUpdate_at
     */
    public Cuenta(int nId, String nNombre, String nEmail, int nFk, String nCreate_at, String nUpdate_at) {
        id_pk = nId;
        nombre = nNombre;
        email = nEmail;
        user_id_fk = nFk;
        create_at = nCreate_at;
        update_at = nUpdate_at;
    }
    
    /**
     * constructor para crear la cuenta para una consulta
     * @param nNombre
     * @param nEmail
     * @param nFk
     */
    public Cuenta(String nNombre, String nEmail, int nFk) {
        nombre = nNombre;
        email = nEmail;
        user_id_fk = nFk;
    }
    
    /**
     * constructor para utilizar la clase como modelo
     */
    public Cuenta(){};

    /**
     * @return the id_pk
     */
    public int getId_pk() {
        return id_pk;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the user_id_fk
     */
    public int getUser_id_fk() {
        return user_id_fk;
    }


    /**
     * @return the create_at
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
     * @param create_at the create_at to set
     */
    public void setCreate_at() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate = LocalDateTime.now();  
        create_at = dtf.format(miDate).toString();
    }

    /**
     * @return the update_at
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
     * @param update_at the update_at to set
     */
    public void setUpdate_at() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate = LocalDateTime.now();  
        create_at = dtf.format(miDate).toString();
    }


    /**
     * crear una cadena de texto con las propiedades del usuario
     * @return String con las propiedades del usuario
     */
    @Override
    public String GetAllProperties() {
        String all = "id_pk: " + this.getId_pk() + "\n";
        if(this.getNombre() != null && this.getNombre() != "" ) {
            all +="nombre: " + this.getNombre() + "\n";
        }
        if(this.getEmail() != null && this.getEmail().isEmpty() == false){
            all +="email: " + this.getEmail() + "\n";
        }
        if(this.getUser_id_fk() != 0){
            all +="user_id_fk: " + this.getUser_id_fk() + "\n";
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
     * crea una cadena de texto con los valores para inicializar el modelo
     * @return String con los valores para inicializar el modelo
     */
    @Override
    public String InitModel() {
        ModelMetadata metadata = new ModelMetadata("Mundo.Cuentas.Cuenta");
        return metadata.GetModelProperties();
    }

}
