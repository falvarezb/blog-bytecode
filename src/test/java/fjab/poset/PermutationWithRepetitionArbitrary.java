package fjab.poset;

import fjab.poset.iterator.LazyPermutationWithRepetitionIterator;
import fjab.poset.iterator.PermutationWithRepetition;
import net.jqwik.api.*;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Arbitrary permutations of r elements of type T taken from a population of n elements with replacement
 */
public class PermutationWithRepetitionArbitrary<T> implements Arbitrary<List<T>>{

  private final List<T> population;
  private final int r;

  public PermutationWithRepetitionArbitrary(List<T> population, int r) {
    this.population = population;
    this.r = r;
  }

  @Override
  public RandomGenerator<List<T>> generator(int genSize) {
    return (random) -> {
      List<T> permutation = PermutationWithRepetition.randomPermutation(population, r, random);
      return Shrinkable.unshrinkable(permutation);
    };
  }

  @Override
  public Optional<ExhaustiveGenerator<List<T>>> exhaustive(long maxNumberOfSamples) {
    long numPermutations = (long) Math.pow(population.size(), r);
    Optional<Long> optionalMaxCount = numPermutations <= maxNumberOfSamples ? Optional.of(numPermutations) : Optional.empty();
    return optionalMaxCount.map((maxCount) -> new PermutationWithRepetitionExhaustiveGenerator<>(population, maxCount, r));
  }

  @Override
  public EdgeCases<List<T>> edgeCases() {
    return EdgeCases.fromSupplier(() -> {throw new UnsupportedOperationException("There are no edge cases. Property should be configured with 'EdgeCasesMode.NONE'");});
  }

  static class PermutationWithRepetitionExhaustiveGenerator<T> implements ExhaustiveGenerator<List<T>> {
    private final List<T> population;
    private final Long maxCount;
    private final int r;

    public PermutationWithRepetitionExhaustiveGenerator(List<T> population, Long maxCount, int r) {
      this.population = population;
      this.maxCount = maxCount;
      this.r = r;
    }

    @Override
    public long maxCount() {
      return this.maxCount;
    }

    @Override
    public Iterator<List<T>> iterator() {
      return new LazyPermutationWithRepetitionIterator<>(this.population, this.r);
    }
  }

}
