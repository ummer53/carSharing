package carsharing;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;

public interface General {
    Company company = new Company();
    Car car = new Car();
    Customer customer = new Customer();
    Connection conn = Main.conn;
    Statement stmt = Main.stmt;
    Scanner scanner = new Scanner(System.in);
   // HashMap cars = new HashMap<>();

}
