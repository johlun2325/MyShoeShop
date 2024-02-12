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

    //Varje funktion i variabel tar en sko och kollar om attribut = sökord, returns boolean(Predicate)
    final FilterOnAttribute foaColor = (shoe, word) -> shoe.getColor().getName().equalsIgnoreCase(word);
    final FilterOnAttribute foaBrand = (shoe, word) -> shoe.getBrand().getName().equalsIgnoreCase(word);
    final FilterOnAttribute foaSize = (shoe, word) -> shoe.getSize().getEu() == Integer.parseInt(word);


    public temporaryShop() {

        //databas try
        try {

            //Logga in kund
            //IOEXCEPTION methods
            final List<String> loginDetails = getLoginDetails(sc);
            final Customer customer = r.saveCustomerDetails(loginDetails.get(0), loginDetails.get(1), loginDetails.get(2));

            //Hämtar produkter och mappar till kategori
            final List<CategoryMap> cm = r.getListOfCategoryMap();

            //Grupperar produkter utefter kategori i en hashmap
            final Map<String, List<Shoe>> groupedMapOfShoes = cm.stream()
                    .collect(Collectors.groupingBy(categoryMap -> categoryMap.getCategory().getName(),
                            Collectors.mapping(CategoryMap::getShoe, Collectors.toList())));

            //be kund välja kategori
            promptUserToChooseCategory(customer.getFirstName());
            final int category = sc.nextInt();

            //filtrerar skor baserat på kategorival, spara i lista  och skriv ut resultatet
            final List<Shoe> shoesByCategory = filterOnCategory(groupedMapOfShoes, category);
            printListOfShoes(shoesByCategory);

            // Ber kund välja filter brand, color, size
            int filter = 0;
            String searchWord = "";

            try {
                promptCustomerToChooseFilter();
                filter = sc.nextInt();
                System.out.print("ange " + filter + ": ");
                searchWord = sc.next();
            } catch (Exception e) {
                System.out.println("Kunde inte läsa input");
            }

            //Filtrerar skor på filterval och sökord samt skriver ut resultatet
            final List<Shoe> shoesByAttribute = doFiltering(filter, searchWord, shoesByCategory);
            printListOfShoes(shoesByAttribute);
            System.out.println();

            //Ber kund ange sko att beställa
            System.out.print("Vilken sko vill du beställa? Ange index: ");
            int shoeIndexToOrder = -1;
            try {
                shoeIndexToOrder = sc.nextInt();
            } catch (Exception e) {
                System.out.println("Felaktig input");
            }

            //Sparar val i skoobjekt och skriver ut vilken sko som ska beställas
            Shoe shoeToOrder = getShoeToOrder(shoesByAttribute, shoeIndexToOrder);
            System.out.println("Shoe to order " + shoeToOrder.getId());

            //Hämtar aktuell kunds kundId och kallar på addToCart();
            final int getOrderId = r.getOrderIdIfCustomerHasOrder(customer.getId());
            r.callAddToCart(getOrderId, customer.getId(), shoeToOrder.getId());


        } catch (IOException e) {
            System.out.println("IOException - kan inte läsa från databas");
        }


    }

    //Filtreringsmetod som innehåller metod för filtrering och tar in sökord och interface
    public List<Shoe> doFiltering(int filter, String wordToSearchFor, List<Shoe> list) {

        List<Shoe> filteredShoes = new ArrayList<>();

        switch (filter) {
            case 1 -> filteredShoes = filterOnAttribute(list, wordToSearchFor, foaSize);
            case 2 -> filteredShoes = filterOnAttribute(list, wordToSearchFor, foaBrand);
            case 3 -> filteredShoes = filterOnAttribute(list, wordToSearchFor, foaColor);

        }
        return filteredShoes;
    }

    //                final int size = Integer.parseInt(searchWord);
    public List<Shoe> filterOnAttribute(List<Shoe> list, String searchWord, FilterOnAttribute foa) {
        return list.stream().filter(a -> foa.filter(a, searchWord)).toList();
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

        final List<Shoe> tempList = list.stream().filter(a -> a.getBalance() > 0).toList();

        String result = IntStream.range(0, tempList.size())
                .mapToObj(i -> (i + 1) + ". " + tempList.get(i).getBrand().getName()
                        + ", " + tempList.get(i).getModel() + ", " + tempList.get(i).getColor().getName()
                        + ", strl " + tempList.get(i).getSize().getEu()
                        + ", " + tempList.get(i).getPrice() + "kr, " + tempList.get(i).getBalance() + " st")

                .collect(Collectors.joining("\n"));
        if (!result.isEmpty())
            System.out.println(result);
        else {
            System.out.println("Inget resultat");
            System.exit(22);
        }
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
