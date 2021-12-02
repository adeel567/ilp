package uk.ac.ed.inf;

/**
 * Class that holds the current configuration variables to be used in the program
 */
public class Config {
    private static Config instance = null;

    //set defaults if left unchanged.
    private String serverPort = "9898";
    private String serverHost = "localhost";
    private String dbHost = "localhost";
    private String dbPort = "1527";

    /**
     * Uses singleton pattern, so private constructor.
     */
    private Config() {
    }

    /**
     * Get the instance of Config.
     * Will be created if it doesn't exist.
     * @return Config object.
     */
    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerPort() {
        return serverPort;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }
}
