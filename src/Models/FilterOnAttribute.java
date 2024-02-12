package Models;

import java.util.List;

@FunctionalInterface
public interface FilterOnAttribute {
     void filter(Shoe shoe, String searchword);
}
