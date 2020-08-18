package fjab;

import net.jqwik.api.*;
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

  static String fromListToString(List<String> list) {
    return list.stream().reduce("", (a, b) -> a + b);
  }

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
      List<String> allSamples = Arrays.asList("aa", "ab", "ba", "bb");

      //when
      Stream<String> sample = arbitrary.sampleStream().limit(20).map(PermutationWithRepetitionArbitraryTest::fromListToString);

      //then
      assertThat(sample).allMatch(allSamples::contains);
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
      List<String> allSamples = Arrays.asList(
        "aaa", "aab", "aba", "abb",
        "baa", "bab", "bba", "bbb"
      );

      //when
      Stream<String> sample = arbitrary.sampleStream().limit(100).map(PermutationWithRepetitionArbitraryTest::fromListToString);

      //then
      assertThat(sample).allMatch(allSamples::contains);
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
      List<String> allSamples = Arrays.asList(
        "aa","ab","ac",
        "ba", "bb", "bc",
        "ca", "cb", "cc"
      );

      //when
      Stream<String> sample = arbitrary.sampleStream().limit(100).map(PermutationWithRepetitionArbitraryTest::fromListToString);

      //then
      assertThat(sample).allMatch(allSamples::contains);
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
      List<String> allSamples = Arrays.asList(
        "aaa", "aab", "aac", "aba", "abb", "abc", "aca", "acb", "acc",
        "baa", "bab", "bac", "bba", "bbb", "bbc", "bca", "bcb", "bcc",
        "caa", "cab", "cac", "cba", "cbb", "cbc", "cca", "ccb", "ccc"
      );

      //when
      Stream<String> sample = arbitrary.sampleStream().limit(300).map(PermutationWithRepetitionArbitraryTest::fromListToString);

      //then
      assertThat(sample).allMatch(allSamples::contains);
    }
  }

  @Nested
  //all samples of r elements taken from a population of n elements with replacement
  class ExhaustiveGeneratorTest {
    @Test
    @DisplayName("all samples: n=2, r=2")
    public void allPermutations_2_2() {

      //given
      List<String> population = new ArrayList<>() {{
        add("a");
        add("b");
      }};
      int r = 2;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allSamples = Arrays.asList("aa", "ab", "ba", "bb");

      //when
      List<String> samples = arbitrary.allValues().get().map(PermutationWithRepetitionArbitraryTest::fromListToString).collect(toList());

      //then
      assertThat(samples.size()).isEqualTo(4);
      assertThat(samples).allMatch(allSamples::contains);
    }

    @Test
    @DisplayName("all samples: n=2, r=3")
    public void allPermutations_2_3() {

      //given
      List<String> population = new ArrayList<>() {{
        add("a");
        add("b");
      }};
      int r = 3;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allSamples = Arrays.asList(
        "aaa", "aab", "aba", "abb",
        "baa", "bab", "bba", "bbb"
      );

      //when
      List<String> samples = arbitrary.allValues().get().map(PermutationWithRepetitionArbitraryTest::fromListToString).collect(toList());

      //then
      assertThat(samples.size()).isEqualTo(8);
      assertThat(samples).allMatch(allSamples::contains);
    }

    @Test
    @DisplayName("all samples: n=3, r=2")
    public void allPermutations_3_2() {

      //given
      List<String> population = new ArrayList<>() {{
        add("a");
        add("b");
        add("c");
      }};
      int r = 2;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allSamples = Arrays.asList(
        "aa","ab","ac",
        "ba", "bb", "bc",
        "ca", "cb", "cc"
      );

      //when
      List<String> samples = arbitrary.allValues().get().map(PermutationWithRepetitionArbitraryTest::fromListToString).collect(toList());

      //then
      assertThat(samples.size()).isEqualTo(9);
      assertThat(samples).allMatch(allSamples::contains);
    }

    @Test
    @DisplayName("all samples: n=3, r=3")
    public void allPermutations_3_3() {

      //given
      List<String> population = new ArrayList<>() {{
        add("a");
        add("b");
        add("c");
      }};
      int r = 3;
      Arbitrary<List<String>> arbitrary = new PermutationWithRepetitionArbitrary<>(population, r);
      List<String> allSamples = Arrays.asList(
        "aaa", "aab", "aac", "aba", "abb", "abc", "aca", "acb", "acc",
        "baa", "bab", "bac", "bba", "bbb", "bbc", "bca", "bcb", "bcc",
        "caa", "cab", "cac", "cba", "cbb", "cbc", "cca", "ccb", "ccc"
      );

      //when
      List<String> samples = arbitrary.allValues().get().map(PermutationWithRepetitionArbitraryTest::fromListToString).collect(toList());

      //then
      assertThat(samples.size()).isEqualTo(27);
      assertThat(samples).allMatch(allSamples::contains);
    }
  }
}