package fjab.poset;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fjab.poset.PosetUtil.validateArguments;

/**
 * A Poset (https://en.wikipedia.org/wiki/Partially_ordered_set) is a set of elements with a binary relation
 * defined between them (not necessarily between all of them) that must satisfy the following rules
 * - reflexivity
 * - antisymmetry
 * - transitivity
 */
public class Poset<E> extends AbstractSet<E> {

  private final List<E> sortedElements;
  private final int[][] transitiveExpansion;
  private final int[][] transitiveReduction;

  /**
   * Creates an immutable Set with the Poset semantics
   * Note: elements of the set can still be mutated (that will not affect topological sort though)
   */
  public Poset(List<E> elements, int[][] incidenceMatrix) {
    validateArguments(elements, incidenceMatrix);

    this.transitiveExpansion = PosetUtil.transitiveExpansion(incidenceMatrix);
    this.transitiveReduction = PosetUtil.transitiveReduction(transitiveExpansion);
    this.sortedElements = IntStream.of(Util.sort(transitiveExpansion)).mapToObj(elements::get).collect(Collectors.toList());
  }

  /**
   * Two posets are equal if they have the same elements and the same transitive expansion of the incidence matrix
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Poset<?> other = (Poset<?>) o;
    return Arrays.deepEquals(this.transitiveExpansion, other.transitiveExpansion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), Arrays.deepHashCode(transitiveExpansion));
  }

  /**
   * All default implementations in AbstractSet are based on iterator()
   * Therefore, overriding this method and size() is enough to have a working implementation of a Set
   */
  @Override
  public Iterator<E> iterator() {

    //We define our own iterator instead of using sortedElements.iterator() as the latter allows mutations
    //via Iterator.remove()
    return new Iterator<>() {
      int counter = 0;

      @Override
      public boolean hasNext() {
        return counter < sortedElements.size();
      }

      @Override
      public E next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return sortedElements.get(counter++);
      }
    };
  }

  @Override
  public int size() {
    return sortedElements.size();
  }

  public int[][] getTransitiveExpansion() {
    return transitiveExpansion;
  }

  public int[][] getTransitiveReduction() {
    return transitiveReduction;
  }

  /**
   * The Poset is characterised by its elements and the transitive expansion of the incidence matrix
   * The elements of the set are printed out in topological order
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int[] ints : transitiveExpansion) {
      for (int anInt : ints) {
        sb.append(" ").append(anInt);
      }
      sb.append("\n");
    }
    return sortedElements + "\n" + sb.toString();
  }
}
