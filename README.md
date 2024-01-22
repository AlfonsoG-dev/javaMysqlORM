# Project in java with MYSQL;

>- An ORM like app in java.
>- creates SQL sentences for MYSQL database using class object as models.
>>- i try to replicate an ORM functionality in java for MYSQL databases.

# Dependencies

>- [Java JDK 17.0.8](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
>- [mysql connector 8.1.0](https://dev.mysql.com/downloads/connector/j/)
>- [mysql_query_examples](https://www.w3schools.com/mysql/)

# Features
- [x] dynamic query creation base on class object as models.
- [x] normal CRUD operations and InnerJoin as well.
- [x] dynamic Migration base on Annotations fields declared inside the model.
- [x] accepts table relationships using the class Model Annotations.

## TODO's
- [ ] view statements.
- [ ] use min and max in statements.
- [ ] creation of table space.
- [ ] add method to execute the given SQL query.
- [ ] add insert into select DAO operation.

-----

# Normal Use
```java
public static void main(String[] args) {
    try {
        // clase que posee la conexión a la base de datos
        DbConfig miConfig = new DbConfig("consulta", "localhost", "3306", "test_user", "5x5W12");

        // enable transaction usage when auto commit is false
        Connection transactionCursor = new Conector(miConfig).conectarMySQL();
        transactionCursor.setAutoCommit(false);

        // Clase DAO para la Query según el tipo de dato generico asignado
        QueryDAO<User> miUserDAO = new QueryDAO<User>("user", miConfig, transactionCursor);

        // verificar si existen datos y seleccionar 1 para comprobar el nombre
        System.out.println(miUserDAO.ReadAll(builder).get(1).getNombre());

        // inner join of 2 tables
        User referenceModel = new User("test", "test@gmail", "123", "test");
        Cuenta localModel = new Cuenta("test", "test@test", loggedUser, "123");
        String refModelTable = "user";

        // QueryDAO for localModel table
        QueryDAO<Cuenta> cuentaDAO = new QueryDAO<Cuenta>("cuenta", miConfig, transactionCursor);
        String rest = cuentaDAO.InnerJoin(localModel, referenceModel, refModelTable);
        System.out.println(rest);


        // Clase que representa el tipo de dato para la clase DAO
        User nuevo = new User("juan", "jl@gmail", "123", "user");

        //clase Builder del Usuario 
        UserBuilder builder = new UserBuilder();

        //método para registrar datos de la tabla
        nuevo.setCreate_at(); // asigna la fecha actual
        miUserDAO.InsertNewRegister(nuevo, "nombre: " + nuevo.getNombre(), "and", builder);

        // método para actualizar datos de la tabla
        nuevo.setUpdate_at(); // asigna la fecha actual
        miUserDAO.UpdateRegister(nuevo, "nombre: juan, password: 123", "or", builder);

        // método para eliminar los datos de una tabla
        miUserDAO.EliminarRegistro("nombre: juan", "and", builder);

        // if want to cancel the database in this SavePoint
        transactionCursor.rollback()
        // else if whant to commit the changes
        transactionCursor.commit();
    } catch (Exception e) {
        System.out.println(e);
    }
}
```

# Migration Use

```java
public static void main(String[] args) {
    try {

        //configuración de la conexión a la base de datos 
        DbConfig miConfig = new DbConfig("consulta", "localhost", "3306", "test_user", "5x5W12");

        //clase para realizar la migración con el tipo genérico dado
        MigrationDAO<Cuenta> miCuentaDAO = new MigrationDAO<>("cuentas", miConfig);

        //clase que representa el modelo con el que se realiza la migración
        Cuenta mia = new Cuenta();

        //crear la base de datos
        miCuentaDAO.CreateDataBase("prueba_db");

        //selecciona la nueva base de datos
        miCuentaDAO.SelecDatabase("prueba_db");

        //crea la tabla según el modelo
        miCuentaDAO.CreateTable(mia);

        // agrega columnas a la tabla
        boolean includeFKPK = false | true;
        miCuentaDAO.AddColumn(mia, ref_model, "ref_table", includeFKPK);

        //elimina columnas de la tabla
        miCuentaDAO.DeleteColumn(mia, includeFKPK);

        //cambia el nombre de las columnas de la tabla
        miCuentaDAO.RenameColumn(mia);

        //cambia el tipo de dato de las columnas de la tabla
        miCuentaDAO.ChangeType(mia, includeFKPK);
    } catch (Exception e) {
        e.prinStackTrace();
    }
}
```
## Model Creation
>- A Class can be a model if implements `ModelMethods` interface
>>- Model Methods have 2 methods and each one have their own purpose.
```java
/**
* this method is to obtain the value of the model
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
* this method is to obtain the value of the model properties
*/
@Override
public String InitModel() {
    ModelMetadata metadata = new ModelMetadata("Mundo.Users.User");
    return metadata.GetModelProperties();
}
```
>- in each one of the members of that model is necessary to declare an Annotation
>>- the Annotation is to declara a constraint and type for the table column in the database.
```java
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
```
>- if the model contains an FK column you must declare the reference inside de Annotation and separate the constraint with `.` before the FK reference
>>- because of the nature of the migration operation.
```java
/**
* foreign key de la cuenta al usuario
*/
@TableProperties(miConstraint = "not null. foreign key('name of the fk') references `name of the table`(name of the pk) on delete cascade on update cascade", miType = "int")

```
---------

# Compile And Execute
>- if you are not using vscode and need to compile the project with the `javac` CLI tool.
>- I include a `java-exe.ps1` shell script for powersehll.

## PowerShell script for compile and execute.

```shell
$Clases = "all the clases in the program"
$Compilation = "javac -d ./bin/" + "$Clases";
$javaCommand = "java -cp " + '"./bin;path to a custom jar file" .\src\App.java';
$runCommand = "$Compilation" + " & " + "$javaCommand"
$CreateJarFile = "md test-jar_extraction && Copy-Item -Path .\lib\mysql-connector-j-8.1.0\mysql-connector-j-8.1.0.jar -Destination .\test-jar_extraction\ && cd .\test-jar_extraction\ && jar -xf .\mysql-connector-j-8.1.0.jar && rm -r .\mysql-connector-j-8.1.0.jar && cd .. && jar -cfm test.jar Manifesto.txt -C bin . -C test-jar_extraction . && rm -r ./test-jar_extraction/"
Invoke-Expression $runCommand
```
>- if you need to add a custom jar file from another project
```shell
$Compilation = "javac -d ./bin/ -cp " + '" path to a custom jar file"' + "$Clases";
```
>- if your need to create a jar file
>>- add in the root of the project: `Manifesto.txt`
```txt
Manifest-Version: 1.0
Main-Class: MyMainClassName
```
>- in the powershell script you need to change the `Invoke-Expression $runCommand` to `Invoke-Expression $CreateJarFile`
>>- the name of the created jar file if: `test.jar`
>>- Create Jar File have a bunch of commands to create the jar and add the content of the MYSQL connector 
>>- i cannot do the jar file creation in other way.

--------

# Disclaimer
>- this project is just for educational purposes.
>- security issues are not taken into account.
>- is not intended to make a full ORM program.
