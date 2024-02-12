package Reports;

import Models.*;
import Repository.Repository;
import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ReportsMain {

    //metoder för att filtrera fram rapporter

    final Repository r = new Repository();

    final FilterOrderByAttribute fobya_color =
            (p, a) -> p.getShoe().getColor().getName().equalsIgnoreCase(a);
    final FilterOrderByAttribute fobya_brand =
            (p, a) -> p.getShoe().getBrand().getName().equalsIgnoreCase(a);
    final FilterOrderByAttribute fobya_size =
            (p, a) -> p.getShoe().getSize().getEu() == Integer.parseInt(a);

    public ReportsMain() throws IOException {

        //läser in alla ordrar
        final List<OrderMap> orders = r.getMappingOfAllOrders();

        getCustomersByProductBought(2, "ecco", orders);
        System.out.println();
        getNumberOfOrdersPerCustomer(orders);
        System.out.println();
        getTopSoldProducts(orders,3);
        System.out.println();




    }

    //rapport 1, filtrera fram kunder per produkt
    public void getCustomersByProductBought(int filter, String searchWord, List<OrderMap> list) throws IOException {

        switch (filter){
            case 1 -> filterOrders(list, searchWord, fobya_color);
            case 2 -> filterOrders(list, searchWord, fobya_brand);
            case 3 -> filterOrders(list, searchWord, fobya_size);
        }
    }
    public void filterOrders(List<OrderMap> list, String searchWord, FilterOrderByAttribute fobya){
        list.stream()
                .filter(a -> fobya.filterOrder(a, searchWord))
                .map(p -> p.getOrder().getCustomer().getFirstName() + " "
                        + p.getOrder().getCustomer().getLastName() + " "
                        + p.getOrder().getCustomer().getAddress()).distinct()
                .forEach(System.out::println);
    }

    //rapport 2, antal ordrar per kund
    public void getNumberOfOrdersPerCustomer(List<OrderMap> listOfOrders) throws IOException {

        // Grupperar: key - kunds fullständiga namn, value är antal ordrar
        Map<String, Long> ordersMappedPerCustomer =
                listOfOrders.stream().collect(Collectors.groupingBy(
                                o -> o.getOrder().getCustomer().getFirstName() + " " +
                                        o.getOrder().getCustomer().getLastName(), Collectors.counting()
                ));

        //skriver ut key - value pairs
        ordersMappedPerCustomer.forEach(
                (customerName, orderCount) -> System.out.println(customerName + ": " + orderCount));

    }
    //rapport 5, topplista
    public void getTopSoldProducts(List<OrderMap> list, int limit) {

        //Grupperar: key - modell, value är antal av samma modell
        Map<String, Long> numberOfOrdersPerModel =
                list.stream().collect(Collectors.groupingBy(
                        o -> o.getShoe().getModel(), Collectors.counting()
                ));

        //sorterar i desc, limit 3, skriver ut key - value pairs
        numberOfOrdersPerModel.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .forEach(entry -> System.out.println(entry.getKey() + " " + entry.getValue()));

    }


    //rapport 3, summa per kund
    public void getSumOfEachCustomer() {
        /*3. En rapport som listar alla kunder och hur mycket pengar varje kund,
        sammanlagt, har beställt för. Skriv ut varje kunds namn och summa.
    - samma som ovan, men summera nu priset per sko som kund köpt*/

    }

    //rapport 4, summa per ort
    public void getSumByCustomerLocality() {
        /*4. En rapport som listar beställningsvärde per ort. Skriv ut orternas namn och summa.
    - som ovan, men summera nu per kundernas ort*/
    }



    public static void main(String[] args) throws IOException {
        new ReportsMain();
    }
}
