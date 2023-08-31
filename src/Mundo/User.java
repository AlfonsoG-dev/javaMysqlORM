package Mundo;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;

public class User {
    //atributos
    private int id_pk;   
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private String create_at;
    private String update_at;

    //constructor
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

    public String getCreate_at() {
        assert create_at == null : "deberia estar inicializada";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate = LocalDateTime.parse(create_at, dtf);  
        return miDate.toString();    
    }

    public void setCreate_at(String ncreate_at) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate = LocalDateTime.now();  
        create_at = dtf.format(miDate);
    }

    public String getUpdate_at() {
        String date = null;
        if(update_at != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
            LocalDateTime miDate = LocalDateTime.parse(update_at, dtf);  
            date = dtf.format(miDate);

        }
        return date;    
    }

    public void setUpdate_at(String nupdate_at) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate = LocalDateTime.now();  
        update_at = dtf.format(miDate);
    }

    public String GetAllProperties() {
        String all =
            "id: " +  this.getId_pk() + "\n" +
            "nombre: " + this.getNombre() + "\n" +
            "email: " + this.getEmail() + "\n" +
            "password: " + this.getPassword() + "\n" +
            "Rol: " + this.getRol() + "\n";

        if(this.getCreate_at() != null) {
            all += "create_at: " + this.getCreate_at() + "\n";
        }
        if(this.getUpdate_at() != null) {
            all += "update_at: " + this.getUpdate_at();
        }
        return all;
    }
}
