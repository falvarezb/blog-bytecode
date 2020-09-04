package fjab.poset.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static fjab.poset.Util.*;

/**
 * Iterator over permutations of r elements of type T taken from a population of n elements with replacement.
 * The total number of permutations is n^r
 *
 * Permutations are calculated lazily based on a simple observation: all the permutations are given by the representation
 * in base 'n' of the numbers 0, 1, ..., n^r-1.
 *
 * For instance, given a list of 2 elements [0, 1] (n=2), all the permutation of 3 elements are:
 *
 * 0: 000
 * 1: 001
 * 2: 010
 * 3: 011
 * 4: 100
 * 5: 101
 * 6: 110
 * 7: 111
 *
 * Note: we can use numbers without loss of generality as we can always enumerate the elements of the given population.
 *
 * The representation in base 'n' of a number 'x' is calculated with the method provided by the API:
 * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#toString(int,int)
 *
 * As stated in the documentation of said method:
 * - If the radix is smaller than Character.MIN_RADIX or larger than Character.MAX_RADIX, then the radix 10 is used instead
 * - The following ASCII characters are used as digits: 0123456789abcdefghijklmnopqrstuvwxyz
 *
 */
public class LazyPermutationWithRepetitionIterator<T> implements Iterator<List<T>>{

  private final List<T> population;
  private final int r;
  private final int n;
  private final long numPermutations;
  private int counter;

  private static final String digits = "0123456789abcdefghijklmnopqrstuvwxyz";

  public LazyPermutationWithRepetitionIterator(List<T> population, int r) {
    this.population = population;
    this.r = r;
    n = population.size();

    if (n < Character.MIN_RADIX || n > Character.MAX_RADIX) {
      throw new IllegalArgumentException(String.format("%d <= n <= %d", Character.MIN_RADIX, Character.MAX_RADIX));
    }

    numPermutations = (long) Math.pow(n, r);
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
    String permutation = leftPad(Integer.toString(counter++, n), r, '0');
    return stringToStream(permutation).map(digits::indexOf).map(population::get).collect(Collectors.toList());
  }



}
