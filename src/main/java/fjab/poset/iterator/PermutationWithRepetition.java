package fjab.poset.iterator;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

/**
 * Methods to obtain permutations of r elements of type T taken from a population of n elements with replacement.
 * The total number of samples is n^r
 *
 * For instance, given a population of n=4 elements:
 *
 * [a,b,c]
 *
 * all possible permutations of r=2 elements with replacement are:
 *
 * [a,a], [a,b], [a,c],
 * [b,a], [b,b], [b,c],
 * [c,a], [c,b], [c,c]
 */
public class PermutationWithRepetition {

  public static <T> List<List<T>> allPermutations(List<T> population, int r) {
    if (r == 0) {
      return singletonList(new ArrayList<>());
    }

    List<List<T>> result = new ArrayList<>();

    for (T elem : population) {
      for (List<T> list : allPermutations(population, r - 1)) {
        list.add(elem);
        result.add(list);
      }
    }
    return result;
  }

  public static <T> List<T> randomPermutation(List<T> population, int r) {
    return randomPermutation(population, r, new Random());
  }

  public static <T> List<T> randomPermutation(List<T> population, int r, Random random) {
    return random
      .ints(0,population.size())
      .mapToObj(population::get).limit(r)
      .collect(Collectors.toList());
  }


}
