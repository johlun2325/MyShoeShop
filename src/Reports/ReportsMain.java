package Reports;

import Models.Customer;
import Models.Shoe;
import Repository.Repository;

import java.io.IOException;
import java.util.List;

public class ReportsMain {

    //metoder för att filtrera fram rapporter

    final Repository r = new Repository();

    public ReportsMain() throws IOException {
        final List<Customer> customers = r.getAllCustomers();
        final List<Shoe> shoes = r.getAllShoes();

        customers.forEach(a ->
                System.out.println(
                        a.getFirstName() + " " + a.getLastName() + " " +  a.getLocality()));
        System.out.println();
        shoes.forEach(r ->
                System.out.println(r.getId() + " " + r.getBrand().getName()));
    }

    public static void main(String[] args) throws IOException {
        new ReportsMain();
    }
}
