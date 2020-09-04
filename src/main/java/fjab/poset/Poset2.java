package fjab.poset;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Poset2<E> extends AbstractSet<E> {

  //private final Poset internalPoset;
  private final List<E> sortedElements;

  private final int[][] expandedArray;
  private final int[][] reducedArray;
  private final int numberOfExpandedBinaryRelations;
  private final int numberOfReducedBinaryRelations;

  public Poset2(List<E> unsafeList, int[][] unsafeBinaryRelations) {
    //as far as the Poset is concerned, we just need to preserve the immutability of the list, not of the
    //elements themselves
    List<E> elements = List.copyOf(unsafeList);
    int[][] binaryRelations = Util.arrayDeepCopy(unsafeBinaryRelations);
    PosetUtil.validateArguments(elements, binaryRelations);
    PosetUtil.validateArray(binaryRelations);

    this.expandedArray = PosetUtil.transitiveExpansion(binaryRelations);
    this.reducedArray = PosetUtil.transitiveReduction(expandedArray);
    this.numberOfExpandedBinaryRelations = Util.sum(this.expandedArray);
    this.numberOfReducedBinaryRelations = Util.sum(this.reducedArray);

    //this.internalPoset = new Poset(binaryRelations);
    this.sortedElements = IntStream.of(PosetUtil.sort(expandedArray)).mapToObj(elements::get).collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Poset2<?> poset2 = (Poset2<?>) o;
    return Arrays.deepEquals(expandedArray, poset2.expandedArray);
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
    return sb.toString();
  }
}
