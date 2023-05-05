package carsharing;

import java.sql.*;
import java.util.HashMap;

public class Run implements General{

    private String sql = "";
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
                    customer.customer();
                    break;
                case "3":
                    customer.createCustomer();
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
}
