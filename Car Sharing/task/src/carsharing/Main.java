package carsharing;



public class Main {


    public static void main(String[] args) {

        String databaseName = "carsharing";

        if (args.length == 2) {
            databaseName = args[1];
        }

        final String JDBC_DRIVER = "org.h2.Driver";
        final String DB_URL = "jdbc:h2:\\C:\\Users\\marang3\\IdeaProjects\\Car Sharing\\Car Sharing\\task\\src\\carsharing\\db\\" + databaseName;

        Engine e = new Engine();
        e.run(JDBC_DRIVER, DB_URL);
    }

}

