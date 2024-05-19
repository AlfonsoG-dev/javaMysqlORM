$srcClases = ".\src\Conexion\*.java .\src\Conexion\Migration\*.java .\src\Conexion\Query\*.java .\src\Config\*.java .\src\Model\*.java .\src\Mundo\*.java .\src\Mundo\Cuentas\*.java .\src\Mundo\Samples\*.java .\src\Mundo\Samples\Migration\*.java .\src\Mundo\Samples\Models\*.java .\src\Mundo\Samples\Normal\*.java .\src\Mundo\Users\*.java .\src\Utils\Builder\*.java .\src\Utils\Formats\*.java .\src\Utils\Model\*.java .\src\Utils\Query\*.java "
$libFiles = ".\lib\mysql-connector-j-8.3.0\mysql-connector-j-8.3.0.jar;"
$compile = "javac -Werror -Xlint:all -d .\bin\ -cp '$libFiles' $srcClases"
$createJar = "jar -cfm javaMysqlORM.jar Manifesto.txt -C .\bin\ . -C .\extractionFiles\mysql-connector-j-8.3.0\ ."
$runCommand = "$compile" + " && " + "$createJar" 
Invoke-Expression $runCommand 
