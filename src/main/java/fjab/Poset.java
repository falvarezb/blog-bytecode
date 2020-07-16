package fjab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fjab.TransitivityMode.TRANSITIVE_EXPANSION;
import static fjab.TransitivityMode.TRANSITIVE_REDUCTION;
import static java.util.stream.Collectors.toList;

public class Poset {

  private final int[][] array;
  private final int numberOfBinaryRelations;
  private final TransitivityMode transitivityMode;

  public Poset(int[][] array) throws PosetException {
    this(arrayDeepCopy(array), TRANSITIVE_EXPANSION);
  }

  private Poset(int[][] array, TransitivityMode transitivityMode) throws PosetException {
    if(transitivityMode == TRANSITIVE_EXPANSION) {
      checkReflexivityAndAntiSymmetryLaws(array);
      this.array = transitiveExpansion(array);
    }
    else {
      this.array = array;
    }
    this.numberOfBinaryRelations = (int) Arrays.stream(this.array).flatMapToInt(j -> Arrays.stream(j).filter(i -> i == 1)).count();
    this.transitivityMode = transitivityMode;
  }

  public int[][] getArrayRepresentation() {
    return arrayDeepCopy(array);
  }

  /**
   * Build a Poset based on the representation stored in the given file
   * The binary relation ≤ between the different pairs of elements is represented in a matrix where
   * 1 indicates that the elements are related to each other
   *
   * For instance, given a set of 4 elements [a,b,c,d], here's an example of a binary relation between them:
   *
   * 1 1 0 0
   * 0 1 1 0
   * 0 0 1 0
   * 1 0 0 1
   *
   * Because of the reflexivity law, all elements in the main diagonal must be 1.
   * Because of the antysimmetry law, elements in symmetric positions cannot be both 1.
   *
   * If a transitive reduction (https://en.wikipedia.org/wiki/Transitive_reduction) is provided, the transitivity law
   * will be applied in order to generate the final version of the Poset.
   *
   * For instance, after applying the transitivity law to the previous example, the final result is
   *
   * 1 1 1 0
   * 0 1 1 0
   * 0 0 1 0
   * 1 1 1 1
   *
   * The application of the transitivity law fails if the result violates the antisymmetry law.
   *
   * @param file File containing the Poset representation
   * @return Final version of the Poset with the transitivity law applied
   * @throws IOException
   * @throws IllegalArgumentException If chars other than numbers are used
   * @throws PosetException If any of the Poset laws is violated
   */
  public static Poset buildPosetFromFile(Path file) throws IOException, IllegalArgumentException, PosetException {

    List<String> lines = Files.readAllLines(file);
    int[][] array = new int[lines.size()][lines.size()];
    try {
      lines.stream().map(line -> Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray()).collect(toList()).toArray(array);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("The Poset representation must contain the numbers 1 and 0 only");
    }
    return new Poset(array, TRANSITIVE_EXPANSION);

  }

  private static void checkReflexivityAndAntiSymmetryLaws(int[][] poset) throws PosetException {
    for(int i=0; i<poset.length; i++){
      if(poset[i][i] != 1) {
        throw new ReflexivityException();
      }
      for(int j=i+1; j<poset[i].length; j++){
        if(poset[i][j]==1 && poset[j][i]==1) {
          throw new AntiSymmetryException();
        }
      }
    }
  }


  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int[] ints : array) {
      for (int anInt : ints) {
        sb.append(" ").append(anInt);
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  public Poset transitiveReduction() throws PosetException {
    int[][] newArray = arrayDeepCopy(array);
    for(int i=0; i<array.length; i++){
      for(int j=0; j<array[i].length; j++){
        if(i != j && array[i][j] == 1){
          for(int k=j+1; k<array[j].length; k++){
            if(array[j][k] == 1){
              newArray[i][k] = 0;
            }
          }
        }
      }
    }
    return new Poset(newArray, TRANSITIVE_REDUCTION);
  }

  /**
   * Applying the transitivity law is equivalent to doing a bitwise OR between a row and the rows it points to
   * Every time a row is updated, all the rows pointing to it must be updated and so on.
   * To do so, this method implements the Observer pattern:
   * - the observed subject is the considered row
   * - the observers or subscribers are the other rows that point to it
   *
   * This method is the inverse of a transitive reduction (https://en.wikipedia.org/wiki/Transitive_reduction)
   *
   *
   * @param poset
   * @return
   * @throws TransitivityException
   */
  private int[][] transitiveExpansion(int[][] array) throws PosetException {
    int[][] newArray = arrayDeepCopy(array);
    //initialisation
    Row[] rows = new Row[newArray.length];
    for (int i=0; i<newArray.length; i++){
      rows[i] = new Row(newArray[i]);
    }

    //register subscribers
    for (int i=0; i<newArray.length; i++){
      for(int j=0; j<newArray[i].length; j++){
        if(newArray[i][j]==1 && i != j){
          rows[j].registerSubscriber(rows[i]);
        }
      }
    }

    //kick-off updates
    for(Row row: rows){
      row.notifySubscribers();
    }

    //re-build poset
    for (int i=0; i<rows.length; i++){
      newArray[i] = rows[i].row;
    }

    //check that antisymmetry law is not violated
    try {
      checkReflexivityAndAntiSymmetryLaws(newArray);
    } catch (PosetException e) {
      throw new TransitivityException();
    }

    return newArray;
  }


  /**
   * Topological sort (https://en.wikipedia.org/wiki/Topological_sorting)
   * Each element of the poset is labelled by its position in the matrix representation
   * @return Array of sorted labels
   */
  public int[] sort() {
    if(this.transitivityMode != TRANSITIVE_EXPANSION){
      throw new UnsupportedOperationException();
    }
    //initialisation
    LabelledRow[] rows = new LabelledRow[array.length];
    for (int i = 0; i< array.length; i++){
      rows[i] = new LabelledRow(array[i], i);
    }

    Arrays.sort(rows, (o1, o2) -> o2.numberOfBinaryRelations - o1.numberOfBinaryRelations);

    return Arrays.stream(rows).mapToInt(row -> row.label).toArray();
  }

  /**
   * Auxiliar object to do the topological sort of the poset
   */
  static class LabelledRow {
    private final int label;
    private final int numberOfBinaryRelations;

    LabelledRow(int[] row, int label){
      this.label = label;
      this.numberOfBinaryRelations = Arrays.stream(row).filter(elem -> elem == 1).toArray().length;
    }
  }

  static class Row {

    Row(int[] row){
      this.row = row;
    }

    private final int[] row;
    private final List<Row> subscribers = new ArrayList<>();

    void registerSubscriber(Row subscriber) {
      subscribers.add(subscriber);
    }

    void notifySubscribers() {
      subscribers.forEach(s -> s.update(this));
    }

    /**
     * bitwise OR between this.row and otherRow.row
     */
    void update(Row otherRow){
      boolean updated = false;
      for(int j=0; j<otherRow.row.length; j++){
        if(otherRow.row[j]==1 && this.row[j]!=1){
          updated = true;
          this.row[j] = 1;
        }
      }
      if(updated) {
        this.notifySubscribers();
      }
    }
  }

  private static int[][] arrayDeepCopy(int[][] array) {
    int[][] newArray = new int[array.length][];
    for(int j=0; j<array.length; j++){
      newArray[j] = Arrays.copyOf(array[j], array[j].length);
    }
    return newArray;
  }
}
