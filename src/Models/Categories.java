package Models;

public enum Categories {

    WOMEN("damsko"),
    MEN("herrsko"),
    BOOT("känga"),
    SANDAL("sandal"),
    SNEAKER("sneaker"),
    BOOTLEG("stövlett");

    private final String category;

    private Categories(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }


}
