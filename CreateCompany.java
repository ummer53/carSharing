package carsharing;

import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class CreateCompany {
    private int companyCount = Main.companyCount;
    private Connection conn = Main.conn;
    private Statement stmt = Main.stmt;
    private Scanner scanner = new Scanner(System.in);
    private String sql = "";

    public CreateCompany() {

        while (true) {

            String proceed = login();

            switch (proceed) {
                case "0":
                    try {
                        stmt.close();
                        conn.close();
                    } catch (Exception se) {
                        break;
                    }
                    break;
                case "1":
                    managerOptions();
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

    private void managerOptions() {
        while(true) {
            System.out.printf("1. Company list\n" +
                    "2. Create a company\n" +
                    "0. Back\n");
            String managerChoice = scanner.nextLine();

            switch (managerChoice) {
                case "0" :
                    return;
                case "1" :
                    companyList();
                    if (companyCount != 0) {
                        addCar();
                    }
                    break;
                case "2" :
                    createCompany();
                    break;
            }
        }
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
                    System.out.println(key + " " + customers.get(key));
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
                                if (stmt.executeQuery("select * from company").next()) {
                                    companyList();
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
                                System.out.println("You didn't rent a car!");
                            } else {
                                sql = "update customer set rented_car_id = 0 where id=%d".formatted(c.id) ;
                                stmt.executeUpdate(sql);
                            }
                        }
                    } catch (SQLException se) {
                        System.out.println("rentCar");
                        se.printStackTrace();
                    }
                }

                private void showRentedCar(Customer c) {
                    sql = "select * from customer where id = %d and rented_car_id <> NULL".formatted(c.id);
                    try {
                        Car car = new Car();
                        Company company = new Company();
                        ResultSet resultSet = stmt.executeQuery(sql);
                        if (resultSet.next()) {
                            car.id = resultSet.getInt("rented_car_id");
                            resultSet = stmt.executeQuery("select * from car where id = %d".formatted(car.id));
                            if (resultSet.next()) {
                                car.name = resultSet.getString("name");
                                company.id = resultSet.getInt("companyCount_id");
                            }
                            resultSet = stmt.executeQuery("select * from car where id = %d".formatted(car.id));
                            if (resultSet.next()) {
                                company.name = resultSet.getString("name");
                            }
                            System.out.println("Your rented car:\n" +
                                    car.name + "\n" +
                                    "Company:\n" +
                                    company.name);
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


    private void companyList() {
//        System.out.println("Choose the company:");
        String showCompanyList = " SELECT id, name FROM COMPANY";
        HashMap hm = new HashMap<>();
        if (companyCount != 0) {
            try {
                ResultSet resultSet = stmt.executeQuery(showCompanyList);
                while (resultSet.next()) {
                    hm.put(resultSet.getInt("id"), resultSet.getString("name"));
                }
                System.out.println("Choose the company:");
                for (Object i : hm.keySet()) {
                    System.out.println(i + ". " + hm.get(i));
                }
                System.out.println("0. Back");
                String choice = scanner.nextLine();
                if (choice.equals("0")) {
                    return;
                }else {
                    companyMenu(choice);
                }

                System.out.println();
            }catch (SQLException se) {
                se.printStackTrace();
            }
        } else {
            System.out.println("The company list is empty!");
        }
    }

    private void companyMenu(String choice) {
        String sql = "";
        if (choice.matches("\\d+"))
            sql = "select id from company where id = \'%d\'".formatted(Integer.parseInt(choice));
        else
            sql = "select id from company where name = \'%s\'".formatted(choice);
        int i = 0;
        try {
            ResultSet set = stmt.executeQuery(sql);
            while (set.next()) {
                i = set.getInt("id");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        while(true) {
            System.out.println("1. Car list\n" +
                    "2. Create a car\n" +
                    "0. Back");

            String ch = scanner.nextLine();
            switch (ch) {
                case "1":
                    sql = "select * from car where company_id = %d ".formatted(i);
                    try {
                        ResultSet result = stmt.executeQuery(sql);
                        HashMap<Integer,String> cars = new HashMap<>();
                        while (result.next()) {
                            cars.put(result.getInt("id"), result.getString("name"));
                        }
                        if (cars.isEmpty())
                            System.out.println("The car list is empty!");
                        else {
                            System.out.println("Car list:");
                            int count = 0;
                            for (Object key : cars.keySet()) {
                                count++;
                                System.out.println(count + ". " + cars.get(key));
                            }
                        }

                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                    break;
                case "2":
                    System.out.println("Enter the car name:");
                    String name = scanner.nextLine();
                    sql = "insert into car(name, company_id) values(\'%s\',%d)".formatted(name,i);
                    try {
                        stmt.executeUpdate(sql);
                        ResultSet set = stmt.executeQuery("select  * from car");
//                        while(set.next()) {
//                            System.out.println(set.getInt("id")+ " " + set.getString("name") + " " + set.getInt("company_id"));
//                        }
                        System.out.println("The car was added!");
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                    break;
                case "0":
                    return;
            }
        }
    }

    private void createCompany() {
        System.out.println("Enter the company name: ");
        String name = scanner.nextLine();
        String createCompany = "INSERT INTO COMPANY (NAME) \nVALUES (\'" + name + "\');";
        try {
            stmt.execute(createCompany);
            companyCount++;
            System.out.println("The company was created!");
            System.out.println();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }


}
