package carsharing;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;


public class Car implements General{
    Integer id = null;
    String name = null;
    Integer company_id = null;
    private String sql;
    public static HashMap cars;
    private boolean returnedRentedCar = false;

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

        sql = !customer ? "select * from car where company_id = %d".formatted(i) :
        "select * from car where company_id = %d and  ID NOT IN (SELECT RENTED_CAR_ID FROM CUSTOMER WHERE RENTED_CAR_ID IS NOT NULL)".formatted(i);

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
                cars.forEach((K,V) -> System.out.println(K + ". " + V)); //displays car list
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

    public void rentCar(Customer c) {
        try {

            ResultSet resultSet = stmt.executeQuery("select * from customer where id = %d and rented_car_id is null".formatted(c.id));
            if (resultSet.next()) {
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
                                    sql = (choice.matches("\\d")) ? "select * from car where name = \'%s\'".formatted(Car.cars.get(Integer.valueOf(choice))) :
                                            "select * from car where name = \'%s\'".formatted(choice);
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
                }
            }
            else {
                System.out.println("You've already rented a car!");
            }
        } catch (SQLException se) {
            System.out.println("rentCar");
            se.printStackTrace();
        }
    }

    public void returnCar(Customer c) {
        try {

            ResultSet resultSet = stmt.executeQuery("select * from customer where id = %d and rented_car_id is not null".formatted(c.id));
            if (resultSet.next()) {
                    sql = "update customer set rented_car_id = NULL where id=%d".formatted(c.id) ;
                    stmt.executeUpdate(sql);
                    returnedRentedCar = true;
                    System.out.println("You've returned a rented car!");

            } else {
                if (returnedRentedCar == true) {
                    System.out.println("You've returned a rented car!");
                } else {
                    System.out.println("You didn't rent a car!");
                }
            }
        } catch (SQLException se) {
            System.out.println("rentCar");
            se.printStackTrace();
        }
    }

    public void showRentedCar(Customer c) {
        sql = ("select cus.rented_car_id, c.name, c.company_id, comp.name " +
                "from customer cus inner join car c on " +
                "cus.rented_car_id = c.id inner join company comp on " +
                "c.company_id = comp.id " +
                "where cus.id = %d and rented_car_id is not null").formatted(c.id);

   try {
            ResultSet rs = stmt.executeQuery(sql);
//            ResultSetMetaData rsmd = rs.getMetaData();  to get meta data of resultset
//            int columnsNumber = rsmd.getColumnCount();    to get no. of columns in resultset
            Car car = new Car();
            Company company = new Company();
            if (rs.next()) {
                car.name = rs.getString(2);
                company.name = rs.getString(4);
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
}
