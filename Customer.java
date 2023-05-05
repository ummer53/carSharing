package carsharing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Customer implements General {
    Integer id = null;
    String name = null;
    Integer rentedCar = null;
    private String sql;

    public Customer(Integer id, String name, Integer rentedCar) {
        this.id = id;
        this.name = name;
        this.rentedCar = rentedCar;
    }
    public Customer(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public Customer () {

    }

    public void createCustomer() {
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

    public void customer() {
        HashMap customers = getCustomerList();
        displayCustomers(customers);
        if (customers.isEmpty()) {
            return;
        }
        System.out.println("0. Back");
        chooseCustomer();
    }

    public HashMap getCustomerList() {
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

    public void displayCustomers(HashMap customers) {
        if(customers == null || customers.isEmpty())
            System.out.println("The customer list is empty!");
        else {
            System.out.println("Customer list:");
            for (Object key : customers.keySet()) {
                System.out.println(key + ". " + customers.get(key));
            }
        }
    }

    public void chooseCustomer() {
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
                    car.rentCar(c);
                    break;
                case "2":
                    car.returnCar(c);
                    break;
                case "3":
                    car.showRentedCar(c);
                    break;
                case "0":
                    return;
            }
        }
    }
}
