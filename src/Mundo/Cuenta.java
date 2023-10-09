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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'GetAllProperties'");
    }

    @Override
    public String InitModel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'InitModel'");
    }

}
