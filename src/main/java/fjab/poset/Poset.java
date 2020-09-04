package fjab.poset;

import java.util.Arrays;

import static fjab.poset.Util.arrayDeepCopy;

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
    PosetUtil.validateArray(array);
    this.expandedArray = PosetUtil.transitiveExpansion(array);
    this.reducedArray = PosetUtil.transitiveReduction(expandedArray);

    this.numberOfExpandedBinaryRelations = Util.sum(this.expandedArray);
    this.numberOfReducedBinaryRelations = Util.sum(this.reducedArray);
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


}
