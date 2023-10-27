#list of the clases of the proyect
$Clases = " ./src/*.java ./src/Conexion/*.java ./src/Conexion/Migration/*.java ./src/Conexion/Query/*.java ./src/Config/*.java ./src/Model/*.java ./src/Mundo/*.java ./src/Mundo/Users/*.java ./src/Mundo/Cuentas/*.java ./src/Utils/*.java"

#create .class file in bin for the jvm
$Compilation = "javac -d ./bin/" + "$Clases";
#run the program 
$javaCommand = "java -cp " + '"./bin;./lib/mysql-connector-j-8.1.0/mysql-connector-j-8.1.0.jar" .\src\App.java';
#create jar file from bin and lib if lib have dependencies
$CreateJarFile = "jar -cf test.jar -C .\bin\ . -C .\lib\ .";

$runCommand = "$Compilation" + " & " + "$javaCommand"

Invoke-Expression $runCommand
