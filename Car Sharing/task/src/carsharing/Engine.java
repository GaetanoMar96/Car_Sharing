package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Engine {

    private final Scanner scanner;
    public Engine() {
        scanner = new Scanner(System.in);
    }

    public void run(String JDBC_DRIVER, String DB_URL) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL);
            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate("CREATE TABLE IF NOT EXISTS COMPANY " +
                        "(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                        " name VARCHAR(255) UNIQUE NOT NULL " +
                        ")");

                statement.executeUpdate("ALTER TABLE COMPANY " +
                        "ALTER COLUMN id RESTART WITH 1");

                statement.executeUpdate("CREATE TABLE IF NOT EXISTS CAR " +
                        "(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                        " name VARCHAR(255) UNIQUE NOT NULL, " +
                        " company_id INT NOT NULL," +
                        " CONSTRAINT fk_company FOREIGN KEY (company_id)" +
                        " REFERENCES COMPANY(id)" +
                        ")");

                statement.executeUpdate("ALTER TABLE CAR " +
                        "ALTER COLUMN id RESTART WITH 1");

                statement.executeUpdate("CREATE TABLE IF NOT EXISTS CUSTOMER " +
                        "(id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                        " name VARCHAR(255) UNIQUE NOT NULL, " +
                        " rented_car_id INT," +
                        " CONSTRAINT fk_car FOREIGN KEY (rented_car_id)" +
                        " REFERENCES CAR(id)" +
                        ")");

                statement.executeUpdate("ALTER TABLE CUSTOMER " +
                        "ALTER COLUMN id RESTART WITH 1");

                statement.executeUpdate("ALTER TABLE CUSTOMER " +
                        "ALTER rented_car_id SET DEFAULT NULL");


            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                while (true) {
                    System.out.println("\n1. Log in as a manager \n2. Log in as a customer \n3. Create a customer \n0. Exit");
                    int mainMenuChoice = Integer.parseInt(scanner.nextLine());
                    if (mainMenuChoice == 0) {
                        break;
                    } else if (mainMenuChoice == 1) {
                        logInAsManager(connection);
                    } else if (mainMenuChoice == 2) {
                        logInAsCustomer(connection);
                    } else if (mainMenuChoice == 3) {
                        createACustomer(connection);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void logInAsManager(Connection connection) throws SQLException {

        while (true) {
            System.out.println("\n1. Company list \n2. Create a company \n0. Back");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 0) {
                break;

            } else if (choice == 1) {
                listAllCompanies(connection, "manager");
            } else if (choice == 2) {
                createACompany(connection);
            }
        }
    }

    private void createACompany(Connection connection) {
        System.out.println("Enter the company name:");
        String companyName = scanner.nextLine();

        String insertCompany = "INSERT INTO COMPANY (name) " +
                "VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCompany)) {
            preparedStatement.setString(1, companyName);
            preparedStatement.executeUpdate();
            System.out.println("The company was created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int listAllCompanies(Connection connection, String mOrC) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet companies = statement.executeQuery("SELECT * FROM COMPANY")) {
                if (!companies.next()) {
                    System.out.println("The company list is empty");
                    return -1;
                } else {
                    List<String> companyList = new ArrayList<>();
                    System.out.println("\nChoose the company:");
                    while (true) {
                        int id = companies.getInt("id");
                        String name = companies.getString("name");
                        System.out.println(id + ". " + name);
                        companyList.add(name);
                        if (!companies.next()) {
                            break;
                        }
                    }
                    System.out.println("0. Back");
                    int chosenCompany = Integer.parseInt(scanner.nextLine());
                    if (chosenCompany != 0) {
                        String compName = companyList.get(chosenCompany - 1);

                        if (mOrC.equals("manager"))
                            carMenu(connection, chosenCompany, compName);
                        else
                            return listAllCar(connection, chosenCompany, compName, "customer");
                        }
                    }
                }
            }
        return 0;
    }

    private void carMenu(Connection connection, int chosenCompany, String compName) throws SQLException {
        System.out.println("'" + compName + "' company");
        while (true) {
            System.out.println("\n1. Car list \n2. Create a car \n0. Back");
            int choiceCar = Integer.parseInt(scanner.nextLine());
            if (choiceCar == 0) {
                break;
            } else if (choiceCar == 1) {
                listAllCar(connection, chosenCompany, compName, "manager");
            } else if (choiceCar == 2) {
                createACar(connection, chosenCompany);
            }
        }
    }

    private void createACar(Connection connection, int chosenCompany) {
        System.out.println("\nEnter the car name:");
        String carName = scanner.nextLine();
        String insertCompany = "INSERT INTO CAR (name, company_id) " +
                "VALUES (?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCompany)) {
            preparedStatement.setString(1, carName);
            preparedStatement.setInt(2, chosenCompany);
            preparedStatement.executeUpdate();
            System.out.println("The car was added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int listAllCar(Connection connection, int chosenCompany, String compName, String mOrC) throws SQLException {

        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            try (ResultSet cars = statement.executeQuery(
                "SELECT car.id, car.name, car.company_id " +
                "FROM car LEFT JOIN customer "+
                "ON car.id = customer.rented_car_id "+
                "WHERE company_id = " + chosenCompany + " AND customer.name IS NULL"))
            {
                if (!cars.next()) {
                    if (mOrC.equals("manager")) {
                        System.out.println("The car list is empty!");
                        return -1;
                    } else {
                        System.out.println("No available cars in the '" + compName + "' company.");
                        return -1;
                    }
                } else {
                    int count = 1;
                    if (mOrC.equals("customer"))
                        System.out.println("\nChoose a car:");
                    while (true) {
                        String name = cars.getString("name");
                        int id = cars.getInt("id");
                        System.out.println(count + ". " + name + ", car id: " + id);
                        count++;
                        if (!cars.next()) {
                            break;
                        }
                    }
                    System.out.println("0. Back");
                    if (mOrC.equals("customer")) {
                        int chosenCar = Integer.parseInt(scanner.nextLine());
                        cars.absolute(chosenCar);
                        if (chosenCar == 0)
                            logInAsCustomer(connection);
                        return cars.getInt("id");
                    }
                }
            }
        }
        return 0;
    }

    private void createACustomer(Connection connection) {
        System.out.println("Enter the customer name:");
        String customerName = scanner.nextLine();

        String insertCustomer = "INSERT INTO CUSTOMER (name) " +
                "VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCustomer)) {
            preparedStatement.setString(1, customerName);
            preparedStatement.executeUpdate();
            System.out.println("The customer was added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logInAsCustomer(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            try (ResultSet customers = statement.executeQuery("SELECT * FROM CUSTOMER")) {
                if (!customers.next()) {
                    System.out.println("The customer list is empty!");
                } else {
                    System.out.println("\nCustomer list:");
                    while (true) {
                        int id = customers.getInt("id");
                        String name = customers.getString("name");
                        System.out.println(id + ". " + name);
                        if (!customers.next()) {
                            break;
                        }
                    }
                    System.out.println("0. Back");
                    int chosenCustomer = Integer.parseInt(scanner.nextLine());
                    if (chosenCustomer != 0) {
                        customers.absolute(chosenCustomer);
                        customerMenu(connection, chosenCustomer);
                    }
                }
            }
        }
    }

    private void customerMenu(Connection connection, int chosenCustomer) {
        try (Statement statement = connection.createStatement()) {
            while (true) {
                boolean hasRentedCar = false;
                int carId = -1;
                try (ResultSet cus = statement.executeQuery("SELECT * FROM CUSTOMER WHERE id = " + chosenCustomer)) {
                    cus.next();
                    carId = cus.getInt("rented_car_id");
                    if (carId != 0)
                        hasRentedCar = true;
                }
                System.out.println("\n1. Rent a car \n2. Return a rented car \n3. My rented car \n0. Back");
                int choice = Integer.parseInt(scanner.nextLine());

                if (choice == 0)
                    break;
                else if (choice == 3) {
                    if (!hasRentedCar)
                        System.out.println("You didn't rent a car!");
                    else {
                        try (ResultSet rentedCar = statement.executeQuery("SELECT * FROM CAR WHERE id = " + carId)) {
                            System.out.println("You rented a car:");
                            rentedCar.next();
                            String name = rentedCar.getString("name");
                            System.out.println(name);
                            int compId = rentedCar.getInt("company_id");

                            try (ResultSet belongToComp = statement.executeQuery("SELECT * FROM COMPANY WHERE id = '" + compId + "'")) {
                                System.out.println("Company:");
                                belongToComp.next();
                                String compName = belongToComp.getString("name");
                                System.out.println(compName);
                            }
                        }
                    }
                } else if (choice == 1) {
                    if (hasRentedCar) {
                        System.out.println("You've already rented a car!");
                    } else {
                        int neededCarId = listAllCompanies(connection, "customer");

                        if (neededCarId != 0) {
                            statement.executeUpdate("UPDATE CUSTOMER SET rented_car_id = " + neededCarId +
                                    " WHERE id = " + chosenCustomer);
                            try (ResultSet car = statement.executeQuery("SELECT * FROM CAR WHERE id = " + neededCarId)) {
                                car.next();
                                String carName = car.getString("name");
                                System.out.println("You rented '" + carName + "'");
                            }
                        }
                    }
                } else if (choice == 2) {
                    // Return a car
                    if (!hasRentedCar) {
                        System.out.println("You didn't rent a car!");
                    } else {
                        statement.executeUpdate("UPDATE CUSTOMER SET rented_car_id = NULL " +
                                "WHERE id = " + chosenCustomer);
                        System.out.println("You've returned a rented car!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
