package fjab;

import fjab.error.PosetException;

import java.io.IOException;
import java.nio.file.Paths;

import static fjab.Poset.*;

public class Main {

  public static void main(String[] args) throws IOException, PosetException {

    Poset poset = buildPosetFromFile(Paths.get("src/main/resources/poset1.txt"));
    System.out.println(poset.toString());

  }


}
