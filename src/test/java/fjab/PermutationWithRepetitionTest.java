package fjab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static fjab.PermutationWithRepetition.allPermutations;
import static fjab.PermutationWithRepetition.randomPermutation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

class PermutationWithRepetitionTest {

  @Nested
  @DisplayName("calculation of all permutations")
  class AllPermutations {
    @Test
    @DisplayName("n=2, r=5")
    public void testPermutation2_5() {
      List<List<Integer>> result = allPermutations(Arrays.asList(0, 1), 5);
      assertEquals(32, result.size());
    }

    @Test
    @DisplayName("n=2, r=2")
    public void testPermutation2_2() {
      List<List<Integer>> result = allPermutations(Arrays.asList(0, 1), 2);

      assertEquals(4, result.size());
      assertThat(Arrays.asList(Arrays.asList(0, 0), Arrays.asList(1, 0), Arrays.asList(0, 1), Arrays.asList(1, 1))).containsExactlyInAnyOrderElementsOf(result);
    }
  }

  @Nested
  @DisplayName("calculation of a random permutation")
  class RandomPermutation {
    @Test
    @DisplayName("n=2, r=5")
    public void testPermutation2_5() {
      List<Integer> result = randomPermutation(Arrays.asList(0, 1), 5);
      assertThat(allPermutations(Arrays.asList(0, 1), 5)).contains(result);
    }

    @Test
    @DisplayName("n=2, r=2")
    public void testPermutation2_2() {
      List<Integer> result = randomPermutation(Arrays.asList(0, 1), 2);
      assertThat(allPermutations(Arrays.asList(0, 1), 2)).contains(result);
    }
  }


}