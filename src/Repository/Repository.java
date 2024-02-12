package Repository;

import Models.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Repository {

    //alla metoder som kopplar upp mot databasen

    private final Properties prop = new Properties();
    private final String propertiesPath = "src/Repository/Settings.properties";
    private List<Customer> allCustomers = new ArrayList<>();

    public boolean logInCustomer(String firstname, String lastname, String password) throws IOException {
        prop.load(new FileInputStream(propertiesPath));

        try (Connection con = DriverManager.getConnection(
                prop.getProperty("connectionString"),
                prop.getProperty("username"),
                prop.getProperty("password"));
             PreparedStatement stmt = con.prepareStatement(
                     "select customer.firstname, customer.lastname, passwords.word \n" +
                             "from customer\n" +
                             "inner join passwords on customer.passwordid = passwords.id\n" +
                             "where customer.firstname = ? and customer.lastname = ? and passwords.word = ?");
        ) {
            stmt.setString(1, firstname);
            stmt.setString(2, lastname);
            stmt.setString(3, password);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String fn = rs.getString("firstname");
                String ln = rs.getString("lastname");
                String pass = rs.getString("word");

                if (fn.equalsIgnoreCase(firstname))
                    if (ln.equalsIgnoreCase(lastname))
                        if (pass.equals(password))
                            return true;


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Customer saveCustomerDetails(String firstname, String lastname, String password) throws IOException {

        prop.load(new FileInputStream(propertiesPath));
        final Customer customer = new Customer();

        try (Connection con = DriverManager.getConnection(
                prop.getProperty("connectionString"),
                prop.getProperty("username"),
                prop.getProperty("password"));
             PreparedStatement stmt = con.prepareStatement(
                     "select customer.id, customer.firstname, customer.lastname, customer.address, customer.locality from customer\n" +
                             "inner join passwords on customer.passwordId = passwords.id\n" +
                             "where customer.firstname = ?\n" +
                             "and customer.lastname = ?\n" +
                             "and passwords.word = ?;");
        ) {
            stmt.setString(1, firstname);
            stmt.setString(2, lastname);
            stmt.setString(3, password);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                customer.setId(rs.getInt("id"));
                customer.setFirstName(rs.getString("firstname"));
                customer.setLastName(rs.getString("lastname"));
                customer.setAddress(rs.getString("address"));
                customer.setLocality(rs.getString("locality"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customer;
    }

    public List<CategoryMap> getListOfCategoryMap() throws IOException {
        //läs in all relevant data för att visa upp produkter för kund
        // skomodell, märke, färg, pris

        prop.load(new FileInputStream(propertiesPath));
//        Map<String, List<Shoe>> mapOfShoesAndCategories = new HashMap<>();
        List<CategoryMap> shoeCategoryMapping = new ArrayList<>();


        try (Connection con = DriverManager.getConnection(
                prop.getProperty("connectionString"),
                prop.getProperty("username"),
                prop.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select shoe.id as ShoeId, shoe.modelName as ModelName, brand.id as BrandId, brand.name as BrandName, \n" +
                             "color.id as ColorId, color.name ColorName, shoe.price as Price, \n" +
                             "size.id as SizeId, size.eu as Size, balance as Balance, \n" +
                             "category.id as CategoryId, category.name as CategoryName\n" +
                             "from shoe\n" +
                             "inner join categoryMap on shoe.id =  categoryMap.shoeId\n" +
                             "inner join category on category.id = categoryMap.categoryid\n" +
                             "inner join brand on brand.id = shoe.brandid\n" +
                             "inner join color on color.id = shoe.colorId\n" +
                             "inner join size on size.id = shoe.sizeId;");
        ) {

            // ShoeId, ModelName, *BrandId, BrandName,
            // ColorId, ColorName, Price, SizeId, Size, Balance
            // CategoryId, CategoryName

            while (rs.next()) {
                final Category category = new Category(rs.getInt("CategoryId"), rs.getString("CategoryName"));
                final Brand brand = new Brand(rs.getInt("BrandId"), rs.getString("BrandName"));
                final Color color = new Color(rs.getInt("ColorId"), rs.getString("ColorName"));
                final Size size = new Size(rs.getInt("SizeId"), rs.getInt("Size"));
                final CategoryMap cm = new CategoryMap();
                final Shoe shoe = new Shoe(
                        rs.getInt("ShoeId"), rs.getString("ModelName"),
                        brand, color, rs.getInt("Price"), size, rs.getInt("Balance"));

                cm.setCategory(category);
                cm.setShoe(shoe);

                shoeCategoryMapping.add(cm);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shoeCategoryMapping;
    }

    public void callAddToCart(int orderId, int customerId, int shoeId) throws IOException {
        System.out.println(shoeId);
        prop.load(new FileInputStream(propertiesPath));

        try (Connection c = DriverManager.getConnection(
                prop.getProperty("connectionString"),
                prop.getProperty("username"),
                prop.getProperty("password"));
             CallableStatement stmt = c.prepareCall("call addToCart(?,?,?)");
        ) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, customerId);
            stmt.setInt(3, shoeId);
            stmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    public int getOrderIdIfCustomerHasOrder(int customerId) throws IOException {
        prop.load(new FileInputStream(propertiesPath));
        final LocalDate currentDate = LocalDate.now();

        try (Connection con = DriverManager.getConnection(
                prop.getProperty("connectionString"),
                prop.getProperty("username"),
                prop.getProperty("password"));
             PreparedStatement stmt = con.prepareStatement(
                     "select id from orders where customerId = ? and orderDate = ?");
        ) {
            stmt.setInt(1, customerId);
            stmt.setString(2, String.valueOf(currentDate));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }


        } catch (SQLException e) {
            System.out.println("fel när kolla orders");
            e.printStackTrace();
        }
        return -1;
    }

    public List<Customer> getAllCustomers() throws IOException {
        prop.load(new FileInputStream(propertiesPath));
        List<Customer> listOfCustomers = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(
                prop.getProperty("connectionString"),
                prop.getProperty("username"),
                prop.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select * from customer");
        ) {

            while (rs.next()) {
                //id, first, last, address, locality
                final Customer customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("address"),
                        rs.getString("locality"));

                listOfCustomers.add(customer);

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listOfCustomers;
    }


    public List<Shoe> getAllShoes() throws IOException {
        prop.load(new FileInputStream(propertiesPath));
        List<Shoe> listOfShoes = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(
                prop.getProperty("connectionString"),
                prop.getProperty("username"),
                prop.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select shoe.id as ShoeId, shoe.modelName as ModelName, \n" +
                             "brand.id as BrandId, brand.name as BrandName, \n" +
                             "color.id as ColorId, color.name ColorName, shoe.price as Price, \n" +
                             "size.id as SizeId, size.eu as Size, balance as Balance\n" +
                             "from shoe\n" +
                             "inner join brand on brand.id = shoe.brandid\n" +
                             "inner join color on color.id = shoe.colorId\n" +
                             "inner join size on size.id = shoe.sizeId;");
        ) {

            while (rs.next()) {

                // ShoeId, ModelName, (brand), (color), Price, Balance
                // BrandId, BrandName,
                // ColorId, ColorName
                // SizeId, SizeEu
                final Brand b = new Brand(rs.getInt("BrandId"), rs.getString("BrandName"));
                final Color c = new Color(rs.getInt("ColorId"), rs.getString("ColorName"));
                final Size s = new Size(rs.getInt("SizeId"), rs.getInt("Size"));

                final Shoe shoe = new Shoe(
                        rs.getInt("ShoeId"),
                        rs.getString("ModelName"),
                        b, c,
                        rs.getInt("Price"),
                        s,
                        rs.getInt("Balance"));

                listOfShoes.add(shoe);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listOfShoes;
    }

    public List<OrderMap> getMappingOfAllOrders() throws IOException {
        //läs in all relevant data för att visa upp produkter för kund
        // skomodell, märke, färg, pris

        prop.load(new FileInputStream(propertiesPath));
        List<OrderMap> listOfOrdermaps = new ArrayList<>();
        final List<Customer> c = getAllCustomers(); //läser från db
        final List<Shoe> s = getAllShoes(); //läser från db

        try (Connection con = DriverManager.getConnection(
                prop.getProperty("connectionString"),
                prop.getProperty("username"),
                prop.getProperty("password"));
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select ordermap.id as ordermapId, ordermap.quantity as quantity,\n" +
                             "orders.id as orderId, orders.orderDate,\n" +
                             "customer.id as customerId,\n" +
                             "shoe.id as shoeId\n" +
                             "from ordermap\n" +
                             "inner join orders on orders.id = ordermap.orderid\n" +
                             "inner join customer on customer.id = orders.customerid\n" +
                             "inner join shoe on shoe.id = ordermap.shoeId;");
        ) {

              //lägg till sko/kund från lista med
            Customer customer = new Customer();
            Shoe shoe = new Shoe();

            while (rs.next()) {
                final int customerId = rs.getInt("customerId");
                final List<Customer> matchedCustomer = c.stream().filter(cu -> cu.getId() == customerId).toList();
                if (matchedCustomer.size() == 1)
                    customer = matchedCustomer.get(0);

                final int shoeId = rs.getInt("shoeId");
                final List<Shoe> matchedShoe = getAllShoes();
                if (matchedShoe.size() == 1)
                    shoe = matchedShoe.get(0);

                final Orders order = new Orders(
                        rs.getInt("orderId"),
                        rs.getString("orderDate"),
                        customer);

                final OrderMap orderMap = new OrderMap(
                        rs.getInt("ordermapId"),
                        order, shoe,
                        rs.getInt("quantity"));

                listOfOrdermaps.add(orderMap);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfOrdermaps;

    }

}
