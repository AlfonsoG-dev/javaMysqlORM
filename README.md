
# Project in java with MYSQL;
>- An ORM like app in java.
>- creates SQL sentences for MYSQL database using class object as models.
>>- i try to replicate an ORM functionality in java for MYSQL databases.

# Dependencies

>- [Java JDK 17.0.8](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
>- [mysql connector 8.1.0](https://dev.mysql.com/downloads/connector/j/)
>- [mysql_query_examples](https://www.w3schools.com/mysql/)

# Features
- [x] dynamic query creation using class objects as table models.
- [x] normal CRUD operations and `INNER JOIN` as well.
- [x] dynamic Migration using Annotations fields of the table model.
- [x] accepts table relationships using the table Model Annotations.
- [x] method to execute INSERT, UPDATE, DELETE operations using SQL sentences as parameters.
- [x] method to SELECT operations using SQL sentences as parameters.
- [x] `CREATE VIEW ` statement.
- [x] `INSERT INTO ... SELECT` statement.

## TODO's
- [ ] creation of table space.

-----

# Normal Use

[normal_use_samples](./src/Samples/Normal/QuerySamples.java)

# Migration Use

[migration_usage](./src/Samples/Migration/MigrationSamples.java)

# Connection use
[connection_usage](./src/Samples/MainApp.java)

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
