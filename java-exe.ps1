$Clases = " ./src/*.java ./src/Conexion/*.java ./src/Conexion/Migration/*.java ./src/Conexion/Query/*.java ./src/Config/*.java ./src/Model/*.java ./src/Mundo/*.java ./src/Mundo/Users/*.java ./src/Mundo/Cuentas/*.java ./src/Utils/*.java"
$Compilation = "javac -d ./bin/" + "$Clases";
$javaCommand = "java -cp " + '"./bin;./lib/mysql-connector-j-8.1.0/mysql-connector-j-8.1.0.jar" .\src\App.java';
$runCommand = "$Compilation" + " & " + "$javaCommand"
Invoke-Expression $runCommand
