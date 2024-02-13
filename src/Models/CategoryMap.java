package Models;

public class CategoryMap {

//    private int id;
    private Category category;
    private Shoe shoe;

    public CategoryMap(Category category, Shoe shoe) {
        this.category = category;
        this.shoe = shoe;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Shoe getShoe() {
        return shoe;
    }

    public void setShoe(Shoe shoe) {
        this.shoe = shoe;
    }
}
