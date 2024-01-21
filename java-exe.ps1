$compile = "javac -d .\bin\ -cp '.\lib\mysql-connector-j-8.3.0\mysql-connector-j-8.3.0.jar' .\src\Conexion\*.java .\src\Conexion\Migration\*.java .\src\Conexion\Query\*.java .\src\Config\*.java .\src\Model\*.java .\src\Mundo\*.java .\src\Mundo\Cuentas\*.java .\src\Mundo\Users\*.java .\src\Utils\*.java "
$createJar = "jar -cf java-mysql-eje.jar -C .\bin\ . -C .\extractionFiles\mysql-connector-j-8.3.0\ ."
$javaCommand = "java -jar .jar"
$runCommand = "$compile" + " && " + "$createJar" + " && " +"$javaCommand"
Invoke-Expression $runCommand