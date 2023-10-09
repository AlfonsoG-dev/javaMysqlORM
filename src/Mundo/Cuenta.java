package Mundo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Model.ModelMethods;

public class Cuenta implements ModelMethods {
    private int id_pk;
    private String nombre;
    private String email;
    private int user_id_fk;
    private String create_at;
    private String update_at;
    
    public Cuenta(int nId, String nNombre, String nEmail, int nFk, String nCreate_at, String nUpdate_at) {
        id_pk = nId;
        nombre = nNombre;
        email = nEmail;
        user_id_fk = nFk;
        create_at = nCreate_at;
        update_at = nUpdate_at;
    }
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
    public void setCreate_at(String create_at) {
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
    public void setUpdate_at(String update_at) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate = LocalDateTime.now();  
        create_at = dtf.format(miDate).toString();
    }


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

    @Override
    public String InitModel() {
        String build = "";
        String[] columns = {
            "id_pk",
            "nombre",
            "email",
            "user_id_fk",
            "create_at",
            "update_at"
        };
        String[] values = {
            "int not null unique primary key auto_increment",
            "varchar(100) not null",
            "varchar(100) not null",
            "int not null, constraint user_id_fk foreign key (user_id_fk) references users(id_pk)",
            "datetime not null",
            "datetime"
        };
        for(int i = 0; i < columns.length; i++) {
            build += columns[i] + ": " + values[i] + "\n";
        }
        return build;
    }

}
