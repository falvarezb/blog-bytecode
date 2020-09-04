package fjab.poset.iterator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EagerPermutationWithRepetitionIteratorTest {

  @Test
  @DisplayName("iteration over all permutations corresponding to: n=2, r=2")
  public void iteration_2_2() {

    //given
    List<String> population = new ArrayList<>() {{
      add("a");
      add("b");
    }};
    int r = 2;
    List<List<String>> allPossiblePermutations = Arrays.asList(
      Arrays.asList("a","a"),
      Arrays.asList("a","b"),
      Arrays.asList("b","a"),
      Arrays.asList("b","b")
    );

    //when
    EagerPermutationWithRepetitionIterator<String> iterator = new EagerPermutationWithRepetitionIterator<>(population, r);
    List<List<String>> allValues = new ArrayList<>();
    iterator.forEachRemaining(allValues::add);

    //then
    assertThat(allPossiblePermutations).containsExactlyInAnyOrderElementsOf(allValues);
  }

  @Test
  @DisplayName("iteration over all permutations corresponding to: n=2, r=3")
  public void iteration_2_3() {

    //given
    List<String> population = new ArrayList<>() {{
      add("a");
      add("b");
    }};
    int r = 3;
    List<List<String>> allPossiblePermutations = Arrays.asList(
      Arrays.asList("a","a","a"),Arrays.asList("a","a","b"),Arrays.asList("a","b","a"),Arrays.asList("a","b","b"),
      Arrays.asList("b","a","a"),Arrays.asList("b","a","b"),Arrays.asList("b","b","a"),Arrays.asList("b","b","b")
    );

    //when
    EagerPermutationWithRepetitionIterator<String> iterator = new EagerPermutationWithRepetitionIterator<>(population, r);
    List<List<String>> allValues = new ArrayList<>();
    iterator.forEachRemaining(allValues::add);

    //then
    assertThat(allPossiblePermutations).containsExactlyInAnyOrderElementsOf(allValues);
  }

  @Test
  @DisplayName("iteration over all permutations corresponding to: n=16, r=1")
  public void iteration_16_1() {

    //given
    List<String> population = new ArrayList<>() {{
      add("A");
      add("B");
      add("C");
      add("D");
      add("E");
      add("F");
      add("G");
      add("H");
      add("I");
      add("J");
      add("K");
      add("L");
      add("M");
      add("N");
      add("O");
      add("P");
    }};
    int r = 1;
    List<List<String>> allPossiblePermutations = Arrays.asList(
      singletonList("A"), singletonList("B"), singletonList("C"), singletonList("D"),
      singletonList("E"), singletonList("F"), singletonList("G"), singletonList("H"),
      singletonList("I"), singletonList("J"), singletonList("K"), singletonList("L"),
      singletonList("M"), singletonList("N"), singletonList("O"), singletonList("P")
    );

    //when
    EagerPermutationWithRepetitionIterator<String> iterator = new EagerPermutationWithRepetitionIterator<>(population, r);
    List<List<String>> allValues = new ArrayList<>();
    iterator.forEachRemaining(allValues::add);

    //then
    assertThat(allPossiblePermutations).isEqualTo(allValues);
  }

  @Test
  @DisplayName("error when trying to retrieve more elements after iterator is exhausted")
  public void errorWhenIteratorExhausted() {

    //given
    List<Integer> population = Arrays.asList(1,2);
    int r = 1;

    //when
    EagerPermutationWithRepetitionIterator<Integer> iterator = new EagerPermutationWithRepetitionIterator<>(population, r);
    iterator.next();
    iterator.next();

    //then
    assertThatThrownBy(iterator::next)
      .isInstanceOf(NoSuchElementException.class);
  }

}