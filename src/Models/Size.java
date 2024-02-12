package Models;

public class Size {

    private int id;
    private int eu;

    public Size(int id, int eu) {
        this.id = id;
        this.eu = eu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEu() {
        return eu;
    }

    public void setEu(int eu) {
        this.eu = eu;
    }
}
