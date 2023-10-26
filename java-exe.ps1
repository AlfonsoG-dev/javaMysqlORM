$Clases = " ./src/*.java"
$Compilation = "javac -d ./bin/" + "$Clases";
$javaCommand = "java -cp " + '"./bin;d:/Default/Proyectos/dependencias/mysql-connector-j-8.1.0/mysql-connector-j-8.1.0.jar" .\src\App.java';
$runCommand = "$Compilation" + " & " + "$javaCommand"
Invoke-Expression $javaCommand
