package fjab;

import net.jqwik.api.Arbitrary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class PermutationWithRepetitionArbitraryTest {

  @Nested
  class RandomGeneratorTest {

    @Test
    @DisplayName("random sample: n=2, r=2")
    public void randomPermutation_2_2() {

      //given
      List<String> population = new ArrayList<>() {{
        add("a");
        add("b");
      }};
      int r = 2;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allPossiblePermutations = Arrays.asList("aa", "ab", "ba", "bb");

      //when
      Stream<String> someSamples = arbitrary.sampleStream().limit(20).map(Util::listToString);

      //then
      assertThat(someSamples).allMatch(allPossiblePermutations::contains);
    }

    @Test
    @DisplayName("random sample: n=2, r=3")
    public void randomPermutation_2_3() {

      //given
      List<String> population = new ArrayList<>() {{
        add("a");
        add("b");
      }};
      int r = 3;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allPossiblePermutations = Arrays.asList(
        "aaa", "aab", "aba", "abb",
        "baa", "bab", "bba", "bbb"
      );

      //when
      Stream<String> someSamples = arbitrary.sampleStream().limit(100).map(Util::listToString);

      //then
      assertThat(someSamples).allMatch(allPossiblePermutations::contains);
    }


    @Test
    @DisplayName("random sample: n=3, r=2")
    public void randomPermutation_3_2() {

      //given
      List<String> population = new ArrayList<>() {{
        add("a");
        add("b");
        add("c");
      }};
      int r = 2;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allPossiblePermutations = Arrays.asList(
        "aa","ab","ac",
        "ba", "bb", "bc",
        "ca", "cb", "cc"
      );

      //when
      Stream<String> someSamples = arbitrary.sampleStream().limit(100).map(Util::listToString);

      //then
      assertThat(someSamples).allMatch(allPossiblePermutations::contains);
    }

    @Test
    @DisplayName("random sample: n=3, r=3")
    public void randomPermutation_3_3() {

      //given
      List<String> population = new ArrayList<>() {{
        add("a");
        add("b");
        add("c");
      }};
      int r = 3;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allPossiblePermutations = Arrays.asList(
        "aaa", "aab", "aac", "aba", "abb", "abc", "aca", "acb", "acc",
        "baa", "bab", "bac", "bba", "bbb", "bbc", "bca", "bcb", "bcc",
        "caa", "cab", "cac", "cba", "cbb", "cbc", "cca", "ccb", "ccc"
      );

      //when
      Stream<String> someSamples = arbitrary.sampleStream().limit(300).map(Util::listToString);

      //then
      assertThat(someSamples).allMatch(allPossiblePermutations::contains);
    }
  }

  @Nested
  //all samples of r elements taken from a population of n elements with replacement
  class ExhaustiveGeneratorTest {
    @Test
    @DisplayName("all samples: n=2, r=2")
    public void allPermutations_2_2() {

      //given
      List<String> population = Arrays.asList("a", "b");
      int r = 2;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allPossiblePermutations = Arrays.asList("aa", "ab", "ba", "bb");

      //when
      List<String> allValues = arbitrary.allValues().get().map(Util::listToString).collect(toList());

      //then
      assertThat(allValues).containsExactlyInAnyOrderElementsOf(allPossiblePermutations);
    }

    @Test
    @DisplayName("all samples: n=2, r=3")
    public void allPermutations_2_3() {

      //given
      List<String> population = Arrays.asList("a", "b");
      int r = 3;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allPossiblePermutations = Arrays.asList(
        "aaa", "aab", "aba", "abb",
        "baa", "bab", "bba", "bbb"
      );

      //when
      List<String> allValues = arbitrary.allValues().get().map(Util::listToString).collect(toList());

      //then
      assertThat(allValues).containsExactlyInAnyOrderElementsOf(allPossiblePermutations);
    }

    @Test
    @DisplayName("all samples: n=3, r=2")
    public void allPermutations_3_2() {

      //given
      List<String> population = Arrays.asList("a", "b", "c");
      int r = 2;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allPossiblePermutations = Arrays.asList(
        "aa","ab","ac",
        "ba", "bb", "bc",
        "ca", "cb", "cc"
      );

      //when
      List<String> allValues = arbitrary.allValues().get().map(Util::listToString).collect(toList());

      //then
      assertThat(allValues).containsExactlyInAnyOrderElementsOf(allPossiblePermutations);
    }

    @Test
    @DisplayName("all samples: n=3, r=3")
    public void allPermutations_3_3() {

      //given
      List<String> population = Arrays.asList("a", "b", "c");
      int r = 3;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allPossiblePermutations = Arrays.asList(
        "aaa", "aab", "aac", "aba", "abb", "abc", "aca", "acb", "acc",
        "baa", "bab", "bac", "bba", "bbb", "bbc", "bca", "bcb", "bcc",
        "caa", "cab", "cac", "cba", "cbb", "cbc", "cca", "ccb", "ccc"
      );

      //when
      List<String> allValues = arbitrary.allValues().get().map(Util::listToString).collect(toList());

      //then
      assertThat(allValues).containsExactlyInAnyOrderElementsOf(allPossiblePermutations);
    }
  }
}