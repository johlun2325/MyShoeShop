package Models;

public class OrderMap {

    private int id;
    private int orderId;
    private int shoeId;
    private int quantity;

    public OrderMap(int id, int orderId, int shoeId, int quantity) {
        this.id = id;
        this.orderId = orderId;
        this.shoeId = shoeId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getShoeId() {
        return shoeId;
    }

    public void setShoeId(int shoeId) {
        this.shoeId = shoeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
