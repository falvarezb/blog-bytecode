package fjab.poset;

import fjab.poset.error.AntiSymmetryException;
import fjab.poset.error.InvalidPosetException;
import fjab.poset.error.PosetException;
import fjab.poset.error.ReflexivityException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static fjab.poset.Util.arrayDeepCopy;
import static fjab.poset.Util.isSquareMatrix;
import static java.util.stream.Collectors.toList;

public class PosetUtil {

  static void checkReflexivityAndAntiSymmetryLaws(int[][] incidenceMatrix) throws PosetException {
    for (int i = 0; i < incidenceMatrix.length; i++) {
      if (incidenceMatrix[i][i] != 1) {
        throw new ReflexivityException();
      }
      for (int j = i + 1; j < incidenceMatrix[i].length; j++) {
        if (incidenceMatrix[i][j] == 1 && incidenceMatrix[j][i] == 1) {
          throw new AntiSymmetryException();
        }
      }
    }
  }

  /**
   * Applying the transitivity law is equivalent to doing a bitwise OR between a row and the rows it points to
   * Every time a row is updated, all the rows pointing to it must be updated and so on.
   * To implement this pattern of cascading changes, this method makes use of the Observer pattern:
   * - the observed subject is the considered row
   * - the observers or subscribers are the other rows that point to it
   * <p>
   * After doing the transitive expansion, it is necessary to verify that the resulting Poset still complies
   * with the antisymmetry property
   * <p>
   * This method is the inverse of a transitive reduction (https://en.wikipedia.org/wiki/Transitive_reduction)
   *
   * @param incidenceMatrix Internal representation of the Poset
   * @return Original array updated in place
   * @throws InvalidPosetException Thrown if transitive expansion is not possible
   */
  static int[][] transitiveExpansion(int[][] incidenceMatrix) {
    int[][] newArray = arrayDeepCopy(incidenceMatrix);
    //initialisation
    AuxRowForTransitiveExpansion[] rows = new AuxRowForTransitiveExpansion[newArray.length];
    for (int i = 0; i < newArray.length; i++) {
      rows[i] = new AuxRowForTransitiveExpansion(newArray[i]);
    }

    //register subscribers
    for (int i = 0; i < newArray.length; i++) {
      for (int j = 0; j < newArray[i].length; j++) {
        if (newArray[i][j] == 1 && i != j) {
          rows[j].registerSubscriber(rows[i]);
        }
      }
    }

    //kick-off updates
    for (AuxRowForTransitiveExpansion row : rows) {
      row.notifySubscribers();
    }

    //re-build poset
    for (int i = 0; i < rows.length; i++) {
      newArray[i] = rows[i].row;
    }

    //check that antisymmetry law is not violated
    try {
      checkReflexivityAndAntiSymmetryLaws(newArray);
    } catch (PosetException e) {
      throw new InvalidPosetException();
    }

    return newArray;
  }

  /**
   * Returns a new Poset whose internal representation is
   * the transitive reduction (https://en.wikipedia.org/wiki/Transitive_reduction) of this
   */
  static int[][] transitiveReduction(int[][] expandedArray) {
    int[][] newArray = arrayDeepCopy(expandedArray);
    for (int i = 0; i < expandedArray.length; i++) {
      for (int j = 0; j < expandedArray[i].length; j++) {
        if (i != j && expandedArray[i][j] == 1) {
          for (int k = 0; k < expandedArray[j].length; k++) {
            if (j != k && expandedArray[j][k] == 1) {
              newArray[i][k] = 0;
            }
          }
        }
      }
    }
    return newArray;
  }

  public static int[][] buildIncidenceMatrixFromFile(Path file) throws IOException, IllegalArgumentException {
    List<String> lines = Files.readAllLines(file);
    int[][] array = new int[lines.size()][lines.size()];
    try {
      lines.stream().map(line -> Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray()).collect(toList()).toArray(array);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("The file cannot have non-numeric chars");
    }
    return array;
  }

  static <E> void validateArguments(List<E> elements, int[][] incidenceMatrix) {
    if(elements.size() != incidenceMatrix.length || !isSquareMatrix(incidenceMatrix)){
      throw new IllegalArgumentException("The incidence matrix must be NxN, where N is the number of elements");
    }
    if(new HashSet<>(elements).size() != elements.size()) {
      throw new IllegalArgumentException("the list of elements cannot contain duplicates");
    }
    if (Arrays.stream(incidenceMatrix).flatMapToInt(Arrays::stream).filter(j -> j != 0 && j != 1).count() > 0) {
      throw new IllegalArgumentException("The incidence matrix must contain the numbers 1 and 0 only");
    }

    checkReflexivityAndAntiSymmetryLaws(incidenceMatrix);
  }

  static UnsupportedOperationException uoe() { return new UnsupportedOperationException(); }

  /**
   * This algorithm consists in sorting the rows by the sum of its elements (as we use 1s and 0s
   * to represent the binary relations, the sum of the elements of each row is equivalent to counting
   * the number of binary relations of each element)
   *
   * @return Array of sorted labels
   */
  static int[] sort(int[][] expandedArray) {
    //initialisation
    PosetUtil.AuxRowForSort[] rows = new PosetUtil.AuxRowForSort[expandedArray.length];
    for (int i = 0; i < expandedArray.length; i++) {
      rows[i] = new PosetUtil.AuxRowForSort(expandedArray[i], i);
    }

    Arrays.sort(rows, (o1, o2) -> o2.numberOfBinaryRelations - o1.numberOfBinaryRelations);

    return Arrays.stream(rows).mapToInt(row -> row.label).toArray();
  }

  static <E> List<E> collectElements(Poset<E> poset) {
    List<E> elements = new ArrayList<>();
    poset.iterator().forEachRemaining(elements::add);
    return elements;
  }

  /**
   * Auxiliary object to do the topological sort of the poset
   */
  private static class AuxRowForSort {
    private final int label;
    private final int numberOfBinaryRelations;

    AuxRowForSort(int[] row, int label) {
      this.label = label;
      this.numberOfBinaryRelations = Arrays.stream(row).sum();
    }
  }

  /**
   * Auxiliary object to do the transitive expansion of the poset
   */
  private static class AuxRowForTransitiveExpansion {

    private final int[] row;
    private final List<AuxRowForTransitiveExpansion> subscribers = new ArrayList<>();
    AuxRowForTransitiveExpansion(int[] row) {
      this.row = row;
    }

    void registerSubscriber(AuxRowForTransitiveExpansion subscriber) {
      subscribers.add(subscriber);
    }

    void notifySubscribers() {
      subscribers.forEach(s -> s.update(this));
    }

    /**
     * bitwise OR between this.row and otherRow.row
     */
    void update(AuxRowForTransitiveExpansion otherRow) {
      boolean updated = false;
      for (int j = 0; j < otherRow.row.length; j++) {
        if (otherRow.row[j] == 1 && this.row[j] != 1) {
          updated = true;
          this.row[j] = 1;
        }
      }
      if (updated) {
        this.notifySubscribers();
      }
    }
  }
}
