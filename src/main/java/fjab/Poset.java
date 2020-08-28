package fjab;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * A Poset (https://en.wikipedia.org/wiki/Partially_ordered_set) is a set of elements with a binary relation
 * defined between them (not necessarily between all of them) that must satisfy the laws of
 * - reflexivity
 * - antisymmetry
 * - transitivity
 * <p>
 * A binary relation with these properties is called "partial order" and is denoted by ≤.
 * <p>
 * The Poset can be represented as a matrix containing 1s and 0s.
 * <p>
 * For instance, given a set of 4 elements [a,b,c,d], here's an example of a binary relation between them:
 * <p>
 * 1 1 0 0
 * 0 1 1 0
 * 0 0 1 0
 * 1 0 0 1
 * <p>
 * The element [0][1] is 1, meaning that the element 0 is related to the element 1, in other words, a ≤ b
 * <p>
 * Because of the:
 * - reflexivity property, all elements in the main diagonal must be 1.
 * - antisymmetry property, elements in symmetric positions cannot be both 1.
 * <p>
 * The above example is the so-called transitive reduction (https://en.wikipedia.org/wiki/Transitive_reduction), a
 * simplified version of the Poset without the transitive relations.
 * <p>
 * After applying the transitive property (transitive expansion), the above example becomes:
 * <p>
 * 1 1 1 0
 * 0 1 1 0
 * 0 0 1 0
 * 1 1 1 1
 * <p>
 * When creating the Poset, the constructor always does the transitive expansion if necessary.
 * <p>
 * Once the Poset is created, it is possible to get a copy where the internal representation is the
 * transitive reduction.
 * <p>
 * A Poset can be interpreted as a directed acyclic graph (DAG) in which the shortest distance between
 * all pairs of elements that are connected is always 1 (transitivity property).
 * Therefore, as a DAG, it is possible to compute its topological sort.
 * According to https://en.wikipedia.org/wiki/Topological_sorting
 * <p>
 * "topological sort or topological ordering of a directed graph is a linear ordering of its vertices
 * such that for every directed edge uv from vertex u to vertex v, u comes before v in the ordering"
 * <p>
 * In our example, if each element of the poset is identified by its position in the matrix representation,
 * the topological sort is: [3,0,1,2].
 * The topological sort can present ambiguity for elements that are not connected between them.
 * It's worth noting that elements with the same number of ≤ cannot be connected (if a ≤ b, a will have at
 * least one more ≤ than b)
 */
public class Poset {

  private final int[][] arrayExpanded;
  private final int[][] arrayReducted;
  private final int numberOfExpandedBinaryRelations;
  private final int numberOfReductedBinaryRelations;
  private final int posetOrder;

  public Poset(int[][] array) {
    if (Arrays.stream(array).flatMapToInt(Arrays::stream).filter(j -> j != 0 && j != 1).count() > 0) {
      throw new IllegalArgumentException("The Poset representation must contain the numbers 1 and 0 only");
    }
    checkReflexivityAndAntiSymmetryLaws(array);
    this.arrayExpanded = transitiveExpansion(array);
    this.arrayReducted = transitiveReduction();
    assert arrayExpanded.length == arrayReducted.length;

    this.numberOfExpandedBinaryRelations = Util.sum(this.arrayExpanded);
    this.numberOfReductedBinaryRelations = Util.sum(this.arrayReducted);
    this.posetOrder = numberOfReductedBinaryRelations - arrayReducted.length;
  }

  /**
   * Build a Poset based on the representation stored in the given file
   *
   * @param file File containing the Poset representation
   * @return Final version of the Poset with the transitivity law applied
   * @throws IOException              If file read fails
   * @throws IllegalArgumentException If chars other than numbers are used
   * @throws PosetException           If any of the Poset laws is violated
   */
  public static Poset buildPosetFromFile(Path file) throws IOException, IllegalArgumentException, PosetException {

    List<String> lines = Files.readAllLines(file);
    int[][] array = new int[lines.size()][lines.size()];
    try {
      lines.stream().map(line -> Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray()).collect(toList()).toArray(array);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("The Poset representation cannot have non-numeric chars");
    }
    return new Poset(array);

  }

  private static void checkReflexivityAndAntiSymmetryLaws(int[][] poset) throws PosetException {
    for (int i = 0; i < poset.length; i++) {
      if (poset[i][i] != 1) {
        throw new ReflexivityException();
      }
      for (int j = i + 1; j < poset[i].length; j++) {
        if (poset[i][j] == 1 && poset[j][i] == 1) {
          throw new AntiSymmetryException();
        }
      }
    }
  }

  public int[][] getArrayExpanded() {
    return arrayExpanded;
  }

  public int[][] getArrayReducted() {
    return arrayReducted;
  }

  public int getNumberOfReductedBinaryRelations() {
    return numberOfReductedBinaryRelations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Poset poset = (Poset) o;
    return Arrays.deepEquals(arrayExpanded, poset.arrayExpanded);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(arrayExpanded);
  }

  public int[][] getArrayRepresentation(TransitivityMode transitivityMode) {
    if(transitivityMode == TransitivityMode.TRANSITIVE_EXPANSION)
      return Util.arrayDeepCopy(arrayExpanded);
    else if(transitivityMode == TransitivityMode.TRANSITIVE_REDUCTION)
      return Util.arrayDeepCopy(arrayReducted);
    else
      throw new IllegalArgumentException("Unknown transitive mode");
  }

  public int getPosetOrder() {
    return posetOrder;
  }

  public int getNumberOfExpandedBinaryRelations() {
    return getNumberOfBinaryRelations(TransitivityMode.TRANSITIVE_EXPANSION);
  }

  public int getNumberOfBinaryRelations(TransitivityMode transitivityMode) {
    if(transitivityMode == TransitivityMode.TRANSITIVE_EXPANSION)
      return numberOfExpandedBinaryRelations;
    else if(transitivityMode == TransitivityMode.TRANSITIVE_REDUCTION)
      return numberOfReductedBinaryRelations;
    else
      throw new IllegalArgumentException("Unknown transitive mode");
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int[] ints : arrayExpanded) {
      for (int anInt : ints) {
        sb.append(" ").append(anInt);
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * Returns a new Poset whose internal representation is
   * the transitive reduction (https://en.wikipedia.org/wiki/Transitive_reduction) of this
   */
  private int[][] transitiveReduction() {
    int[][] newArray = Util.arrayDeepCopy(arrayExpanded);
    for (int i = 0; i < arrayExpanded.length; i++) {
      for (int j = 0; j < arrayExpanded[i].length; j++) {
        if (i != j && arrayExpanded[i][j] == 1) {
          for (int k = 0; k < arrayExpanded[j].length; k++) {
            if (j != k && arrayExpanded[j][k] == 1) {
              newArray[i][k] = 0;
            }
          }
        }
      }
    }
    return newArray;
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
   * @param array Internal representation of the Poset
   * @return Original array updated in place
   * @throws InvalidPoset Thrown if transitive expansion is not possible
   */
  private int[][] transitiveExpansion(int[][] array) {
    //initialisation
    AuxRowForTransitiveExpansion[] rows = new AuxRowForTransitiveExpansion[array.length];
    for (int i = 0; i < array.length; i++) {
      rows[i] = new AuxRowForTransitiveExpansion(array[i]);
    }

    //register subscribers
    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < array[i].length; j++) {
        if (array[i][j] == 1 && i != j) {
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
      array[i] = rows[i].row;
    }

    //check that antisymmetry law is not violated
    try {
      checkReflexivityAndAntiSymmetryLaws(array);
    } catch (PosetException e) {
      throw new InvalidPoset();
    }

    return array;
  }

  /**
   * This algorithm consists in sorting the rows by the sum of its elements (as we use 1s and 0s
   * to represent the binary relations, the sum of the elements of each row is equivalent to counting
   * the number of binary relations of each element)
   *
   * @return Array of sorted labels
   */
  public int[] sort() {
    //initialisation
    AuxRowForSort[] rows = new AuxRowForSort[arrayExpanded.length];
    for (int i = 0; i < arrayExpanded.length; i++) {
      rows[i] = new AuxRowForSort(arrayExpanded[i], i);
    }

    Arrays.sort(rows, (o1, o2) -> o2.numberOfBinaryRelations - o1.numberOfBinaryRelations);

    return Arrays.stream(rows).mapToInt(row -> row.label).toArray();
  }

  /**
   * Auxiliary object to do the topological sort of the poset
   */
  static class AuxRowForSort {
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
  static class AuxRowForTransitiveExpansion {

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
