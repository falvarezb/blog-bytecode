package fjab.poset;

import fjab.poset.error.PosetException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {

  public static void main(String[] args) throws IOException, PosetException {

    Poset<String> poset = new Poset<>(Arrays.asList("a", "b", "c", "d"), PosetUtil.buildIncidenceMatrixFromFile(Paths.get("src/main/resources/poset1.txt")));
    System.out.println(poset.toString());

  }


}
