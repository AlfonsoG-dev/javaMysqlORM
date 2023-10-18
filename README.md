# Proyecto en Java con mysql

>- proyecto creado en vscode.
>- proyecto desarrollado en java para realizar consultas a una base de datos en mysql.

## Dependencias

>- [Java JDK 17.0.8](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
>- [mysql connector 8.1.0](https://dev.mysql.com/downloads/connector/j/)

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

>- The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

## Use
```java
public static void main(String[] args) {
    try {
        // clase que posee la conexión a la base de datos
        DbConfig miConfig = new DbConfig("consulta", "localhost", "3306", "test_user", "5x5W12");

        // Clase DAO para la Query según el tipo de dato generico asignado
        QueryDAO<User> miUserDAO = new QueryDAO<User>("users", miConfig);

        // verificar si existen datos y seleccionar 1 para comprobar el nombre
        System.out.println(miUserDAO.ReadAll(builder).get(1).getNombre());

        // Clase que representa el tipo de dato para la clase DAO
        User nuevo = new User(0, "juan", "jl@gmail", "123", "user", null, null);

        //clase Builder del Usuario 
        UserBuilder builder = new UserBuilder();

        //método para registrar datos de la tabla
        nuevo.setCreate_at(); // asigna la fecha actual
        miUserDAO.InsertNewRegister(nuevo, "nombre: " + nuevo.getNombre(), builder);

        // método para actualizar datos de la tabla
        nuevo.setUpdate_at(); // asigna la fecha actual
        miUserDAO.UpdateRegister(nuevo, "nombre: juan, password: 123", builder);

        // método para eliminar los datos de una tabla
        miUserDAO.EliminarRegistro("nombre: juan", builder);
    } catch (Exception e) {
        System.out.println(e);
    }
}
```

## Migration Use

```java
public static void main(String[] args) {
    try {

        //configuración de la conexión a la base de datos 
        DbConfig miConfig = new DbConfig("consulta", "localhost", "3306", "test_user", "5x5W12");

        //clase para realizar la migración con el tipo genérico dado
        MigrationDAO<Cuenta> miCuentaDAO = new MigrationDAO<>("cuentas", miConfig);

        //clase que representa el modelo con el que se realiza la migración
        Cuenta mia = new Cuenta(0, null, null, 0, null, null);

        //crear la base de datos
        miCuentaDAO.CreateDataBase("prueba_db");

        //selecciona la nueva base de datos
        miCuentaDAO.SelecDatabase("prueba_db");

        //crea la tabla según el modelo
        miCuentaDAO.CreateTable(mia);

        // agrega columnas a la tabla
        miCuentaDAO.AddColumn(mia);

        //elimina columnas de la tabla
        miCuentaDAO.DeleteColumn(mia);

        //cambia el nombre de las columnas de la tabla
        miCuentaDAO.RenameColumn(mia);

        //cambia el tipo de dato de las columnas de la tabla
        miCuentaDAO.ChangeType(mia);

        //agrega constraint a las columnas de la tabla
        miCuentaDAO.AddConstraint(mia);

        //elimina el constraint de las columnas de la tabla
        miCuentaDAO.DeleteConstraint(mia);
    } catch (Exception e) {
        System.out.println(e);
    }
}
```
