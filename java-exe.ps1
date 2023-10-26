$Clases = " ./src/Conexion/Migration/MigrationDAO.java ./src/Conexion/Migration/MigrationExecution.java ./src/Conexion/Conector.java ./src/Conexion/Query/QueryDAO.java ./src/Conexion/Query/QueryExecution.java ./src/Config/DbConfig.java ./src/Model/ModelBuilderMethods.java ./src/Model/ModelMethods.java ./src/Mundo/Users/User.java ./src/Mundo/Users/UserBuilder.java ./src/Mundo/Cuentas/Cuenta.java ./src/Mundo/Cuentas/CuentaBuilder.java ./src/Utils/MigrationBuilder.java ./src/Utils/MigrationBuilder.java ./src/Utils/QueryBuilder.java ./src/Utils/QueryUtils.java"

$Compilation = "javac -d ./bin/" + "$Clases";
$javaCommand = "java -cp " + '"./bin;d:/Default/Proyectos/dependencias/mysql-connector-j-8.1.0/mysql-connector-j-8.1.0.jar" .\src\App.java';
$runCommand = "$Compilation" + " & " + "$javaCommand"
Invoke-Expression $javaCommand
