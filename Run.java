package carsharing;

import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class Run implements General{
    private int companyCount = Main.companyCount;
    private Connection conn = Main.conn;
    private Statement stmt = Main.stmt;
    private Scanner scanner = new Scanner(System.in);
    private String sql = "";
    private boolean returnedRentedCar = false;
   // HashMap<Integer,String> cars = Car.cars;

    public Run () {

        while (true) {
            Manager manager = new Manager();
            String proceed = login();

            switch (proceed) {
                case "0":
                    try {
                        stmt.close();
                        conn.close();
                        return;
                    } catch (Exception se) {
                        return;
                    }
                case "1":
                    manager.managerOptions();
                    break;
                case "2":
                    customer();
                    break;
                case "3":
                    createCustomer();
                    break;
            }
        }
    }


    private String login() {
        System.out.println("1. Log in as a manager\n" +
                "2. Log in as a customer\n" +
                "3. Create a customer\n" +
                "0. Exit");
        return scanner.nextLine();

    }


    private void customer() {
        HashMap customers = getCustomerList();
        displayCustomers(customers);
        if (customers.isEmpty()) {
            return;
        }
        System.out.println("0. Back");
        chooseCustomer();
    }

        private HashMap getCustomerList() {
            try {
                ResultSet resultSet = stmt.executeQuery("select * from customer");
                HashMap<Integer, String> customers = new HashMap<>();
                while (resultSet.next()) {
                    customers.put(resultSet.getInt("id"),resultSet.getString("name"));
                }
                return customers;

            } catch (SQLException se) {
                se.printStackTrace();
            }
            return null;
        }

        private void displayCustomers(HashMap customers) {
            if(customers == null || customers.isEmpty())
                System.out.println("The customer list is empty!");
            else {
                System.out.println("Customer list:");
                for (Object key : customers.keySet()) {
                    System.out.println(key + ". " + customers.get(key));
                }
            }
        }

        private void chooseCustomer() {
            String choosedCustomer = scanner.nextLine();
            if (choosedCustomer.equals("0"))
                return;
            else if (choosedCustomer.matches("\\d")) {
                sql = "select * from customer where id = %d".formatted(Integer.valueOf(choosedCustomer));

            } else {
                sql = "select * from customer where name = %s".formatted(choosedCustomer);
            }
            try {
                ResultSet resultSet = stmt.executeQuery(sql);
                Customer c = new Customer();
                while(resultSet.next()) {
                    c = new Customer(resultSet.getInt("id"), resultSet.getString("name"), null);
                }
                customerOptions(c);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

            private void customerOptions(Customer c) {
                while(true) {
                    System.out.println("1. Rent a car\n" +
                            "2. Return a rented car\n" +
                            "3. My rented car\n" +
                            "0. Back");
                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            rentCar(c);
                            break;
                        case "2":
                            returnCar(c);
                            break;
                        case "3":
                            showRentedCar(c);
                            break;
                        case "0":
                            return;
                    }
                }
            }

                private void rentCar(Customer c) {
                    try {

                        ResultSet resultSet = stmt.executeQuery("select * from customer where id = %d".formatted(c.id));
                        if (resultSet.next()) {
                            c.rentedCar = resultSet.getInt("rented_car_id");
                            if (c.rentedCar != 0) {
                                System.out.println("You've already rented a car!");
                            } else {
                                ResultSet resultSet1 = stmt.executeQuery("select * from company");
                                if (resultSet1.next()) {
                                    while (true) {
                                        company.companyList();
                                        String choice = scanner.nextLine();
                                        if (choice.equals("0"))
                                            break;
                                        else {
                                            while (true) {
                                            int carNumber = car.carsList(choice, true);
                                            if (carNumber < 0) {
                                                break;
                                            }
                                            choice = scanner.nextLine();
                                            if (choice.equals("0"))
                                                break;
                                            else {
                                                if (choice.matches("\\d")) {
                                                    sql = "select * from car where name = \'%s\'".formatted(Car.cars.get(Integer.valueOf(choice)));
                                                } else {
                                                    sql = "select * from car where name = \'%s\'".formatted(choice);
                                                }
                                                Car car = new Car();
                                                resultSet = stmt.executeQuery(sql);
                                                while (resultSet.next()) {
                                                    car.id = resultSet.getInt("id");
                                                    car.name = resultSet.getString("name");
                                                }
                                                sql = String.format("update customer set rented_car_id = %d where id = %d", car.id, c.id);
                                                stmt.executeUpdate(sql);
                                                System.out.printf("You rented \'%s\'%n", car.name);
                                                returnedRentedCar = false;
                                                return;
                                            }
                                            }
                                        }
                                    }
                                } else {
                                    System.out.println("The company list is empty!");
                                }
                            }
                        }
                    } catch (SQLException se) {
                        System.out.println("rentCar");
                        se.printStackTrace();
                    }
                }
                private void returnCar(Customer c) {
                    try {

                        ResultSet resultSet = stmt.executeQuery("select * from customer where id = %d".formatted(c.id));
                        if (resultSet.next()) {
                            c.rentedCar = resultSet.getInt("rented_car_id");
                            if (c.rentedCar == 0) {
                                if (returnedRentedCar == true) {
                                    System.out.println("You've returned a rented car!");
                                } else {
                                    System.out.println("You didn't rent a car!");
                                }
                            } else {
                                sql = "update customer set rented_car_id = NULL where id=%d".formatted(c.id) ;
                                stmt.executeUpdate(sql);
                                returnedRentedCar = true;
                                System.out.println("You've returned a rented car!");
                            }
                        }
                    } catch (SQLException se) {
                        System.out.println("rentCar");
                        se.printStackTrace();
                    }
                }

                private void showRentedCar(Customer c) {
                    sql = "select * from customer where id = %d".formatted(c.id);
                    try {
                        Car car = new Car();
                        Company company = new Company();
                        ResultSet resultSet = stmt.executeQuery(sql);
                        if (resultSet.next()) {
                            car.id = resultSet.getInt("rented_car_id");
                            if (car.id == 0) {
                                     System.out.println("You didn't rent a car!");
                                     return;
                                  }
                            resultSet = stmt.executeQuery("select * from car where id = %d".formatted(car.id));
                            if (resultSet.next()) {
                                car.name = resultSet.getString("name");
                                company.id = resultSet.getInt("company_id");
                            }
                            resultSet = stmt.executeQuery("select * from company where id = %d".formatted(company.id));
                            if (resultSet.next()) {
                                company.name = resultSet.getString("name");
                            }
                          //  if (car.name.equals(null)) {
                          //      System.out.println("You didn't rent a car!");
                         //   } else {
                                System.out.println("Your rented car:\n" +
                                        car.name + "\n" +
                                        "Company:\n" +
                                        company.name);
                          //  }
                        } else {
                            System.out.println("You didn't rent a car!");
                        }
                    } catch (SQLException se) {
                        System.out.println("showRentedCars");
                        se.printStackTrace();
                    }
                }

    private void createCustomer() {
        System.out.println("Enter the customer name:");
        String name = scanner.nextLine();
        sql = "insert into customer(name) values (\'%s\')".formatted(name);
        try {
            stmt.executeUpdate(sql);
            System.out.println("The customer was added!");
        } catch (SQLException se) {
            System.out.println("error in inserting customer");
        }
    }
    private int getCarID(String chooice) {
        return 0;
    }



}
