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

## Uso
```java
public static void main(String[] args) {
    try {
        UserDAO miUserDAO = new UserDAO();
        System.out.println("usuarios: " + miUserDAO.ReadAll().length);
        //modelo de la tabla
        User nuevo = new User(0, "juan", "jl@gmail", "123", "user", null, null);
        // se asigna la fecha actual
        nuevo.setCreate_at();
        
        //ingreso de un nuevo registro
        miUserDAO.InsertNewRegister(nuevo);
        
        //actualizar un registro
        miUserDAO.UpdateRegister(nuevo, "nombre: juan, password: 123");

        //eliminar un registro
        miUserDAO.EliminarRegistro("nombre: juan");
    } catch (Exception e) {
        System.out.println(e);
    }
}
```
