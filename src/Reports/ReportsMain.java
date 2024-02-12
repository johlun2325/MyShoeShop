package Reports;

import Models.*;
import Repository.Repository;
import java.io.IOException;
import java.util.*;
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
        final Scanner sc = new Scanner(System.in);

        //läser in alla ordrar
        final List<OrderMap> orders = r.getMappingOfAllOrders();

        System.out.print("Vilken rapport vill du generera?\n" +
                "1) Kunder baserat på produkts egenskap\n" +
                "2) Antalet ordrar per kund\n" +
                "3) Topplista, sålda produkter\n" +
                "4) Totalsumma per kund\n" +
                "5) Totalsumma per ort\n" +
                "Välj: ");

        final int choice = sc.nextInt();

        System.out.println();
        if (choice != 0) {

            switch (choice) {
                case 1 -> {
                    try {

                        System.out.print("\n1) Färg\n2) Märke\n3)Storlek\nVälj: ");
                        final int response = sc.nextInt();
                        System.out.print("\nVänligen skriv det du vill söka på: ");
                        final String searchWord = sc.next();
                        getCustomersByProductBought(response, searchWord, orders);

                    } catch (IOException e) {
                        System.out.println("\nNågot gick fel");
                    } catch (NumberFormatException ex) {
                        System.out.println("\nFel input");
                    } catch (Exception x) {
                        System.out.println("\nOkänt fel inträffade");
                    }
                }
                case 2 -> getNumberOfOrdersPerCustomer(orders);
                case 3 -> getTopSoldProducts(orders, 3);
                case 4 -> getSumOfEachCustomer(orders);
                case 5 -> getSumByCustomerLocality(orders);
                default -> System.out.println("\nDu har inte gjort ett korrekt menyval, 1-5");

            }
        }
        else
            System.out.println("Du har inte angett ett val");

//        getSumOfEachCustomer(orders);
//        System.out.println();
//        getSumByCustomerLocality(orders);


    }

    //rapport 1, filtrera fram kunder per produkt
    public void getCustomersByProductBought(int filter, String searchWord, List<OrderMap> list) throws IOException {

        switch (filter) {
            case 1 -> filterOrders(list, searchWord, fobya_color);
            case 2 -> filterOrders(list, searchWord, fobya_brand);
            case 3 -> filterOrders(list, searchWord, fobya_size);
        }
    }

    public void filterOrders(List<OrderMap> list, String searchWord, FilterOrderByAttribute fobya) {
        list.stream()
                .filter(a -> fobya.filterOrder(a, searchWord))
                .map(p -> p.getOrder().getCustomer().getFirstName() + " "
                        + p.getOrder().getCustomer().getLastName() + ", "
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
                (customerName, orderCount) -> System.out.println(customerName + ": " + orderCount + " st"));

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
                .forEach(entry -> System.out.println(entry.getKey() + " " + entry.getValue() + " st"));

    }

    //rapport 3, summa per kund
    public void getSumOfEachCustomer(List<OrderMap> list) {
        /*3. En rapport som listar alla kunder och hur mycket pengar varje kund,
        sammanlagt, har beställt för. Skriv ut varje kunds namn och summa.
    - samma som ovan, men summera nu priset per sko som kund köpt*/

        Map<Customer, List<OrderMap>> mapByCustomer =
                list.stream().collect(Collectors.groupingBy(
                        o -> o.getOrder().getCustomer()
                ));

        mapByCustomer.forEach((customer, orderMaps) -> {
            int totalSum = orderMaps.stream()
                    .mapToInt(orderMap -> orderMap.getShoe().getPrice() * orderMap.getQuantity())
                    .sum();

            System.out.println(customer.getFirstName() + " " + customer.getLastName() + ": " + totalSum + " kr");
        });

    }

    //rapport 4, summa per ort
    public void getSumByCustomerLocality(List<OrderMap> list) {

        // grupperar utefter ort
        Map<String, List<OrderMap>> mapByLocality =
                list.stream().collect(Collectors.groupingBy(
                        o -> o.getOrder().getCustomer().getLocality()
                ));

        // summerar beställningsvärdet för alla ordrar per ort och printar
        mapByLocality.forEach((locality, orderMaps) -> {
            int totalSum = orderMaps.stream()
                    .mapToInt(orderMap -> orderMap.getShoe().getPrice() * orderMap.getQuantity())
                    .sum();

            System.out.println(locality + ": " + totalSum + " kr");
        });
    }


    public static void main(String[] args) throws IOException {
        new ReportsMain();
    }
}
