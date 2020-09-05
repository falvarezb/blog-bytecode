package fjab.poset;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
public class Poset<E> extends AbstractSet<E> {

  private final List<E> sortedElements;
  private final int[][] expandedArray;
  private final int[][] reducedArray;
  private final int numberOfExpandedBinaryRelations;
  private final int numberOfReducedBinaryRelations;

  public Poset(List<E> unsafeList, int[][] unsafeBinaryRelations) {
    //as far as the Poset is concerned, we just need to preserve the immutability of the list, not of the
    //elements themselves
    List<E> elements = List.copyOf(unsafeList);
    int[][] binaryRelations = Util.arrayDeepCopy(unsafeBinaryRelations);
    PosetUtil.validateArguments(elements, binaryRelations);

    this.expandedArray = PosetUtil.transitiveExpansion(binaryRelations);
    this.reducedArray = PosetUtil.transitiveReduction(expandedArray);
    this.numberOfExpandedBinaryRelations = Util.sum(this.expandedArray);
    this.numberOfReducedBinaryRelations = Util.sum(this.reducedArray);
    this.sortedElements = IntStream.of(PosetUtil.sort(expandedArray)).mapToObj(elements::get).collect(Collectors.toList());
  }

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

  public int[][] getExpandedBinaryRelations() {
    return expandedArray;
  }
  public int[][] getReducedBinaryRelations() {
    return reducedArray;
  }

  // all mutating methods throw UnsupportedOperationException
  @Override public boolean add(E e) { throw PosetUtil.uoe(); }
  @Override public boolean addAll(Collection<? extends E> c) { throw PosetUtil.uoe(); }
  @Override public void    clear() { throw PosetUtil.uoe(); }
  @Override public boolean remove(Object o) { throw PosetUtil.uoe(); }
  @Override public boolean removeAll(Collection<?> c) { throw PosetUtil.uoe(); }
  @Override public boolean removeIf(Predicate<? super E> filter) { throw PosetUtil.uoe(); }
  @Override public boolean retainAll(Collection<?> c) { throw PosetUtil.uoe(); }

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
