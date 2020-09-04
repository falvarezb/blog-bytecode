package fjab.poset;

import fjab.poset.error.PosetException;

import java.io.IOException;
import java.nio.file.Paths;

import static fjab.poset.Poset.*;

public class Main {

  public static void main(String[] args) throws IOException, PosetException {

    Poset poset = buildPosetFromFile(Paths.get("src/main/resources/poset1.txt"));
    System.out.println(poset.toString());

  }


}
