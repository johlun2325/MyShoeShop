package Shop;

import Models.Categories;
import Models.CategoryMap;
import Models.Customer;
import Models.Shoe;
import Repository.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class ShopMain {

    /*
brand - id, name
category - id, name
categorymap - id, fk shoeid, fk categoryid
color - id, name
customer - id, firstname, lastname, address, locality,  fk passwordid
ordermap - id, fk orderid, fk shoeid, quantity
orders - id, orderdate, fk customerid
passwords - id, word
shoe - id, modelname, fk brandid, fk colorid, price, fk sizeid, balance
size - id, eu, us
*/

    final Repository r = new Repository();
    final Scanner sc = new Scanner(System.in);

    final String promptSearch = "Ange sökord: ";
    final String notValidTypeMsg = "Ogiltigt värde. Ange menyval 1-5.";
    final String notValidNumberMsg = "Ej giltigt menyval. Ange menyval 1-5";


    public ShopMain() throws IOException {


        //logga in kund
        final List<String> loginDetails = getLoginDetails(sc);
        final Customer customer = r.saveCustomerDetails(loginDetails.get(0), loginDetails.get(1), loginDetails.get(2));

        //hämta produkter
        final List<CategoryMap> cm = r.getListOfCategoryMap();

        //grupperar produkter utefter kategori
        final Map<String, List<Shoe>> groupedMapOfShoes = cm.stream()
                .collect(Collectors.groupingBy(categoryMap -> categoryMap.getCategory().getName(),
                        Collectors.mapping(CategoryMap::getShoe, Collectors.toList())));


        //be kund välja kategori
        try {
            //välj kategori
            promptUserToChooseCategory(customer.getFirstName());
            final int category = sc.nextInt();

            //filtrerar skor baserat på userinput
            final List<Shoe> shoesByCategory = filterOnCategory(groupedMapOfShoes, category);
            printListOfShoes(shoesByCategory);

            //välj filter/egenskap
            promptCustomerToChooseFilter();
            final int filter = sc.nextInt();

            System.out.println("Skriv in ditt sökord: ");



            //            final List<Shoe> shoesToShow = showAllProductsWithinFilter(category, "herrsko", groupedMapOfShoes);
//
//            //skriver ut den valda kategorin
//            shoesToShow.forEach(v -> System.out.println(v.getModel()));
        } catch (Exception e) {
            System.out.println(notValidTypeMsg);
        }

//        groupedMapOfShoes.forEach((k, v) -> System.out.println(k + " " + v.stream().map(Shoe::getModel).toList()));
//        System.out.println();
//        List<Shoe> list = groupedMapOfShoes.get(kategori1.toLowerCase());
//        list.stream().map(s -> s.getBrand().getName()).forEach(a -> System.out.println(a));

    }

    public void printListOfShoes(List<Shoe> list){
        list.stream().map(a -> a.getBrand().getName()
                + " " + a.getModel() + " " + a.getColor().getName()
                + " " + a.getPrice() + "kr").forEach(System.out::println);
    }

    //överlagrad metod
//    public void printListOfShoes(List<Shoe> list, String searchWord) {
//
//        switch (searchWord) {
//            case 1 -> list.stream().map(a -> a.getSize() == )
//            case 2 ->
//            case 3 ->
//        }
//
//
//        list.stream().map(a -> a.getBrand().getName()
//                + " " + a.getModel() + " " + a.getColor().getName()
//                + " " + a.getPrice() + "kr").forEach(System.out::println);
//    }


    public List<Shoe> filterOnCategory(Map<String, List<Shoe>> map, int category) {

        switch (category) {
            case 1 -> {
                return map.get(Categories.WOMEN.getCategory());
            }
            case 2 -> map.get(Categories.MEN.getCategory())
                    .stream().map(a -> a.getBrand().getName())
                    .forEach(System.out::println);
            case 3 -> map.get(Categories.BOOT.getCategory())
                    .stream().map(a -> a.getBrand().getName())
                    .forEach(System.out::println);
            case 4 -> map.get(Categories.SANDAL.getCategory())
                    .stream().map(a -> a.getBrand().getName())
                    .forEach(System.out::println);
            case 5 -> map.get(Categories.SNEAKER.getCategory())
                    .stream().map(a -> a.getBrand().getName())
                    .forEach(System.out::println);
            case 6 -> map.get(Categories.BOOTLEG.getCategory())
                    .stream().map(a -> a.getBrand().getName())
                    .forEach(System.out::println);
            default -> {
            }
        }
        final List<Shoe> emptyList = new ArrayList<>();
        return emptyList;

    }

    //filter kan vara storlek, märke, färg - ej klart
    public List<Shoe> showAllProductsWithinFilter(int filter, String searchWord, Map<String, List<Shoe>> map) {
        List<Shoe> filteredShoes = new ArrayList<>();
        switch (filter) {
            case 1 -> filteredShoes = map.entrySet().stream()
                    .filter(e -> e.getKey().equalsIgnoreCase(searchWord))
                    .flatMap(e -> e.getValue().stream())
                    .collect(Collectors.toList());

            case 2 -> System.out.println();
            case 3 -> System.out.println();
            case 4 -> System.out.println();
            default -> System.out.println(notValidNumberMsg);


            /*filteredShoes = map.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(searchWord))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());*/
        }
        return filteredShoes;
    }


    public void promptUserToChooseCategory(String name) {
        System.out.println("\nVälkommen " + name + "!\n" +
                "\nVilken kategori vill du titta på?  " +
                "\n1)Damsko" +
                "\n2)Herrsko" +
                " \n3)Känga" +
                " \n4)Sandal" +
                " \n5)Sneaker" +
                " \n6)Stövlett");
        System.out.print("Välj: ");
    }

    public void promptCustomerToChooseFilter() {

        System.out.println("\nVad vill du filtrera på?  " +
                "\n1)Storlek" +
                "\n2)Märke" +
                " \n3)Färg");

    }

//    public boolean getSearchInput(int filter, int Category) {
//        boolean wordNotEmpty = false;
//
//        while (!wordNotEmpty) {
//            System.out.print(promptSearch);
//            final String searchWord = sc.next();
//
//            if (!searchWord.isEmpty()) {
//                searchForProduct(searchWord, filter);
//                System.out.println("giltigt");
//                wordNotEmpty = true;
//            } else {
//                System.out.println("ogiltigt");
//            }
//        }
//        return wordNotEmpty;
//    }



    /*----------------------------------------------------------*/
    //logga in metoder

    public List<String> getLoginDetails(Scanner sc) throws IOException {
        boolean accessGranted = false;
        List<String> loginDetails = new ArrayList<>();

        while (!accessGranted) {
            System.out.print("Ange förnamn: ");
            final String firstname = sc.nextLine();
            System.out.print("Ange efternamn: ");
            final String lastname = sc.nextLine();
            System.out.print("Ange lösenord: ");
            final String password = sc.nextLine();

            loginDetails.add(firstname);
            loginDetails.add(lastname);
            loginDetails.add(password);

            //promptar, läser int och kontrollerar uppgifter
            accessGranted = loginCustomer(firstname, lastname, password);
            // om inte godkänd
            if (!accessGranted)
                System.out.println("Access denied");
        }
        return loginDetails;
    }

    public boolean loginCustomer(String firstname, String lastname, String password) throws IOException {
        return r.logInCustomer(firstname.trim(), lastname.trim(), password.trim());
    }


    public static void main(String[] args) throws IOException {
        ShopMain sm = new ShopMain();
    }


}
