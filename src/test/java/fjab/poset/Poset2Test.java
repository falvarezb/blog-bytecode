package fjab.poset;

import fjab.poset.error.PosetException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static fjab.poset.Poset2.buildBinaryRelationsFromFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class Poset2Test {

  @Nested
  @DisplayName("poset construction failure")
  class PosetConstructionFailureTest {
    @Test
    @DisplayName("there must be NxN binary relations, where N is the number of elements")
    public void testMatchOfElementsWithBinaryRelations() {
      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset2<>(Arrays.asList("a", "b"), new int[][]{new int[]{1}}));
      assertEquals("there must be NxN binary relations, where N is the number of elements", e.getMessage());
    }

    @Test
    @DisplayName("the list of elements cannot contain duplicates")
    public void testNotDuplicateElements() {
      Exception e = assertThrows(IllegalArgumentException.class, () -> new Poset2<>(Arrays.asList("a", "b", "a"), new int[][]{
        {1,1,1},
        {0,1,1},
        {0,0,1}
      }));
      assertEquals("the list of elements cannot contain duplicates", e.getMessage());
    }
  }

  @Nested
  @DisplayName("equality test")
  class EqualityTest {
    @Test
    @DisplayName("2 Posets with the same constructor parameters are equal")
    public void testEqualPosetsWithSameConstructorParameters() {
      List<String> elements = Arrays.asList("a", "b", "c", "d");
      int[][] binaryRepresentation = new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      };

      Poset2<String> poset1 = new Poset2<>(elements, binaryRepresentation);
      Poset2<String> poset2 = new Poset2<>(elements, binaryRepresentation);
      assertEquals(poset1, poset2);
    }

    @Test
    @DisplayName("2 Posets are equal if they contain the same elements and have the same transitive expansion")
    public void testEqualPosetsWithSameElementsAndSameTransitiveExpansion() {
      List<String> elements1 = Arrays.asList("a", "b", "c", "d");
      List<String> elements2 = Arrays.asList("d", "b", "c", "a");
      int[][] binaryRepresentation1 = new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      };
      int[][] binaryRepresentation2 = new int[][]{
        {1, 1, 0, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 0, 0, 1}
      };

      Poset2<String> poset1 = new Poset2<>(elements1, binaryRepresentation1);
      Poset2<String> poset2 = new Poset2<>(elements2, binaryRepresentation2);
      assertEquals(poset1, poset2);
    }

    @Test
    @DisplayName("2 Posets with different elements are not equal")
    public void testPosetsWithDifferentElements() {
      List<String> elements1 = Arrays.asList("a", "b", "c", "d");
      List<String> elements2 = Arrays.asList("d", "b", "c", "x");
      int[][] binaryRepresentation = new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      };

      Poset2<String> poset1 = new Poset2<>(elements1, binaryRepresentation);
      Poset2<String> poset2 = new Poset2<>(elements2, binaryRepresentation);
      assertNotEquals(poset1, poset2);
    }

    @Test
    @DisplayName("2 Posets with different transitive expansion are not equal")
    public void testPosetsWithDifferentTransitiveExpansion() {
      List<String> elements = Arrays.asList("a", "b", "c", "d");
      int[][] binaryRepresentation1 = new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      };
      int[][] binaryRepresentation2 = new int[][]{
        {1, 1, 1, 0},
        {0, 1, 0, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      };

      Poset2<String> poset1 = new Poset2<>(elements, binaryRepresentation1);
      Poset2<String> poset2 = new Poset2<>(elements, binaryRepresentation2);
      assertNotEquals(poset1, poset2);
    }
  }


  @ParameterizedTest
  @MethodSource("sortSupplier")
  @DisplayName("topological sort of poset")
  public void testSort(String fileName, List<String> elements, List<String> elementsInExpectedOrder) throws IOException, PosetException {
    assertIterableEquals(elementsInExpectedOrder, new Poset2<>(elements, buildBinaryRelationsFromFile(Paths.get(fileName))));
  }

  static Stream<Arguments> sortSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt", Arrays.asList("a", "b", "c", "d"), Arrays.asList("d", "a", "b", "c")),
      arguments("src/test/resources/poset_success_2.txt", Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"), Arrays.asList("b", "a", "c", "d", "e", "f", "g", "h")),
      arguments("src/test/resources/poset_success_3.txt", Arrays.asList("a", "b", "c", "d", "e"), Arrays.asList("e", "d", "c", "b", "a"))
    );
  }

  @ParameterizedTest
  @MethodSource("transitiveExpansionSupplier")
  @DisplayName("transitive expansion")
  public void testTransitiveExpansion(String fileName, List<String> elements, int[][] array) throws IOException, PosetException {
    Poset2 poset = new Poset2<>(elements, buildBinaryRelationsFromFile(Paths.get(fileName)));
    assertArrayEquals(array, poset.getExpandedBinaryRelations());
  }

  static Stream<Arguments> transitiveExpansionSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt",
        Arrays.asList("a", "b", "c", "d"),
        new int[][]{
        {1, 1, 1, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 1, 1, 1}
      }),
      arguments("src/test/resources/poset_success_2.txt",
        Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"),
        new int[][]{
        {1, 0, 0, 1, 0, 1, 1, 1},
        {0, 1, 0, 1, 1, 1, 1, 1},
        {0, 0, 1, 0, 1, 0, 1, 1},
        {0, 0, 0, 1, 0, 1, 1, 1},
        {0, 0, 0, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 1}
      }),
      arguments("src/test/resources/poset_success_3.txt",
        Arrays.asList("a", "b", "c", "d", "e"),
        new int[][]{
        {1, 0, 0, 0, 0},
        {1, 1, 0, 0, 0},
        {1, 1, 1, 0, 0},
        {1, 1, 1, 1, 0},
        {1, 1, 1, 1, 1}
      })
    );
  }

  @ParameterizedTest
  @MethodSource("transitiveReductionSupplier")
  @DisplayName("transitive reduction")
  public void testTransitiveReduction(String fileName, List<String> elements, int[][] array) throws IOException, PosetException {
    Poset2 poset = new Poset2<>(elements, buildBinaryRelationsFromFile(Paths.get(fileName)));
    assertArrayEquals(array, poset.getReducedBinaryRelations());
  }

  static Stream<Arguments> transitiveReductionSupplier() {
    return Stream.of(
      arguments("src/test/resources/poset_success_1.txt",
        Arrays.asList("a", "b", "c", "d"),
        new int[][]{
        {1, 1, 0, 0},
        {0, 1, 1, 0},
        {0, 0, 1, 0},
        {1, 0, 0, 1}
      }),
      arguments("src/test/resources/poset_success_2.txt",
        Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"),
        new int[][]{
        {1, 0, 0, 1, 0, 0, 0, 0},
        {0, 1, 0, 1, 1, 0, 0, 0},
        {0, 0, 1, 0, 1, 0, 0, 1},
        {0, 0, 0, 1, 0, 1, 1, 1},
        {0, 0, 0, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 1}
      }),
      arguments("src/test/resources/poset_success_3.txt",
        Arrays.asList("a", "b", "c", "d", "e"),
        new int[][]{
        {1, 0, 0, 0, 0},
        {1, 1, 0, 0, 0},
        {0, 1, 1, 0, 0},
        {0, 0, 1, 1, 0},
        {0, 0, 0, 1, 1}
      })
    );
  }
}