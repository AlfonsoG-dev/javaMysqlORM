# Java MYSQL ORM 
>- An ORM like app in java.
>- creates SQL sentences for MYSQL databases using java classes as models.
>>- i try to replicate an ORM functionality in java for MYSQL.

# Dependencies

>- [Java JDK 17.0.8](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
>- [mysql connector 8.1.0](https://dev.mysql.com/downloads/connector/j/)
>- [javabuild tool](https://github.com/AlfonsoG-dev/javaBuild)

# References

>- [mysql_query_examples](https://www.w3schools.com/mysql/)
>- [JDBC samples](https://www.javatpoint.com/PreparedStatement-interface)

# Features
- [x] dynamic query creation using class objects as table models.
- [x] temporary tables using class objects as table models.
- [x] normal CRUD operations and `INNER JOIN` as well.
- [x] dynamic Migration using Annotations fields of the table model.
- [x] accepts table relationships using the table Model Annotations.
- [x] method to execute INSERT, UPDATE, DELETE operations using SQL sentences as parameters.
- [x] method to SELECT operations using SQL sentences as parameters.
- [x] `CREATE VIEW ` statement.
- [x] `INSERT INTO ... SELECT` statement.
- [x] prepared and normal statements

-----

# Normal Use

[normal_samples](./src/Samples/Normal/QuerySamples.java)

# Migration Use

[migration_samples](./src/Samples/Migration/MigrationSamples.java)

# Connection use

[connection_samples](./src/Samples/MainApp.java)

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
private int user_id_fk;

```
---------

# Compile And Execute
This project use [javaBuild_tool](https://github.com/AlfonsoG-dev/javaBuild) to build the project.

>- if you are not using vscode and need to compile the project with the `javac` CLI tool.
>- I include a `java-exe.ps1` shell script for powershell.

## PowerShell script for compile and execute.

```shell
$clases = "all the clases in the program"
$compilation = "javac -d ./bin/" + "$clases";
$javaCommand = "java -cp " + '"./bin;path to a custom jar file" .\src\MainClass.java';
$createJar = "jar -cfm jarName.jar Manifesto.txt -C .\bin\ . -C .\lib extracted file path\ ."
$runCommand = "$compilation" + " && " + "&createJar" + " && " + "$javaCommand"
Invoke-Expression $runCommand
```
>- if you need to add a custom jar file from another project
```shell
$compilation = "javac -d ./bin/ -cp " + '" path to a custom jar file"' + "$clases";
```
>- if your need to create a jar file
>>- add in the root of the project: `Manifesto.txt`
```txt
Manifest-Version: 1.0
Main-Class: MyMainClassName
```
>- remember if you want the lib dependency as part of the jar build you must add it.
>>- create a directory name: `extractionFile`
>>- move the lib jar file dependency only to `extractionFile` directory.
>>- in the `extractionFile` directory extract the jar file dependency.
>>- finally for the ps1 build script add the following sentence.
```
$createJar = "jar -cfm jarName.jar Manifesto.txt -C .\bin\ . -C .\extractionFile\lib extracted file path\ ."
```

--------

# Disclaimer
>- this project is just for educational purposes.
>- security issues are not taken into account.
>- is not intended to make a full ORM program.
