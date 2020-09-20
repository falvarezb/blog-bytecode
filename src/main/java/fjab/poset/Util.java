package fjab.poset;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Util {

  /**
   * Pads the given string with a char (multiple times, if needed) until the resulting string reaches
   * the given length. The padding is applied to the left.
   */
  public static String leftPad(String str, int length, char paddingChar) {
    return String.format("%"+ length +"s" ,str).replace(' ' ,paddingChar);
  }

  /**
   * Given a String representing a numeric value, this method returns a Stream of digits
   *
   * Example:
   * "0123" => Stream(0,1,2,3)
   *
   */
  @SuppressWarnings("unused")
  public static Stream<Integer> stringToIntegerStream(String str) {
    return str.chars().mapToObj(c -> Integer.valueOf(String.valueOf((char) c)));
  }

  /**
   * Returns the string given as parameter as a Stream of characters
   *
   * Example:
   * "abcd" => Stream('a','b','c','d')
   *
   */
  public static Stream<Character> stringToStream(String str) {
    return str.chars().mapToObj(c -> (char) c);
  }

  /**
   * Returns the single string resulting of concatenating all strings in the given list
   *
   * Example:
   * List("a","b","c","d") => "abcd"
   */
  public static String listToString(List<String> list) {
    return list.stream().reduce("", (a, b) -> a + b);
  }

  public static int[][] arrayDeepCopy(int[][] array) {
    int[][] newArray = new int[array.length][];
    for (int j = 0; j < array.length; j++) {
      newArray[j] = Arrays.copyOf(array[j], array[j].length);
    }
    return newArray;
  }

  /**
   * Returns the sum of all elements in the given array
   */
  public static int sum(int[][] arr) {
    return Arrays.stream(arr).flatMapToInt(Arrays::stream).sum();
  }

  public static boolean isSquareMatrix(int[][] arr) {
    return Arrays.stream(arr).mapToInt(row -> row.length).filter(size -> size != arr.length).toArray().length == 0;
  }

  /**
   * Returns the indexes of the rows sorted by the sum of its elements in ascending order
   *
   * Example
   * =======
   * Given:
   * [[1,2,0],
   * [0,2,0],
   * [1,1,2]]
   *
   * returns:
   * [1,0,2]
   *
   */
  static int[] sort(int[][] array) {

    class Row {
      private final int idx;
      private final int sum;

      Row(int[] row, int idx) {
        this.idx = idx;
        this.sum = Arrays.stream(row).sum();
      }
    }

    Row[] rows = new Row[array.length];
    for (int i = 0; i < array.length; i++) {
      rows[i] = new Row(array[i], i);
    }
    Arrays.sort(rows, (o1, o2) -> o2.sum - o1.sum);
    return Arrays.stream(rows).mapToInt(row -> row.idx).toArray();
  }
}
