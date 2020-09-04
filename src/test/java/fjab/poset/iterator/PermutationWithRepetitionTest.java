package fjab.poset.iterator;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fjab.poset.iterator.PermutationWithRepetition.allPermutations;
import static fjab.poset.iterator.PermutationWithRepetition.randomPermutation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Label("PermutationWithRepetitionTest")
class PermutationWithRepetitionTest {

  @Group
  @Label("test of 'allPermutations'")
  class AllPermutations {
    @Property
    @Label("check of the amount of permutations for different combinations of 'n' and 'r'")
    public void testAmountPermutations(@ForAll @IntRange(min=1, max=5) int n, @ForAll @IntRange(min=1, max=5) int r) {
      List<Integer> population = IntStream.range(0,n).boxed().collect(Collectors.toList());
      List<List<Integer>> result = allPermutations(population, r);
      assertEquals((int) Math.pow(n, r), result.size());
    }

    @Example
    @Label("check of the permutations corresponding to n=2, r=2")
    public void testPermutation2_2() {
      List<List<Integer>> result = allPermutations(Arrays.asList(0, 1), 2);

      assertEquals(4, result.size());
      assertThat(Arrays.asList(Arrays.asList(0, 0), Arrays.asList(1, 0), Arrays.asList(0, 1), Arrays.asList(1, 1))).containsExactlyInAnyOrderElementsOf(result);
    }
  }

  @Group
  @Label("test of 'randomPermutation'")
  class RandomPermutation {
    @Property
    @Label("checking 2^2 random permutations for n=2, r=2")
    public void testPermutation2_2(@ForAll @IntRange(min=1, max=4) int repetitions) {
      List<Integer> population = Arrays.asList(0, 1);
      int r = 2;
      assertThat(allPermutations(population, r)).contains(randomPermutation(population, r));
    }

    @Property
    @Label("checking 2^5 random permutations for n=2, r=5")
    public void testPermutation2_5(@ForAll @IntRange(min=1, max=32) int repetitions) {
      List<Integer> population = Arrays.asList(0, 1);
      int r = 5;
      assertThat(allPermutations(population, r)).contains(randomPermutation(population, r));
    }
  }


}