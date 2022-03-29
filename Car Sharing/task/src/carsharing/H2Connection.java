package carsharing;

import java.sql.*;

public class H2Connection {

    private static final String jdbc_driver = "org.h2.Driver";
    private final static String dbLocation = "\\C:\\Users\\marang3\\IdeaProjects\\Car Sharing\\Car Sharing\\task\\src\\carsharing\\db\\";
    private final static String dbUrl = "jdbc:h2:" + dbLocation + "%s";
    private final static String companyTableName = "COMPANY";
    private final static String carTableName = "CAR";
    private final static String customerTableName = "CUSTOMER";
    private static String dbname = "";

    final private static String CREATE_COMPANY =
            "CREATE TABLE IF NOT EXISTS %s " +
                    "(ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                    " NAME VARCHAR(255) UNIQUE NOT NULL, " +
                    " UNIQUE (ID)" +
                    ")";

    final private static String DROP_IF_EXISTS =
            "DROP TABLE IF EXISTS %s;";

    final private static String ALTER_COMPANY =
            "ALTER TABLE COMPANY ALTER COLUMN ID RESTART WITH 1;";

    final private static String INSERT_COMPANY =
            "INSERT INTO COMPANY (name) VALUES (?);";

    final private static String CREATE_CAR = "CREATE TABLE IF NOT EXISTS %s " +
            "(ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
            " NAME VARCHAR(255) UNIQUE NOT NULL, " +
            " COMPANY_ID INT NOT NULL," +
            " CONSTRAINT UC_car UNIQUE (ID)," +
            " CONSTRAINT fk_company FOREIGN KEY (COMPANY_ID)" +
            " REFERENCES COMPANY(ID)" +
            ")";

    final private static String INSERT_CAR = "INSERT INTO CAR (name, company_id) " +
            "VALUES (?,?)";

    final private static String CREATE_CUSTOMER = "CREATE TABLE IF NOT EXISTS %s " +
            "(ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
            " NAME VARCHAR(255) UNIQUE NOT NULL, " +
            " RENTED_CAR_ID INT," +
            " CONSTRAINT fk_customer FOREIGN KEY (RENTED_CAR_ID)" +
            " REFERENCES CAR(ID)" +
            ")";

    final private static String INSERT_CUSTOMER = "INSERT INTO CUSTOMER (name) " +
            "VALUES (?)";

    public static void setDbName(String dbname) {
        H2Connection.dbname = dbname;
    }

    /**
     * @return statement to create company table
     */
    private static String getCreateCompany() {
        return String.format(CREATE_COMPANY, companyTableName);
    }

    /**
     *
     * @return car create query
     */
    private static String getCreateCar() {
        return String.format(CREATE_CAR, carTableName);
    }


    /**
     * Builds query: 'Drop if exists [tableName]'
     */
    static String dropTableIfExists(String table) {
        return String.format(DROP_IF_EXISTS, table);
    }

    /**
     * @return query to alter table company
     */
    private static String alterTable() {
        return ALTER_COMPANY;
    }

    /**
     *
     * @return query to create customer table
     */
    private static String getCreateCustomer() {return String.format(CREATE_CUSTOMER, customerTableName);}

    public static void setDropTables() {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();
            statement.executeUpdate(dropTableIfExists(customerTableName)); //Delete customer table
            statement.executeUpdate(dropTableIfExists(carTableName)); //Delete car table
            statement.executeUpdate(dropTableIfExists(companyTableName)); //Delete company table
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setCreateCompany() {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();
            statement.executeUpdate(getCreateCompany()); //Create company table
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setUpdateCompany() {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();
            statement.executeUpdate(alterTable());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setInsertCompany(String company_name) {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            PreparedStatement statement = connection.prepareStatement(INSERT_COMPANY);
            statement.setString(1, company_name);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet selectCompanies() {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            ResultSet companies = connection.createStatement().executeQuery(
                    "SELECT * FROM COMPANY;");
            companies.next();
            return companies;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet selectCompanyFilter(int id) {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM COMPANY WHERE ID = ?;");
            preparedStatement.setInt(1, id);
            ResultSet company = preparedStatement.executeQuery();
            company.next();
            return company;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setCreateCar() {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();
            //statement.executeUpdate(dropTableIfExists(carTableName)); //Delete car table
            statement.executeUpdate(getCreateCar()); //Create car table
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setUpdateCar() {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();
            statement.executeUpdate("ALTER TABLE CAR " +
                    "ALTER COLUMN id RESTART WITH 1;");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setInsertCar(String car_name, Integer company_id) {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            PreparedStatement statement = connection.prepareStatement(INSERT_CAR);
            statement.setString(1, car_name);
            statement.setInt(2, company_id);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet selectCarFilter(int id) {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM CAR WHERE ID = ?;");
            preparedStatement.setInt(1, id);
            ResultSet car = preparedStatement.executeQuery();
            car.next();
            return car;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet selectCarbyCompany(int id) {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM CAR WHERE COMPANY_ID = ?;");
            preparedStatement.setInt(1, id);
            ResultSet car = preparedStatement.executeQuery();
            car.next();
            return car;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setCreateCustomer() {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();
            statement.executeUpdate(getCreateCustomer()); //Create customer table
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setUpdateCustomer() {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();
            statement.executeUpdate("ALTER TABLE CUSTOMER " +
                    "ALTER COLUMN id RESTART WITH 1;");
            statement.executeUpdate("ALTER TABLE CUSTOMER " +
                    "ALTER rented_car_id SET DEFAULT NULL;");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setUpdateCustomerbyCar(Integer customer_id) {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE CUSTOMER SET rented_car_id = NULL " +
                    "WHERE id = ?");
            preparedStatement.setInt(1, customer_id);
            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setInsertCustomer(String customer_name) {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            PreparedStatement statement = connection.prepareStatement(INSERT_CUSTOMER);
            statement.setString(1, customer_name);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet selectCustomers() {

        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet customers = statement.executeQuery("SELECT * FROM CUSTOMER");
            customers.next();
            return customers;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet selectCustomersFilter(int id) {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet customer = statement.executeQuery("SELECT * FROM CUSTOMER WHERE ID = " + id + ";");
            customer.next();
            return customer;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setUpdateCustomerbyCarandCustomer(Integer car_id, int customer_id) {
        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE CUSTOMER SET rented_car_id = ?" +
                            " WHERE id = ?");
            preparedStatement.setInt(1, car_id);
            preparedStatement.setInt(2, customer_id);
            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet selectCarsNotRented(int chosenCompany) {

        try {
            Class.forName(jdbc_driver);
            Connection connection = DriverManager.getConnection(String.format(dbUrl, dbname));
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet cars = statement.executeQuery(
                    "SELECT car.id, car.name, car.company_id " +
                    "FROM car LEFT JOIN customer "+
                    "ON car.id = customer.rented_car_id "+
                    "WHERE company_id = " + chosenCompany + " AND customer.name IS NULL");
            cars.next();
            return cars;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
