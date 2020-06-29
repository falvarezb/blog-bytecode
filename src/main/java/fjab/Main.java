package fjab;

import java.io.IOException;

import static fjab.Poset.printPoset;
import static fjab.Poset.readConfiguration;

public class Main {

  public static void main(String[] args) throws IOException {

    printPoset(readConfiguration("src/main/resources/poset1.txt"));

  }


}
