package fjab;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Arbitrary sample of r elements of type T taken from a population of n elements with replacement
 */
public class PermutationWithRepetitionArbitrary<T> implements Arbitrary<List<T>>{

  private final List<T> population;
  private final int r;

  public PermutationWithRepetitionArbitrary(List<T> population, int r) {
    this.population = population;
    this.r = r;
  }

  private List<T> randomPermutationWithRepetition(List<T> values, int repetitions, Random random) {
    return random
      .ints(0,values.size())
      .mapToObj(values::get).limit(repetitions)
      .collect(Collectors.toList());
  }

  @Override
  public RandomGenerator<List<T>> generator(int genSize) {
    return (random) -> {
      List<T> permutation = randomPermutationWithRepetition(population, r, random);
      return Shrinkable.unshrinkable(permutation);
    };
  }

  @Override
  public Optional<ExhaustiveGenerator<List<T>>> exhaustive(long maxNumberOfSamples) {
    Optional<Long> optionalMaxCount = PermutationWithRepetitionExhaustiveGenerator.calculateMaxCount(population, maxNumberOfSamples, r);
    return optionalMaxCount.map((maxCount) -> new PermutationWithRepetitionExhaustiveGenerator<>(population, maxCount, r));
  }

  @Override
  public EdgeCases<List<T>> edgeCases() {
    return EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(population));
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

    static <T> Optional<Long> calculateMaxCount(List<T> population, long maxNumberOfSamples, int r) {
      try {
        long choices = (long) Math.pow(population.size(), r);
        return choices <= maxNumberOfSamples && choices >= 0L ? Optional.of(choices) : Optional.empty();
      } catch (ArithmeticException var5) {
        return Optional.empty();
      }
    }

    @Override
    public long maxCount() {
      return this.maxCount;
    }

    @Override
    public Iterator<List<T>> iterator() {
      return new PermutationWithRepetitionIterator<>(this.population, this.r);
    }
  }

  private static List<Integer> padToTheRight(List<Integer> ints, int length) {
    int numExtraElements = length - ints.size();
    ints.addAll(IntStream.range(0,numExtraElements).boxed().collect(toList()));
    return ints;
  }

  /**
   * leading digit is in the rightmost position
   * @param n
   * @return
   */
  public static List<Integer> baseNRepresentation(int n, int radix) {
    int q = n/radix;
    int r = n%radix;

    if(q == 0) {
      return new ArrayList<>(){{
        add(r);
      }};
    }
    else {
      return new ArrayList<>(){{
        add(r);
        addAll(baseNRepresentation(q, radix));
      }};
    }
  }

}
