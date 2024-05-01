# Java MYSQL ORMor fk 
>- An ORM like app in java.
>- creates SQL sentences for MYSQL databases using java classes as models.
>>- i try to replicate an ORM functionality in java for MYSQL.

# Dependencies

>- [Java JDK 17.0.8](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
>- [mysql connector 8.3.0](https://dev.mysql.com/downloads/connector/j/)
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

[connection_samples](./src/Samples/ConnectionSamples.java)

## Model Creation
>- A Class can be a model if implements `ModelMethods` interface
>>- Model Methods have 2 methods and each one have their own purpose.
```java
public class Model implements ModelMethods {
    /**
    * primary key for model always need to be id_pk
    * if you put another you have to add id_pk to the end or at the start 
    * ejm: mode_id_pk
    */
    @TableProperties(
        miConstraint = "not null primary key auto_increment",
        miType = "int"
    )
    private int id_pk;   
    /**
    * a class another class member
     */
    @TableProperties(
        miConstraint = "not null",
        miType = "varchar(100)"
    )
    private String description;
    public Model() {
    }

    // getter and setter

    /**
    * method to implement from ModelMethods.
    * is used to get the model data in *column: value* format
    */
    @Override
    public String getAllProperties() {
        StringBuffer all = new StringBuffer();
        if(this.getId_pk() > 0) {
            all.append("id_pk: " + this.getId_pk() + "\n");
        }
        if(this.getName() != null) {
            all.append( "name: " + this.getName() + "\n");
        }
        if(this.getDescription() != null) {
            all.append( "decription: " + this.getDescription());
        }
        return all.toString();
    }
}
```
>- if the model contains an FK column you must declare the reference inside de Annotation and separate the constraint with `.` before the FK reference
>>- because of the nature of the migration operation.

```java
/**
* foreign key de la cuenta al usuario
* you have to add the id_fk to the end or start
* ejm: model_id_fk | id_fk_model
*/
@TableProperties(
    miConstraint = "not null. foreign key('name of the fk') references `name of the table`(name of the pk) on delete cascade on update cascade",
    miType = "int"
)
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
Class-Path: lib\lib_dependency\dependency.jar
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
