package fjab.poset;

import fjab.poset.error.AntiSymmetryException;
import fjab.poset.error.InvalidPosetException;
import fjab.poset.error.PosetException;
import fjab.poset.error.ReflexivityException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static fjab.poset.Util.arrayDeepCopy;
import static fjab.poset.Util.isSquareMatrix;

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

  private final int[][] expandedArray;
  private final int[][] reducedArray;
  private final int numberOfExpandedBinaryRelations;
  private final int numberOfReducedBinaryRelations;

  public Poset(int[][] unsafeArray) {
    int[][] array = arrayDeepCopy(unsafeArray);
    validateArray(array);
    this.expandedArray = transitiveExpansion(array);
    this.reducedArray = transitiveReduction();

    this.numberOfExpandedBinaryRelations = Util.sum(this.expandedArray);
    this.numberOfReducedBinaryRelations = Util.sum(this.reducedArray);
  }

  private static void validateArray(int[][] array) {
    if (Arrays.stream(array).flatMapToInt(Arrays::stream).filter(j -> j != 0 && j != 1).count() > 0) {
      throw new IllegalArgumentException("The binary relations representation must contain the numbers 1 and 0 only");
    }
    if(!isSquareMatrix(array)) {
      throw new IllegalArgumentException("the array must be a NxN matrix, where N is the number of elements");
    }

    checkReflexivityAndAntiSymmetryLaws(array);
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

  public int[][] getExpandedArray() {
    return expandedArray;
  }

  public int[][] getReducedArray() {
    return reducedArray;
  }

  public int getNumberOfReducedBinaryRelations() {
    return numberOfReducedBinaryRelations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Poset poset = (Poset) o;
    return Arrays.deepEquals(expandedArray, poset.expandedArray);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(expandedArray);
  }

  public int getNumberOfExpandedBinaryRelations() {
    return numberOfExpandedBinaryRelations;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int[] ints : expandedArray) {
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
   * @throws InvalidPosetException Thrown if transitive expansion is not possible
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
      throw new InvalidPosetException();
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
    AuxRowForSort[] rows = new AuxRowForSort[expandedArray.length];
    for (int i = 0; i < expandedArray.length; i++) {
      rows[i] = new AuxRowForSort(expandedArray[i], i);
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
