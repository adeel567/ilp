package uk.ac.ed.inf;

public class App {

    public static void main(String[] args) throws Exception{
        int day = Integer.parseInt(args[0]);
        int month = Integer.parseInt(args[1]);
        int year = Integer.parseInt(args[2]);
        String web_port = args[3];
        String db_port = args[4];

        Config config = Config.getInstance();
        config.setDbHost("localhost");
        config.setDbPort(db_port);
        config.setServerHost("localhost");
        config.setServerPort(web_port);




    }
}
