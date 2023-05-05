package carsharing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;

public class Car implements General{
    Integer id = null;
    String name = null;
    Integer company_id = null;
    private Scanner scanner = new Scanner(System.in);
    private Connection conn = Main.conn;
    private Statement stmt = Main.stmt;
    private String sql;
    public static HashMap cars;

    public Car(Integer id, String name, Integer company_id) {
        this.id = id;
        this.name = name;
        this.company_id = company_id;
    }
    public Car() {
    }

    public int carsList(String choice, boolean customer) {
        String sql = "";
        cars = new HashMap<>();
        int returnValue = -1;
        int i = company.getCompanyID(choice);
        if (!customer) {
            sql = "select * from car where company_id = %d".formatted(i);
        } else {
            sql = "select * from car where company_id = %d and  ID NOT IN (SELECT RENTED_CAR_ID FROM CUSTOMER WHERE RENTED_CAR_ID IS NOT NULL)".formatted(i);
        }
        try {
            ResultSet result = stmt.executeQuery(sql);
            int count = 0;
            while (result.next()) {
                count++;
                cars.put(count, result.getString("name"));
            }
            if (cars.isEmpty()) {
                System.out.println("The car list is empty!");
            }
            else {
                System.out.println("Car list:");
                for (Object key : cars.keySet()) {
                    System.out.println(key + ". " + cars.get(key));
                }
                System.out.println("0. Back");
                returnValue = Integer.MAX_VALUE;
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }
        return returnValue;
    }

    public void createCar(int i) {
        System.out.println("Enter the car name:");
        String name = scanner.nextLine();
        sql = "insert into car(name, company_id) values(\'%s\',%d)".formatted(name,i);
        try {
            stmt.executeUpdate(sql);
            System.out.println("The car was added!");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
