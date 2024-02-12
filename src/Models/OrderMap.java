package Models;

public class OrderMap {

    private int id;
    private Orders order;
    private Shoe shoe;
    private int quantity;

    public OrderMap(int id, Orders order, Shoe shoe, int quantity) {
        this.id = id;
        this.order = order;
        this.shoe = shoe;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
