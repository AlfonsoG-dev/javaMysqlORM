package Config;

/**
 * record con los métodos para crear la configuración de la conexión a base de datos
 */
public record DbConfig(String database, String hostname, String port, String username, String password) {

    /**
     * crear la url de conexión para mysql
     * @return la url de la conexión
     */
    public String getMysqlUrl() {
        String url = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database;
        return url;
    }
}
