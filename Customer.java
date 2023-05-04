package carsharing;

public class Customer {
    Integer id = null;
    String name = null;
    Integer rentedCar = null;

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
}
