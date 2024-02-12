package Shop;

import Models.*;
import Repository.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class temporaryShop {
    final Repository r = new Repository();
    final Scanner sc = new Scanner(System.in);


    public temporaryShop() throws IOException {
        //logga in kund
        final List<String> loginDetails = getLoginDetails(sc);
        final Customer customer = r.saveCustomerDetails(loginDetails.get(0), loginDetails.get(1), loginDetails.get(2));

        //hämta produkter
        final List<CategoryMap> cm = r.getListOfCategoryMap();

        //grupperar produkter utefter kategori i en hashmap
        final Map<String, List<Shoe>> groupedMapOfShoes = cm.stream()
                .collect(Collectors.groupingBy(categoryMap -> categoryMap.getCategory().getName(),
                        Collectors.mapping(CategoryMap::getShoe, Collectors.toList())));

        //be kund välja kategori
        promptUserToChooseCategory(customer.getFirstName());
        final int category = sc.nextInt();

        //filtrerar skor baserat på kategorival, spara i lista  och skriv ut resultatet
        final List<Shoe> shoesByCategory = filterOnCategory(groupedMapOfShoes, category);
        printListOfShoes(shoesByCategory);

        //be kund välja filter
        promptCustomerToChooseFilter();
        final int filter = sc.nextInt();

        System.out.print("ange " + filter + ": ");
        final String searchWord = sc.next();

        final List<Shoe> shoesByAttribute = filterOnAttribute(filter, shoesByCategory, searchWord);
        printListOfShoes(shoesByAttribute);
        System.out.println();

        //hämtar id:t för första skon i sista listan
        Shoe orderShoe = shoesByAttribute.get(0);
        System.out.println(orderShoe.getId());
        System.out.println();

        //ber kund ange sko att beställa och sparar sko i skoobjekt
        System.out.print("Vilken sko vill du beställa? Ange index: ");
        final int shoeIndexToOrder = sc.nextInt();
        final int correctIndex = shoeIndexToOrder;
        Shoe shoeToOrder = getShoeToOrder(shoesByAttribute, correctIndex);
        System.out.println("shoe to order " + shoeToOrder.getId());
        final int getOrderId = r.getOrderIdIfCustomerHasOrder(customer.getId());
        r.callAddToCart(getOrderId, customer.getId(), shoeToOrder.getId());


        //varje funktion i variabel tar en sko och kollar om attribut = sökord
        FilterOnAttribute foaColor = (shoe, word) -> shoe.getColor().getName().equalsIgnoreCase(word);
        FilterOnAttribute foaBrand = (shoe, word) -> shoe.getColor().getName().equalsIgnoreCase(word);
        FilterOnAttribute foaSize = (shoe, word) -> shoe.getColor().getName().equalsIgnoreCase(word);

    }

    //filtering metod som innehåller filtreringen och tar in sökord och interface
//    public void doFiltering(List<Shoe> list, String searchWord, Shoe shoe, FilterOnAttribute foa) {
//        list = list.stream().filter(f -> foa.filter(f, searchWord)).toList();
//
//    }

    //fixa higher order function på denna
    public List<Shoe> filterOnAttribute(int filter, List<Shoe> list, String searchWord) {
        List<Shoe> listOf = new ArrayList<>();
        switch (filter) {
            case 1 -> {
                final int size = Integer.parseInt(searchWord);
                listOf = list.stream().filter(a -> a.getSize().getEu() == size).toList();
            }
            case 2 -> listOf = list.stream()
                    .filter(a -> a.getBrand().getName().equalsIgnoreCase(searchWord)).toList();
            case 3 -> listOf = list.stream().filter(a -> a.getColor().getName().equalsIgnoreCase(searchWord)).toList();
            default -> System.out.println("Du har inte angett ett korrekt menyval.");
        }
        return listOf;
    }

    public Shoe getShoeToOrder(List<Shoe> list, int index) {
        Shoe shoeToOrder = list.get(index - 1);
        System.out.println(shoeToOrder.getId() + " "
                + shoeToOrder.getBrand().getName() + " "
                + shoeToOrder.getColor().getName());
        return shoeToOrder;
    }

    public List<Shoe> filterOnCategory(Map<String, List<Shoe>> map, int category) {

        List<Shoe> list = new ArrayList<>();

        switch (category) {
            case 1 -> list = map.get(Categories.WOMEN.getCategory());
            case 2 -> list = map.get(Categories.MEN.getCategory());
            case 3 -> list = map.get(Categories.BOOT.getCategory());
            case 4 -> list = map.get(Categories.SANDAL.getCategory());
            case 5 -> list = map.get(Categories.SNEAKER.getCategory());
            case 6 -> list = map.get(Categories.BOOTLEG.getCategory());
        }
        return list;
    }

    //printing methods - ändra utskrift till joining el dylikt
    public void printListOfShoes(List<Shoe> list) {

        String result = IntStream.range(0, list.size())
                .mapToObj(i -> (i + 1) + ". " + list.get(i).getBrand().getName()
                        + " " + list.get(i).getModel() + " " + list.get(i).getColor().getName()
                        + " strl " + list.get(i).getSize().getEu()
                        + " " + list.get(i).getPrice() + "kr")
                .collect(Collectors.joining("\n"));

        System.out.println(result);


//        list.stream().map(a -> a.getBrand().getName()
//                + " " + a.getModel() + " " + a.getColor().getName()
//                + " strl " + a.getSize().getEu()
//                + " " + a.getPrice() + "kr").forEach(System.out::println);
    }


    //prompting methods
    public void promptUserToChooseCategory(String name) {
        System.out.println("\nVälkommen " + name + "!\n" +
                "\nVilken kategori vill du titta på?  " +
                "\n1) " + Categories.WOMEN.getCategory() +
                "\n2) " + Categories.MEN.getCategory() +
                "\n3) " + Categories.BOOT.getCategory() +
                "\n4) " + Categories.SANDAL.getCategory() +
                "\n5) " + Categories.SNEAKER.getCategory() +
                "\n6) " + Categories.BOOTLEG.getCategory());
        System.out.print("Välj: ");
    }

    public void promptCustomerToChooseFilter() {

        System.out.println("\nVad vill du filtrera på?  " +
                "\n1) " + Filter.SIZE.getFilter() +
                "\n2) " + Filter.BRAND.getFilter() +
                " \n3) " + Filter.COLOR.getFilter());

    }


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
        new temporaryShop();
    }
}
