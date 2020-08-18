package fjab;

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
  public static Stream<Integer> stringToIntegerStream(String str) {
    return str.chars().mapToObj(c -> Integer.valueOf(String.valueOf((char) c)));
  }

  /**
   * Given a String, this method returns a Stream of the chars of that String
   *
   * Example:
   * "abcd" => Stream('a','b','c','d')
   *
   */
  public static Stream<Character> stringToStream(String str) {
    return str.chars().mapToObj(c -> (char) c);
  }
}
