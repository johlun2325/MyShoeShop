package Models;

import java.util.List;

@FunctionalInterface
public interface FilterOnAttribute {
     boolean filter(Shoe shoe, String searchword);
}
