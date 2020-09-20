package fjab.poset;

import fjab.poset.error.PosetException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

  public static void main(String[] args) throws IOException, PosetException {

//    Poset<String> poset = new Poset<>(Arrays.asList("a", "b", "c", "d"), PosetUtil.buildIncidenceMatrixFromFile(Paths.get("src/main/resources/poset1.txt")));
//    System.out.println(poset.toString());
    example();

  }

  static void example() {
    Poset<String> poset = new Poset<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"), new int[][] {
      {1, 0, 0, 1, 0, 0, 0, 0},
      {0, 1, 0, 1, 1, 0, 0, 0},
      {0, 0, 1, 0, 1, 0, 0, 1},
      {0, 0, 0, 1, 0, 1, 1, 1},
      {0, 0, 0, 0, 1, 0, 1, 0},
      {0, 0, 0, 0, 0, 1, 0, 0},
      {0, 0, 0, 0, 0, 0, 1, 0},
      {0, 0, 0, 0, 0, 0, 0, 1}
    });

    //Elements in topological sort
    System.out.println(new ArrayList<>(poset));

    //Transitive expansion
    System.out.println(Arrays.deepToString(poset.getTransitiveExpansion()));

    //Transitive reduction
    System.out.println(Arrays.deepToString(poset.getTransitiveReduction()));
  }


}
