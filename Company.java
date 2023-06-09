package carsharing;




import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Company implements General{
    Integer id = null;
    String name = null;

    private String sql;

    public Company(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public Company () {
    }
    public int getCompanyCount() {
       sql = "select * from company";
       try {
          return stmt.executeQuery(sql).next() ? Integer.MAX_VALUE : 0;  // to check whether there is any company in table
       } catch (SQLException se) {
           System.out.println("Error in getting company count");
           se.printStackTrace();
       }
       return 0;
    }

    public void companyList() {
        String showCompanyList = " SELECT id, name FROM COMPANY";
        HashMap hm = new HashMap<>();
        try {
            if (stmt.executeQuery(showCompanyList).next()) {

                ResultSet resultSet = stmt.executeQuery(showCompanyList);
                while (resultSet.next()) {
                    hm.put(resultSet.getInt("id"), resultSet.getString("name"));
                }
                System.out.println("Choose the company:");
                hm.forEach((K,V) -> System.out.println(K + ". " + V));   // displays company list
                System.out.println("0. Back");

            } else {
                System.out.println("The company list is empty!");
            }
        }catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void createCompany() {
        System.out.println("Enter the company name: ");
        String name = scanner.nextLine();
        String createCompany = "INSERT INTO COMPANY (NAME) \nVALUES (\'" + name + "\');";
        try {
            stmt.execute(createCompany);
            System.out.println("The company was created!");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void companyMenu(String choice) {
        String sql = "";
        int i = getCompanyID(choice);
        while(true) {
            System.out.println("1. Car list\n" +
                    "2. Create a car\n" +
                    "0. Back");

            String ch = scanner.nextLine();
            switch (ch) {
                case "1":
                    int carNumber = car.carsList(choice, false);
                    if (carNumber < 0) {
                        break;
                    }else {
                        String newChoice = scanner.nextLine();
                        if (newChoice.equals("0"))
                            break;
                        else companyMenu(newChoice);
                    }
                    break;
                case "2":
                    car.createCar(i);
                    break;
                case "0":
                    return;
            }
        }
    }

    public int getCompanyID(String choice) {
        sql = (choice.matches("\\d+") ? "select id from company where id = %d".formatted(Integer.parseInt(choice)) :
             "select id from company where name = \'%s\'".formatted(choice));
        try {
            ResultSet set = stmt.executeQuery(sql);
            while (set.next()) {
                return  set.getInt("id");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return 0;
    }
}
