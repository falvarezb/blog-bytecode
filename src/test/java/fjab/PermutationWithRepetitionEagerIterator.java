package fjab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Iterator over samples of r elements of type T taken from a population of n elements with replacement.
 * The total number of samples is n^r
 *
 * Samples are calculated eagerly and kept in a list
 */
public class PermutationWithRepetitionEagerIterator<T> implements Iterator<List<T>>{

  private final List<T> population;
  private final int r;
  private final int n;
  private final long numPermutations;
  private final List<List<T>> permutations;
  private int counter;

  public PermutationWithRepetitionEagerIterator(List<T> population, int r) {
    this.population = population;
    this.r = r;
    n = population.size();
    numPermutations = (long) Math.pow(n, r);
    permutations = permutationsWithRepetition(population, r);
  }

  @Override
  public boolean hasNext() {
    return counter < numPermutations;
  }

  @Override
  public List<T> next() {
    return permutations.get(counter++);
  }

  public static <T> List<List<T>> permutationsWithRepetition(List<T> population, int r) {
    if (r == 0) {
      return new ArrayList<>() {{
        add(new ArrayList<>());
      }};
    }

    List<List<T>> result = new ArrayList<>();

    for (T elem : population) {
      for (List<T> list : permutationsWithRepetition(population, r - 1)) {
        list.add(elem);
        result.add(list);
      }
    }
    return result;
  }
}
