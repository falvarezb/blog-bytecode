package fjab;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Permutation {

  int repetitions;
  long numPermutations;

  public Permutation(int repetitions) {
    this.repetitions = repetitions;
    numPermutations = (long) Math.pow(2, repetitions);
  }


  public static List<List<Integer>> permutationsWithRepetition(int[] elems, int repetitions) {
    if(repetitions == 0){
      return new ArrayList<>(){{
        add(new ArrayList<>());
      }};
    }

    List<List<Integer>> result = new ArrayList<>();

    for (int elem: elems) {
      for (List<Integer> list: permutationsWithRepetition(elems, repetitions-1)){
        list.add(elem);
        result.add(list);
      }
    }


    return result;
  }


  static class PermutationWithRepetitionSupplier implements Supplier<List<Integer>> {

    int i;
    int repetitions;
    PermutationWithRepetitionSupplier(int repetitions) {
      this.repetitions = repetitions;
    };

    @Override
    public List<Integer> get() {
      String permutation = String.format("%"+repetitions+"s" ,Integer.toBinaryString(i++)).replace(" ","0");
      return permutation.chars().mapToObj(c -> Integer.valueOf(String.valueOf((char) c))).collect(Collectors.toList());
    }
  }

  public Stream<List<Integer>> stream() {
    return generate(new PermutationWithRepetitionSupplier(repetitions), numPermutations);
  }

  private <T> Stream<T> generate(Supplier<T> s, long count) {
    return StreamSupport.stream(
      new Spliterators.AbstractSpliterator<T>(count, Spliterator.SIZED) {
        long remaining=count;

        public boolean tryAdvance(Consumer<? super T> action) {
          if(remaining<=0) return false;
          remaining--;
          action.accept(s.get());
          return true;
        }

        /** May improve the performance of most non-short-circuiting operations */
        @Override
        public void forEachRemaining(Consumer<? super T> action) {
          long toGo=remaining;
          remaining=0;
          for(; toGo>0; toGo--) action.accept(s.get());
        }
      }, false);
  }
}
