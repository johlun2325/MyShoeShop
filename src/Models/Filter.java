package Models;

public enum Filter {
    SIZE("storlek"),
    BRAND("märke"),
    COLOR( "färg");

    private final String filter;

    private Filter(String filterSv) {
        this.filter = filterSv;
    }


    public String getFilter() {
        return filter;
    }



}
