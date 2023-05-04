package carsharing;

import java.io.IOException;
import java.sql.*;

public class Main {

    static int companyCount = 0;
    static Connection conn = null;
    static Statement stmt = null;



    public static void main(String[] args) throws IOException {
        String DB_URL = "jdbc:h2:./src/carsharing/db/test";
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-databaseFileName")) {
                DB_URL = "jdbc:h2:./src/carsharing/db/" + args[1];
            }
        }
        System.out.println(DB_URL);
        //CreateCompany company = new CreateCompany(DB_URL);


        try {
            // STEP 1: Register JDBC driver
            Class.forName ("org.h2.Driver");

            //STEP 2: Open a connection
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(true);

            //STEP 3: Execute a query
            String  sql;

            stmt = conn.createStatement();
            sql = "DROP TABLE IF EXISTS customer";
            stmt.executeUpdate(sql);
            sql =  "DROP TABLE IF EXISTS car";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS COMPANY";
            stmt.executeUpdate(sql);


            sql = "CREATE TABLE COMPANY (ID bigint auto_increment(1,1), " +
                    "NAME VARCHAR(255) unique not null , PRIMARY KEY ( id ))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE car (ID bigint auto_increment(1,1), " +
                    "NAME VARCHAR(255) unique not null , PRIMARY KEY ( id )," +
                    "company_id int not null ," +
                    "constraint fk_company_id foreign key (company_id)" +
                    "references company( id))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE customer (ID bigint auto_increment(1,1), " +
                    "NAME VARCHAR(255) unique not null , PRIMARY KEY ( id )," +
                    "rented_car_id int," +
                    "constraint fk_rented_car foreign key (rented_car_id)" +
                    "references car( id))";
            stmt.executeUpdate(sql);

            // STEP 4: Clean-up environment
            //stmt.close();
            //conn.close();
        } catch(SQLException se) {
            se.printStackTrace(); //Handle errors for JDBC
        } catch(Exception e) {
            e.printStackTrace(); //Handle errors for Class.forName
        }
        CreateCompany company = new CreateCompany();
    }
}