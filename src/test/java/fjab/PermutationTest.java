package fjab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fjab.Permutation.permutationsWithRepetition;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PermutationTest {

  @Nested
  @DisplayName("recursive implementation")
  class RecursiveImplementation {
    @Test
    @DisplayName("n=2, r=5")
    public void testPermutation2_5() {
      List<List<Integer>> result = permutationsWithRepetition(new int[]{0, 1}, 5);

      assertEquals(32, result.size());
    }

    @Test
    @DisplayName("n=2, r=2")
    public void testPermutation2_2() {
      List<List<Integer>> result = permutationsWithRepetition(new int[]{0, 1}, 2);

      assertEquals(4, result.size());
      assertEquals(new ArrayList<List<Integer>>(){{
        add(Arrays.asList(0, 0));
        add(Arrays.asList(1, 0));
        add(Arrays.asList(0, 1));
        add(Arrays.asList(1, 1));
      }}, result);
    }
  }

  @Nested
  @DisplayName("stream implementation")
  class StreamImplementation {

    @Test
    @DisplayName("n=2, r=5")
    public void permutationStream2_5() {
      List<List<Integer>> result = new Permutation(5).stream().collect(Collectors.toList());
      assertEquals(32, result.size());
    }

    @Test
    @DisplayName("n=2, r=2")
    public void permutationStream2_2() {
      List<List<Integer>> result = new Permutation(2).stream().collect(Collectors.toList());
      assertEquals(new ArrayList<List<Integer>>(){{
        add(Arrays.asList(0, 0));
        add(Arrays.asList(0, 1));
        add(Arrays.asList(1, 0));
        add(Arrays.asList(1, 1));
      }}, result);
    }
  }



}