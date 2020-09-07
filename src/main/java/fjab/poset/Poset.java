package fjab.poset;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A Poset (https://en.wikipedia.org/wiki/Partially_ordered_set) is a set of elements with a binary relation
 * defined between them (not necessarily between all of them) that must satisfy the following rules
 * - reflexivity
 * - antisymmetry
 * - transitivity
 *
 * A binary relation with these properties is called "partial order" and is denoted by ≤.
 * The binary relation between the different elements can be represented as a square binary matrix
 * (the elements are ones and zeros) where rows and columns are indexed by the elements of the set.
 * Such a matrix is also called incidence matrix (https://en.wikipedia.org/wiki/Incidence_matrix), where 1 means
 * that 2 elements are related and 0 means that they are not.
 *
 *
 * For instance, given a set of 4 elements {a,b,c,d} and this incidence matrix:
 * 1 1 0 0
 * 0 1 1 0
 * 0 0 1 0
 * 1 0 0 1
 *
 * these are the binary relations:
 * a ≤ a
 * a ≤ b
 * b ≤ b
 * b ≤ c
 * c ≤ c
 * d ≤ a
 * d ≤ d
 *
 *
 * Because of the:
 * - reflexivity property, all elements in the main diagonal must be 1.
 * - antisymmetry property, elements in symmetric positions with respect to the main diagonal cannot be both 1.
 *
 * The above example is the so-called transitive reduction (https://en.wikipedia.org/wiki/Transitive_reduction), a
 * simplified version of the incidence matrix without the transitive relations.
 *
 * After applying the transitive property (transitive expansion), the above example becomes:
 *
 * 1 1 1 0
 * 0 1 1 0
 * 0 0 1 0
 * 1 1 1 1
 *
 *
 * A Poset can be interpreted as a directed acyclic graph (DAG) in which the shortest distance between
 * all pairs of elements that are connected is always 1 (because of the transitivity property).
 * Therefore, as a DAG, it is possible to compute its topological sort.
 * According to https://en.wikipedia.org/wiki/Topological_sorting
 *
 * "topological sort or topological ordering of a directed graph is a linear ordering of its vertices
 * such that for every directed edge uv from vertex u to vertex v, u comes before v in the ordering"
 *
 * In our example, if each element of the poset is identified by its position in the matrix representation,
 * the topological sort is: [d,a,b,c].
 * The topological sort can present ambiguity for elements that are not connected between them.
 *
 * It's worth noting that elements with the same number of ≤ cannot be connected (if a ≤ b, a will have at
 * least one more ≤ than b)
 */
public class Poset<E> extends AbstractSet<E> {

  private final List<E> sortedElements;
  private final int[][] expandedArray;
  private final int[][] reducedArray;
  private final int numberOfExpandedBinaryRelations;
  private final int numberOfReducedBinaryRelations;

  /**
   * Creates an immutable Set with the Poset semantics
   * Note: elements of the set can still be mutated (that will not affect topological sort though)
   */
  public Poset(List<E> elements, int[][] incidenceMatrix) {
    PosetUtil.validateArguments(elements, incidenceMatrix);

    this.expandedArray = PosetUtil.transitiveExpansion(incidenceMatrix);
    this.reducedArray = PosetUtil.transitiveReduction(expandedArray);
    this.numberOfExpandedBinaryRelations = Util.sum(this.expandedArray);
    this.numberOfReducedBinaryRelations = Util.sum(this.reducedArray);
    this.sortedElements = IntStream.of(Util.sort(expandedArray)).mapToObj(elements::get).collect(Collectors.toList());
  }

  /**
   * The Poset is characterised by its elements and the transitive expansion of the incidence matrix
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Poset<?> other = (Poset<?>) o;
    return Arrays.deepEquals(this.expandedArray, other.expandedArray);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), Arrays.deepHashCode(expandedArray));
  }

  /**
   * All default implementations in AbstractSet are based on iterator()
   * Therefore, overriding this method and size() is enough to have a working implementation of a Set
   */
  //TODO: change implementation to remove iterator.remove()
  @Override
  public Iterator<E> iterator() {
    return sortedElements.iterator();
  }

  @Override
  public int size() {
    return sortedElements.size();
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

  public int getNumberOfExpandedBinaryRelations() {
    return numberOfExpandedBinaryRelations;
  }

  /**
   * The Poset is characterised by its elements and the transitive expansion of the incidence matrix
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int[] ints : expandedArray) {
      for (int anInt : ints) {
        sb.append(" ").append(anInt);
      }
      sb.append("\n");
    }
    return sortedElements + "\n" + sb.toString();
  }
}
