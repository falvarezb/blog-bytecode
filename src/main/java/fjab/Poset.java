package fjab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Poset {

  public static String[][] readConfiguration(String fileConfiguration) throws IOException {

    List<String> lines = Files.readAllLines(Paths.get(fileConfiguration));
    String[][] array = new String[lines.size()][lines.size()];
    lines.stream().map(line -> line.split("")).collect(Collectors.toList()).toArray(array);
    return array;
  }

  public static void printPoset(String[][] poset) {
    for(int i=0; i<poset.length; i++){
      for(int j=0; j<poset[i].length; j++){
        System.out.print(poset[i][j]);
      }
      System.out.println("");
    }
  }
}
