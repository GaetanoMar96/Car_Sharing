package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CarSharing {

    private final Scanner scanner;
    public CarSharing() {
        scanner = new Scanner(System.in);
    }

    public void run() {

        H2Connection.setDbName("carsharing");
        H2Connection.setCreateCompany();
        H2Connection.setUpdateCompany();
        H2Connection.setCreateCar();
        H2Connection.setUpdateCar();
        H2Connection.setCreateCustomer();
        H2Connection.setUpdateCustomer();

        try {
            while (true) {
                System.out.println("\n1. Log in as a manager \n2. Log in as a customer \n3. Create a customer \n0. Exit");
                int mainMenuChoice = Integer.parseInt(scanner.nextLine());
                if (mainMenuChoice == 0) {
                    break;
                } else if (mainMenuChoice == 1) {
                    logInAsManager();
                } else if (mainMenuChoice == 2) {
                    logInAsCustomer();
                } else if (mainMenuChoice == 3) {
                    createCustomer();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void logInAsManager() throws SQLException {

        while (true) {
            System.out.println("\n1. Company list \n2. Create a company \n0. Back");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                break;
            } else if (choice == 1) {
                listAllCompanies("manager");
            } else if (choice == 2) {
                createCompany();
            }
        }
    }

    private void createCompany() {
        System.out.println("Enter the company name:");
        String companyName = scanner.nextLine();
        H2Connection.setInsertCompany(companyName);
    }

    private int listAllCompanies(String user) throws SQLException {
        ResultSet companies = H2Connection.selectCompanies();
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
                if (user.equals("manager"))
                    carMenu(chosenCompany, compName);
                else
                    return listAllCar(chosenCompany, compName, "customer");
            }
        }
        return 0;
    }

    private void carMenu(int chosenCompany, String compName) throws SQLException {
        System.out.println("'" + compName + "' company");
        while (true) {
            System.out.println("\n1. Car list \n2. Create a car \n0. Back");
            int choiceCar = Integer.parseInt(scanner.nextLine());
            if (choiceCar == 0) {
                break;
            } else if (choiceCar == 1) {
                listAllCar(chosenCompany, compName, "manager");
            } else if (choiceCar == 2) {
                createCar(chosenCompany);
            }
        }
    }

    private void createCar(int chosenCompany) {
        System.out.println("\nEnter the car name:");
        String carName = scanner.nextLine();
        H2Connection.setInsertCar(carName, chosenCompany);
    }

    private int listAllCar(int chosenCompany, String compName, String user) throws SQLException {

       ResultSet cars = H2Connection.selectCarsNotRented(chosenCompany);
       if (!cars.next()) {
           if (user.equals("manager")) {
               System.out.println("The car list is empty!");
               return -1;
           } else {
               System.out.println("No available cars in the '" + compName + "' company.");
               return -1;
           }
       } else {
           int count = 1;
           if (user.equals("customer"))
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
           if (user.equals("customer")) {
               int chosenCar = Integer.parseInt(scanner.nextLine());
               cars.absolute(chosenCar);
               if (chosenCar == 0)
                   logInAsCustomer();
               return cars.getInt("id");
           }
       }
        return 0;
    }

    private void createCustomer() {
        System.out.println("Enter the customer name:");
        String customerName = scanner.nextLine();
        H2Connection.setInsertCustomer(customerName);
    }

    private void logInAsCustomer() throws SQLException {

        ResultSet customers = H2Connection.selectCustomers();
        if (!customers.next())
            System.out.println("The customer list is empty!");
         else {
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
                customerMenu(chosenCustomer);
            }
         }
    }

    private void customerMenu(int chosenCustomer) throws SQLException {
        while (true) {
            boolean hasRentedCar = false;
            int carId = -1;
            ResultSet customer = H2Connection.selectCustomersFilter(chosenCustomer);
            carId = customer.getInt("rented_car_id");
            if (carId != 0)
                hasRentedCar = true;
            System.out.println("\n1. Rent a car \n2. Return a rented car \n3. My rented car \n0. Back");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0)
                break;
            else if (choice == 3) {
                if (!hasRentedCar)
                    System.out.println("You didn't rent a car!");
                else {
                    ResultSet rentedCar = H2Connection.selectCarFilter(carId);
                    System.out.println("You rented a car:");
                    String name = rentedCar.getString("name");
                    System.out.println(name);
                    int compId = rentedCar.getInt("company_id");
                    ResultSet belongToComp = H2Connection.selectCompanyFilter(compId);
                    System.out.println("Company:");
                    String compName = belongToComp.getString("name");
                    System.out.println(compName);
                }
            } else if (choice == 1) {
                if (hasRentedCar)
                    System.out.println("You've already rented a car!");
                else {
                    int neededCarId = listAllCompanies("customer");
                    if (neededCarId != 0) {
                        H2Connection.setUpdateCustomerbyCarandCustomer(neededCarId, chosenCustomer);
                        ResultSet car = H2Connection.selectCarFilter(neededCarId);
                        String carName = car.getString("name");
                        System.out.println("You rented '" + carName + "'");
                            }
                        }
                } else if (choice == 2) {
                    if (!hasRentedCar) {
                        System.out.println("You didn't rent a car!");
                    } else {
                        H2Connection.setUpdateCustomerbyCar(chosenCustomer);
                        System.out.println("You've returned a rented car!");
                    }
                }

        }
    }
}

