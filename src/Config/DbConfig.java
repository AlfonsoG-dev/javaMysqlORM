package Config;

/**
 * record class with database connection data
 */
public record DbConfig(String database, String hostname, String port, String username, String password) {

    /**
     * url for mysql connection
     */
    public String getMysqlUrl() {
        String url = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database;
        return url;
    }
}
