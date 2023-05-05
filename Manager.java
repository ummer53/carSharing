package carsharing;



public class Manager implements General{

    public Manager() {
    }

    public void managerOptions() {
        while(true) {
            System.out.printf("1. Company list\n" +
                    "2. Create a company\n" +
                    "0. Back\n");
            String managerChoice = scanner.nextLine();

            switch (managerChoice) {
                case "0" :
                    return;
                case "1" :
                    company.companyList();
                    if (company.getCompanyCount() == 0)
                        break;
                    String choice = scanner.nextLine();
                    if (!choice.equals("0")) {
                        company.companyMenu(choice);
                    }
                    break;
                case "2" :
                    company.createCompany();
                    break;
            }
        }
    }



}
