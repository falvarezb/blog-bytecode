package fjab;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class PermutationWithRepetitionArbitrary {

  public static <T> Arbitrary<List<T>> permutationWithRepetitionArbitrary(List<T> values, int repetitions) {
    return fromGenerators(
      permutationWithRepetitionRandomGenerator(values, repetitions),
      max -> permutationWithRepetitionExhaustiveGenerator(values, max, repetitions),
      EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(values))
    );
  }

  public static <T> RandomGenerator<List<T>> permutationWithRepetitionRandomGenerator(List<T> values, int repetitions) {
    return (random) -> {
      List<T> permutation = randomPermutationWithRepetition(values, repetitions, random);
      return Shrinkable.unshrinkable(permutation);
    };
  }

  public static <T> List<T> randomPermutationWithRepetition(List<T> values, int repetitions, Random random) {
    return random
      .ints(0,values.size())
      .mapToObj(values::get).limit(repetitions)
      .collect(Collectors.toList());
  }

  public static <T> Optional<ExhaustiveGenerator<List<T>>> permutationWithRepetitionExhaustiveGenerator(List<T> values, long maxNumberOfSamples, int repetitions) {
    Optional<Long> optionalMaxCount = PermutationWithRepetitionExhaustiveGenerator.calculateMaxCount(values, maxNumberOfSamples, repetitions);
    return optionalMaxCount.map((maxCount) -> new PermutationWithRepetitionExhaustiveGenerator<>(values, maxCount, repetitions));
  }

  static class PermutationWithRepetitionExhaustiveGenerator<T> implements ExhaustiveGenerator<List<T>> {
    private final List<T> values;
    private final Long maxCount;
    private final int repetitions;

    public PermutationWithRepetitionExhaustiveGenerator(List<T> values, Long maxCount, int repetitions) {
      this.values = values;
      this.maxCount = maxCount;
      this.repetitions = repetitions;
    }

    static <T> Optional<Long> calculateMaxCount(List<T> values, long maxNumberOfSamples, int repetitions) {
      try {
        long choices = (long) Math.pow(values.size(), repetitions);
        return choices <= maxNumberOfSamples && choices >= 0L ? Optional.of(choices) : Optional.empty();
      } catch (ArithmeticException var5) {
        return Optional.empty();
      }
    }

    public long maxCount() {
      return this.maxCount;
    }

    public Iterator<List<T>> iterator() {
      return new PermutationWithRepetitionIterator<>(this.values, this.repetitions);
    }
  }

  public static class PermutationWithRepetitionIterator<T> implements Iterator<List<T>> {

    private final List<T> values;
    private final int repetitions;
    private int counter;
    private final long numPermutations;

    public PermutationWithRepetitionIterator(List<T> values, int repetitions) {
      if (repetitions < Character.MIN_RADIX || repetitions > Character.MAX_RADIX) {
        throw new IllegalArgumentException(Character.MIN_RADIX + " < " + Character.MAX_RADIX);
      }
      this.values = values;
      this.repetitions = repetitions;
      numPermutations = (long) Math.pow(values.size(), repetitions);
    }

    @Override
    public boolean hasNext() {
      return counter < numPermutations;
    }

    @Override
    public List<T> next() {
      String permutation = String.format("%"+repetitions+"s" ,Integer.toString(counter++, values.size())).replace(" ","0");
      return permutation.chars()
        .mapToObj(c -> Integer.valueOf(String.valueOf((char) c)))
        .map(values::get)
        .collect(Collectors.toList());
    }
  }

  private static <T> Arbitrary<T> fromGenerators(
    RandomGenerator<T> randomGenerator,
    Function<Long, Optional<ExhaustiveGenerator<T>>> exhaustiveGeneratorFunction,
    final EdgeCases<T> edgeCases
  ) {
    return new Arbitrary<>() {
      @Override
      public RandomGenerator<T> generator(int tries) {
        return randomGenerator;
      }

      @Override
      public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
        return exhaustiveGeneratorFunction.apply(maxNumberOfSamples);
      }

      @Override
      public EdgeCases<T> edgeCases() {
        return edgeCases;
      }
    };
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
