package fjab;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fjab.Util.isSquareMatrix;

public class Poset2<E> extends AbstractSet<E> {

  private final Poset internalPoset;
  private final List<E> sortedElements;

  public Poset2(List<E> unsafeElements, int[][] binaryRelations) {
    if(unsafeElements.size() != binaryRelations.length || !isSquareMatrix(binaryRelations)){
      throw new IllegalArgumentException("there must be NxN binary relations, where N is the number of elements");
    }
    if(new HashSet<E>(unsafeElements).size() != unsafeElements.size()) {
      throw new IllegalArgumentException("the list of elements cannot contain duplicates");
    }

    List<E> elements = List.copyOf(unsafeElements);
    this.internalPoset = new Poset(Util.arrayDeepCopy(binaryRelations));
    this.sortedElements = IntStream.of(internalPoset.sort()).mapToObj(elements::get).collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Poset2<?> poset2 = (Poset2<?>) o;
    return internalPoset.equals(poset2.internalPoset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), internalPoset);
  }

  public int[][] getExpandedBinaryRelations() {
    return internalPoset.getExpandedArray();
  }
  public int[][] getReducedBinaryRelations() {
    return internalPoset.getReducedArray();
  }

  private static UnsupportedOperationException uoe() { return new UnsupportedOperationException(); }
  // all mutating methods throw UnsupportedOperationException
  @Override public boolean add(E e) { throw uoe(); }
  @Override public boolean addAll(Collection<? extends E> c) { throw uoe(); }
  @Override public void    clear() { throw uoe(); }
  @Override public boolean remove(Object o) { throw uoe(); }
  @Override public boolean removeAll(Collection<?> c) { throw uoe(); }
  @Override public boolean removeIf(Predicate<? super E> filter) { throw uoe(); }
  @Override public boolean retainAll(Collection<?> c) { throw uoe(); }

  @Override
  public Iterator<E> iterator() {
    return sortedElements.iterator();
  }

  @Override
  public int size() {
    return sortedElements.size();
  }
}
