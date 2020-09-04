package fjab.poset.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator over permutations of r elements of type T taken from a population of n elements with replacement.
 * The total number of permutations is n^r
 *
 * Permutations are calculated eagerly and kept in a list
 */
public class EagerPermutationWithRepetitionIterator<T> implements Iterator<List<T>>{

  private final long numPermutations;
  private final List<List<T>> permutations;
  private int counter;

  public EagerPermutationWithRepetitionIterator(List<T> population, int r) {
    int n = population.size();
    numPermutations = (long) Math.pow(n, r);
    permutations = PermutationWithRepetition.allPermutations(population, r);
  }

  @Override
  public boolean hasNext() {
    return counter < numPermutations;
  }

  @Override
  public List<T> next() {
    if (!this.hasNext()) {
      throw new NoSuchElementException();
    }
    return permutations.get(counter++);
  }
}
