$compile = "javac -Xlint:all -d .\bin\ -cp '.\lib\mysql-connector-j-8.3.0\mysql-connector-j-8.3.0.jar' .\src\Conexion\*.java .\src\Conexion\Migration\*.java .\src\Conexion\Query\*.java .\src\Config\*.java .\src\Model\*.java .\src\Mundo\*.java .\src\Mundo\Cuentas\*.java .\src\Mundo\Users\*.java .\src\Samples\*.java .\src\Samples\Migration\*.java .\src\Utils\*.java "
$createJar = "jar -cfm javaMysqlORM.jar Manifesto.txt -C .\bin\ . -C .\extractionFiles\mysql-connector-j-8.3.0\ ."
$runCommand = "$compile" + " && " + "$createJar"
Invoke-Expression $runCommand 
